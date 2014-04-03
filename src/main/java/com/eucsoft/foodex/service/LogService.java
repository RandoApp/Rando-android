package com.eucsoft.foodex.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eucsoft.foodex.task.SendLogTask;

public class LogService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        setTimeout(AlarmManager.INTERVAL_DAY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new SendLogTask().execute();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setTimeout(long time) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, time, createIntent());
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(getApplicationContext(), LogService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        return pendingIntent;
    }
}
