package com.eucsoft.foodex.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.service.SyncService;

public class Notification {

    public static void show() {
        //TODO: Remove SyncService.NOTIFICATION
        Intent intent = new Intent(SyncService.NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) App.context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }
}
