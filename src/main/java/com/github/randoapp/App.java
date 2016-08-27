package com.github.randoapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.RandoFirebaseInstanceIdService;
import com.github.randoapp.service.RandoMessagingService;
import com.github.randoapp.service.UploadService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startServices();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Preferences.setFirebaseInstanceId(FirebaseInstanceId.getInstance().getToken());
            Log.i(App.class,  "Firebase ID: " + FirebaseInstanceId.getInstance().getToken());
        }
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    private void startServices() {
        //App onCreate called twice. Prevent double service run, if it is already created
            if (!UploadService.isRunning()) {
                startService(new Intent(context, RandoFirebaseInstanceIdService.class));
                startService(new Intent(context, RandoMessagingService.class));
                startService(new Intent(getApplicationContext(), UploadService.class));
        }
    }
}
