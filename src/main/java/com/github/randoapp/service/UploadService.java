package com.github.randoapp.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;

import java.io.File;
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
        uploadFiles();
        return Service.START_NOT_STICKY;
    }

    private void uploadFiles() {
        RandoDAO randoDAO = new RandoDAO(getApplicationContext());
        List<RandoUpload> randosToUpload = randoDAO.getAllRandosToUpload();
        for (RandoUpload randoUpload: randosToUpload) {
            try {
                API.uploadImage(new File(randoUpload.file), getLocation(randoUpload));
            } catch (Exception e) {
                Log.w(UploadService.class, "Can not upload image, because: ", e.getMessage());
                sleep();
                return;
            }
        }
    }

    private Location getLocation(RandoUpload randoUpload) {
        Location location = new Location("Rando4Me.UploadService");
        location.setLatitude(Double.parseDouble(randoUpload.latitude));
        location.setLongitude(Double.parseDouble(randoUpload.longitude));
        return location;
    }

    private void sleep() {
        //TODO: implement sleep method
    }

}
