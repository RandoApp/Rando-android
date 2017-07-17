package com.github.randoapp;

import android.app.Application;
import android.content.Context;

import com.evernote.android.job.JobManager;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.upload.UploadJobCreator;
import com.github.randoapp.upload.UploadJobScheduler;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;


@ReportsCrashes(
        formUri = "https://reports.rando4.me/" + BuildConfig.RANDO_REPORTS_ACRA_DB + "/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.PUT,
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

    private void startServices() {
        JobManager.create(this).addJobCreator(new UploadJobCreator());
        if (RandoDAO.getNextRandoToUpload() != null) {
            UploadJobScheduler.scheduleUpload(getApplicationContext());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
