package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;

import com.eucsoft.foodex.service.LogService;
import com.eucsoft.foodex.service.SyncService;

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

        if (!LogService.isRunning()) {
            LogService.run();
        }
    }

    private void startLogging() {
        ACRA.init(this);
    }

}
