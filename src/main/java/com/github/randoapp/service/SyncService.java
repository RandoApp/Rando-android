package com.github.randoapp.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.log.Log;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.task.SyncTask;

import java.util.List;
import java.util.Map;

import static com.github.randoapp.Constants.NEED_NOTIFICATION;
import static com.github.randoapp.Constants.NOT_PAIRED_RANDO_PAIRS_NUMBER;
import static com.github.randoapp.Constants.SERVICE_LONG_PAUSE;
import static com.github.randoapp.Constants.SERVICE_SHORT_PAUSE;

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
            public void onFetch(final List<RandoPair> randoPairs) {
                Log.i(SyncService.class, "Fetched ", String.valueOf(randoPairs.size()), " randoPairs");
                new SyncTask(randoPairs)
                        .onOk(new OnOk() {
                            @Override
                            public void onOk(Map<String, Object> data) {
                                if (data.get(NEED_NOTIFICATION) != null) {
                                    sendNotification(randoPairs.size());
                                }
                                if ((Integer) data.get(NOT_PAIRED_RANDO_PAIRS_NUMBER) > 0)
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

    private void sendNotification(int randoPairsNumber) {
        Intent intent = new Intent(Constants.SYNC_SERVICE_BROADCAST);
        intent.putExtra(Constants.RANDO_PAIRS_NUMBER, randoPairsNumber);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) App.context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        Log.i(SyncService.class, "Update broadcast sent.");
    }

}
