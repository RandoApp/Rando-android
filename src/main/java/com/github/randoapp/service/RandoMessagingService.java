package com.github.randoapp.service;

import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
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
        Log.d(RandoMessagingService.class, "Firebase From: " + remoteMessage.getFrom() +"Firebase Notification Message Body: " + remoteMessage.getData().toString());

        Map<String,String> data = remoteMessage.getData();
        if (data != null){
            String notificationType = data.get("notificationType");
            if (notificationType != null){
                if(Constants.PUSH_NOTIFICATION_LANDED.equals(notificationType)){
                    API.syncUserAsync(null);
                } else if (Constants.PUSH_NOTIFICATION_RECEIVED.equals(notificationType)) {
                    String randoString = data.get("rando");
                    if (randoString != null) {
                        Rando rando = RandoUtil.parseRando(randoString, Rando.Status.IN);
                        if (rando!=null && RandoDAO.getRandoByRandoId(rando.randoId) != null){
                            RandoDAO.createRando(rando);
                            Log.d(RandoMessagingService.class, "Inserting newly Recieved Rando"+randoString);
                        }
                    }
                }
            }
        }
    }
}
