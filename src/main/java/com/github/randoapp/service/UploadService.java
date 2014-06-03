package com.github.randoapp.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    private void getFilesToUpload() {
        RandoDAO randoDAO = new RandoDAO(getApplicationContext());
        List<RandoUpload> randosToUpload = randoDAO.getAllRandosToUpload();
        for (RandoUpload randoUpload: randosToUpload) {
            Location location = new Location();
            API.uploadImage(new File(randoUpload.file, );
        }

        //Run SyncService
    }

}
