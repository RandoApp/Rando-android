package com.github.randoapp.service;

import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class RandoFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.i(RandoFirebaseInstanceIdService.class,  "Firebase ID Updated: " + FirebaseInstanceId.getInstance().getToken());
        Preferences.setFirebaseInstanceId(FirebaseInstanceId.getInstance().getToken());
    }
}
