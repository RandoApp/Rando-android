package com.github.randoapp.service;

import com.github.randoapp.log.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class RandoMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(RandoMessagingService.class, "Firebase RandoMessage: "+ remoteMessage.toString());
        Log.d(RandoMessagingService.class, "Firebase From: " + remoteMessage.getFrom());
        Log.d(RandoMessagingService.class, "Firebase Notification Message Body: " + remoteMessage.getData().toString());
    }
}
