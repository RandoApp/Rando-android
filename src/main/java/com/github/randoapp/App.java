package com.github.randoapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.github.randoapp.log.Log;
import com.github.randoapp.service.RandoFirebaseInstanceIdService;
import com.github.randoapp.service.RandoMessagingService;
import com.github.randoapp.service.UploadService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;

import static com.github.randoapp.Constants.LOG_URL;

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startServices();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Log.i(App.class,  "Firebase ID: " + FirebaseInstanceId.getInstance().getToken());
            Log.i(App.class,  "Firebase App: " + FirebaseApp.getInstance());
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            if (!ACRA.isACRASenderServiceProcess()) {
                final ACRAConfiguration config = new ConfigurationBuilder(this)
                        .setFormUri(LOG_URL)
                        .setReportType(HttpSender.Type.JSON)
                        .setReportingInteractionMode(ReportingInteractionMode.TOAST)
                        .setCustomReportContent(ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE)
                        .setResToastText(R.string.crash_toast_text)
                        .build();
                ACRA.init(this, config);
            }
        } catch (ACRAConfigurationException e) {
            e.printStackTrace();
        }
    }
}
