package com.eucsoft.foodex.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.api.OnFetchUser;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.OnOk;
import com.eucsoft.foodex.task.SyncTask;

import java.util.List;
import java.util.Map;

import static com.eucsoft.foodex.Constants.NEED_NOTIFICATION;
import static com.eucsoft.foodex.Constants.SERVICE_LONG_PAUSE;
import static com.eucsoft.foodex.Constants.SERVICE_SHORT_PAUSE;

public class SyncService extends Service {

    public static void run() {
        Intent syncService = new Intent(App.context, SyncService.class);
        App.context.startService(syncService);
    }

    public static boolean isRunning() {
        ActivityManager manager = (ActivityManager) App.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SyncService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInterval(SERVICE_LONG_PAUSE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(SyncService.class, "onStartCommand");

        API.fetchUserAsync(new OnFetchUser() {
            @Override
            public void onFetch(List<FoodPair> foodPairs) {
            new SyncTask(foodPairs)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    if (data.get(NEED_NOTIFICATION) != null) {
                        sendNotification();
                    }
                    setTimeout(System.currentTimeMillis() + SERVICE_SHORT_PAUSE);
                }
            })
            .execute();
            }
        });

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void setTimeout(long time) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, time, createIntent());
    }

    private void setInterval(long time) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, time, createIntent());
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(getApplicationContext(), SyncService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        return pendingIntent;
    }

    private void sendNotification() {
        Intent intent = new Intent(Constants.SYNC_SERVICE_BROADCAST);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) App.context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

}
