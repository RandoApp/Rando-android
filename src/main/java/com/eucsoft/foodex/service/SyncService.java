package com.eucsoft.foodex.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.api.FetchUserListener;
import com.eucsoft.foodex.autoinstall.APKGithubInstaller;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.SyncAllTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SyncService extends Service {

    public static final String NOTIFICATION = "SyncService";
    private static final long SHORT_PAUSE = 30 * 1000;
    private static final long LONG_PAUSE = AlarmManager.INTERVAL_HALF_HOUR;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(SyncService.class, "onStartCommand");

        try {
            API.fetchUser(new FetchUserListener() {
                @Override
                public void userFetched(List<FoodPair> foodPairs) {
                    Log.v(FetchUserListener.class, "foosPairsRecieved");
                    FoodDAO foodDAO = new FoodDAO(App.context);

                    boolean updated = false;

                    if (foodPairs.size() != foodDAO.getFoodPairsNumber()) {
                        foodDAO.clearFoodPairs();
                        foodDAO.insertFoodPairs(foodPairs);
                        sendUpdatedNotification();
                        updated = true;
                    }

                    List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
                    Collections.sort(foodPairs);

                    for (int i = 0; i < dbFoodPairs.size(); i++) {
                        if (!dbFoodPairs.get(i).equals(foodPairs.get(i))) {
                            foodDAO.clearFoodPairs();
                            foodDAO.insertFoodPairs(foodPairs);
                            sendUpdatedNotification();
                            updated = true;
                        }
                    }

                    if (foodDAO.getNotPairedFoodsNumber() > 0) {
                        setAlarm(System.currentTimeMillis() + SHORT_PAUSE);
                        Log.i(SyncService.class, "Service will start in " + SHORT_PAUSE / 1000 + " seconds");
                    } else {
                        setAlarm(System.currentTimeMillis() + LONG_PAUSE);
                        Log.i(SyncService.class, "Service will start in " + LONG_PAUSE / 1000 + " seconds");
                    }

                    foodDAO.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*SyncAllTask syncAllTask = new SyncAllTask();
        syncAllTask.setTaskResultListener(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                switch ((int) resultCode) {
                    case (int) SyncAllTask.FOOD_PAIRS_UPDATED:
                        sendUpdatedNotification();
                        break;
                    case (int) SyncAllTask.RESULT_ERROR:
                        //sendUpdatedNotification();
                        Log.e(SyncService.class, "SyncAllTask errored.");
                        break;
                    case (int) SyncAllTask.NOT_UPDATED:
                        //sendUpdatedNotification();
                        break;
                }
                FoodDAO foodDAO = new FoodDAO(getApplicationContext());
                if (foodDAO.getNotPairedFoodsNumber() > 0) {
                    setAlarm(System.currentTimeMillis() + SHORT_PAUSE);
                    Log.i(SyncService.class, "Service will start in " + SHORT_PAUSE / 1000 + " seconds");
                } else {
                    setAlarm(System.currentTimeMillis() + LONG_PAUSE);
                    Log.i(SyncService.class, "Service will start in " + LONG_PAUSE / 1000 + " seconds");
                }
                foodDAO.close();
            }
        });
        syncAllTask.execute();*/
        /*new APKGithubInstaller("xp-vit/foodex-android").update();*/
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void sendUpdatedNotification() {
        Intent intent = new Intent(NOTIFICATION);
        PendingIntent startPIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startPIntent);
    }

    private void setAlarm(long time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SyncService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, time, pintent);
    }


}
