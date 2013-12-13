package com.eucsoft.foodex.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.service.listener.SyncAllListener;

public class SyncService extends Service {

    public static final String NOTIFICATION = "SyncService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(SyncService.class, "onStartCommand");
        API.fetchUserAsync(new SyncAllListener(this));
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void setAlarm(long time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SyncService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, time, pintent);
    }

}
