package com.eucsoft.foodex.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.SendLogTask;

import static com.eucsoft.foodex.Constants.LOG_SRVICE_INTERVAL;

public class LogService extends Service {

    public static void run() {
        Intent logService = new Intent(App.context, LogService.class);
        App.context.startService(logService);
    }

    public static boolean isRunning() {
        ActivityManager manager = (ActivityManager) App.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LogService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LogService.class, "LogService created");
        setInterval(LOG_SRVICE_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LogService.class, "LogService wakeup and start command");
        new SendLogTask().execute();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setInterval(long time) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, time, createIntent());
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(getApplicationContext(), LogService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        return pendingIntent;
    }
}
