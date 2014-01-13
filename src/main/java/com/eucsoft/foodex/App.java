package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;

import com.eucsoft.foodex.service.SyncService;

public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startService();
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    private void startService() {
        //App onCreate called twice. Prevent double service run, if it is already created
        if (!SyncService.isRunning()) {
            SyncService.run();
        }
    }


}
