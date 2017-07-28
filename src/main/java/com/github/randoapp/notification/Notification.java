package com.github.randoapp.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.github.randoapp.Constants;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;

public class Notification {

    private static final int LED_MS_TO_BE_ON = 3000;
    private static final int LED_MS_TO_BE_OFF = 3000;

    public static void show(Context context, String title, String text) {
        Log.d(Notification.class, "Show with following params: title -> " + title + " text -> " + text);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setLights(Color.RED, LED_MS_TO_BE_ON, LED_MS_TO_BE_OFF);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    public static void sendSyncNotification(Context context, int randosNumber, String updateStatus) {
        Intent intent = new Intent(Constants.SYNC_BROADCAST_EVENT);
        intent.putExtra(Constants.TOTAL_RANDOS_NUMBER, randosNumber);
        intent.putExtra(Constants.UPDATE_STATUS, updateStatus);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }
}
