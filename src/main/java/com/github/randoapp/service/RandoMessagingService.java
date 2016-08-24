package com.github.randoapp.service;

import com.github.randoapp.Constants;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
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
            String randoString = data.get("rando");
            if (notificationType != null && randoString != null) {
                Rando rando = null;
                if (Constants.PUSH_NOTIFICATION_RECEIVED.equals(notificationType)) {
                    rando = RandoUtil.parseRando(randoString, Rando.Status.IN);
                } else if (Constants.PUSH_NOTIFICATION_LANDED.equals(notificationType)) {
                    rando = RandoUtil.parseRando(randoString, Rando.Status.OUT);
                }
                if (rando != null) {
                    RandoDAO.createOrUpdateRandoCheckingByRandoId(rando);
                    Log.d(RandoMessagingService.class, "Inserting/Updating newly Received Rando" + rando.toString());
                }
            }
        }
    }
}
