package com.eucsoft.foodex.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.SyncAllTask;

import java.util.HashMap;

public class SyncService extends Service {

    public static final String NOTIFICATION = "SyncService";
    private static final long SHORT_PAUSE = 2 * 60 * 1000;
    private static final long LONG_PAUSE = 2 * 60 * 60 * 1000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(SyncService.class, "onStartCommand");
        SyncAllTask syncAllTask = new SyncAllTask();
        //removeAlarm();
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
                    Log.i(SyncService.class, "Server will start in " + SHORT_PAUSE / 1000 + " seconds");
                } else {
                    setAlarm(System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_HOUR);
                    Log.i(SyncService.class, "Server will start in " + AlarmManager.INTERVAL_HALF_HOUR / 1000 + " seconds");
                }
            }
        });
        syncAllTask.execute();
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

    private void removeAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SyncService.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        am.cancel(pintent);
        pintent.cancel();
    }

    private void setAlarm(long time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SyncService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, time, pintent);
    }


}
