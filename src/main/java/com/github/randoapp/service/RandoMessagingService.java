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
        processMessage(data);
    }

    public void processMessage(Map<String, String> data) {
        if (data != null) {
            String notificationType = data.get(Constants.NOTIFICATION_TYPE_PARAM);
            String randoString = data.get(Constants.RANDO_PARAM);
            if (notificationType != null && randoString != null) {
                Rando rando = null;
                int notificationTextResId = 0;
                if (Constants.PUSH_NOTIFICATION_RECEIVED.equals(notificationType)) {
                    rando = Rando.fromJSON(randoString, Rando.Status.IN);
                    notificationTextResId = R.string.rando_received;
                } else if (Constants.PUSH_NOTIFICATION_LANDED.equals(notificationType)) {
                    rando = Rando.fromJSON(randoString, Rando.Status.OUT);
                    notificationTextResId = R.string.rando_landed;
                } else if (Constants.PUSH_NOTIFICATION_RATED.equals(notificationType)) {
                    rando = Rando.fromJSON(randoString, Rando.Status.OUT);
                    if (rando != null) {
                        switch (rando.rating) {
                            case 3:
                                notificationTextResId = R.string.rando_liked;
                                break;
                            case 2:
                                notificationTextResId = R.string.rando_rated;
                                break;
                            case 1:
                                notificationTextResId = R.string.rando_disliked;
                                break;
                            default:
                                notificationTextResId = R.string.rando_rated;
                                break;
                        }
                    }

                }
                if (rando != null) {
                    boolean shouldSendNotification = RandoUtil.isRatedFirstTime(rando.randoId, getBaseContext());
                    RandoDAO.createOrUpdateRandoCheckingByRandoId(getBaseContext(), rando);
                    Log.d(RandoMessagingService.class, "Inserting/Updating newly Received Rando" + rando.toString());
                    if (shouldSendNotification) {
                        Notification.show(this, getResources().getString(R.string.app_name), getResources().getString(notificationTextResId), rando);
                    }
                }
            }
            Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
            sendBroadcast(intent);
        }
    }
}
