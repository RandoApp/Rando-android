package com.github.randoapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.github.randoapp.App;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;

public class Notification {

    public static void show(String title, String text) {
        Intent notificationIntent = new Intent(App.context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(App.context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(App.context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        notification.setLights(Color.BLUE, 500, 500);
        notification.setStyle(new NotificationCompat.InboxStyle());

        NotificationManager manager = (NotificationManager) App.context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification.build());
    }
}
