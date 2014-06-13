package com.github.randoapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.github.randoapp.service.SyncService;
import com.github.randoapp.service.UploadService;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(formKey="",
        formUri = Constants.LOG_URL,
        reportType = HttpSender.Type.JSON,
        mode = ReportingInteractionMode.TOAST,
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE},
        resToastText = R.string.crash_toast_text)
public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startLogging();
        startServices();
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    private void startServices() {
        //App onCreate called twice. Prevent double service run, if it is already created
        if (!SyncService.isRunning()) {
            SyncService.run();
            startService(new Intent(context, UploadService.class));
        }
    }

    private void startLogging() {
        ACRA.init(this);
    }

}
