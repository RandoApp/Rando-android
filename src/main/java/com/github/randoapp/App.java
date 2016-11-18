package com.github.randoapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.evernote.android.job.JobManager;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.upload.UploadJobCreator;
import com.github.randoapp.upload.UploadServiceLegacy;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import static org.acra.sender.HttpSender.Method;
import static org.acra.sender.HttpSender.Type;


@ReportsCrashes(
        formUri = "https://reports.rando4.me/" + BuildConfig.RANDO_REPORTS_ACRA_DB + "/_design/acra-storage/_update/report",
        reportType = Type.JSON,
        httpMethod = Method.PUT,
        formUriBasicAuthLogin = BuildConfig.RANDO_REPORTS_USER,
        formUriBasicAuthPassword = BuildConfig.RANDO_REPORTS_PASSWORD,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startServices();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Preferences.setFirebaseInstanceId(FirebaseInstanceId.getInstance().getToken());
            Log.i(App.class, "Firebase ID: " + FirebaseInstanceId.getInstance().getToken());
        }
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    private void startServices() {
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            startService(new Intent(getApplicationContext(), UploadServiceLegacy.class));
        } else {
            JobManager.create(this).addJobCreator(new UploadJobCreator());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
