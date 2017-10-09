package com.github.randoapp;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.upload.UploadJobCreator;
import com.github.randoapp.upload.UploadJobScheduler;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startServices();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Preferences.setFirebaseInstanceId(getBaseContext(), FirebaseInstanceId.getInstance().getToken());
            Log.i(App.class, "Firebase ID: " + FirebaseInstanceId.getInstance().getToken());
        }
    }

    private void startServices() {
        JobManager.create(this).addJobCreator(new UploadJobCreator());
        if (RandoDAO.getNextRandoToUpload(getBaseContext()) != null) {
            UploadJobScheduler.scheduleUpload(getApplicationContext());
        }
    }
}
