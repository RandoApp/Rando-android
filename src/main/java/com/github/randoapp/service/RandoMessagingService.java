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

import static com.github.randoapp.Constants.RANDO_ID_PARAM;
import static com.github.randoapp.Constants.STATISTICS_PARAM;

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
            Intent intent = new Intent(Constants.PUSH_NOTIFICATION_BROADCAST_EVENT);
            if (notificationType != null && randoString != null) {
                Rando rando = null;
                boolean shouldSendNotification = true;
                int notificationTextResId = 0;
                if (Constants.PUSH_NOTIFICATION_RECEIVED.equals(notificationType)) {
                    rando = Rando.fromJSON(randoString, Rando.Status.IN);
                    notificationTextResId = R.string.rando_received;
                } else if (Constants.PUSH_NOTIFICATION_LANDED.equals(notificationType)) {
                    rando = Rando.fromJSON(randoString, Rando.Status.OUT);
                    notificationTextResId = R.string.rando_landed;
                    intent.putExtra(RANDO_ID_PARAM, rando.randoId);
                } else if (Constants.PUSH_NOTIFICATION_RATED.equals(notificationType)) {
                    rando = Rando.fromJSON(randoString, Rando.Status.OUT);
                    intent.putExtra(RANDO_ID_PARAM, rando.randoId);
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
                        shouldSendNotification = RandoUtil.isRatedFirstTime(rando.randoId, getBaseContext());
                        intent.putExtra(STATISTICS_PARAM, shouldSendNotification);
                    }

                }
                if (rando != null) {
                    RandoDAO.createOrUpdateRandoCheckingByRandoId(getBaseContext(), rando);
                    Log.d(RandoMessagingService.class, "Inserting/Updating newly Received Rando" + rando.toString());
                    if (shouldSendNotification) {
                        Notification.show(this, getResources().getString(R.string.app_name), getResources().getString(notificationTextResId), rando);
                    }
                }
            }
            sendBroadcast(intent);
        }
    }
}
