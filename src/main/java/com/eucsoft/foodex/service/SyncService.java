package com.eucsoft.foodex.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.SyncAllTask;

import java.util.HashMap;

public class SyncService extends Service {

    public static final String NOTIFICATION = "SyncService";

    private final IBinder mBinder = new MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SyncAllTask syncAllTask = new SyncAllTask();
        removeAlarm();
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
            }
        });
        syncAllTask.execute();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        SyncService getService() {
            return SyncService.this;
        }
    }

    private void sendUpdatedNotification() {
        Intent intent = new Intent(NOTIFICATION);
        PendingIntent startPIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startPIntent);
        /*sendBroadcast(intent);*/
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
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, pintent);
    }


}
