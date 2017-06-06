package com.github.randoapp.service;

import android.content.Intent;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.notification.Notification;
import com.github.randoapp.util.RandoUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class RandoMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(RandoMessagingService.class, "Firebase From: " + remoteMessage.getFrom() + "Firebase Notification Message Body: " + remoteMessage.getData().toString());

        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            String notificationType = data.get("notificationType");
            String randoString = data.get(Constants.RANDO_PARAM);
            if (notificationType != null && randoString != null) {
                Rando rando = null;
                int notificationTextResId = 0;
                if (Constants.PUSH_NOTIFICATION_RECEIVED.equals(notificationType)) {
                    rando = RandoUtil.parseRando(randoString, Rando.Status.IN);
                    notificationTextResId = R.string.rando_received;
                } else if (Constants.PUSH_NOTIFICATION_LANDED.equals(notificationType)) {
                    rando = RandoUtil.parseRando(randoString, Rando.Status.OUT);
                    notificationTextResId = R.string.rando_landed;
                }
                if (rando != null) {
                    RandoDAO.createOrUpdateRandoCheckingByRandoId(rando);
                    Notification.show(this, getResources().getString(R.string.app_name), getResources().getString(notificationTextResId), rando);
                    Log.d(RandoMessagingService.class, "Inserting/Updating newly Received Rando" + rando.toString());
                }
            }
            Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
            sendBroadcast(intent);
        }
    }
}
