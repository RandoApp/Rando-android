package com.github.randoapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import com.github.randoapp.api.API;
import com.github.randoapp.api.exception.ForbiddenException;
import com.github.randoapp.api.exception.RequestTooLongException;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.ConnectionUtil;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.github.randoapp.Constants.UPLOAD_SERVICE_ATTEMPTS_FAIL;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_FORBIDDEN_PAUSE;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_INTERVAL;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_LONG_PAUSE;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_MANY_ATTEMPTS_FAIL;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_SHORT_PAUSE;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_VERY_LONG_PAUSE;

public class UploadService extends Service {

    private int uploadAttemptsFail = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInterval(UPLOAD_SERVICE_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uploadFiles();
        return Service.START_NOT_STICKY;
    }

    private void uploadFiles() {
        Toast.makeText(getApplicationContext(), "Upload service", Toast.LENGTH_LONG).show();

        if (!ConnectionUtil.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            uploadAttemptsFail++;
            sleep();
            return;
        }

        RandoDAO randoDAO = new RandoDAO(getApplicationContext());
        List<RandoUpload> randosToUpload = randoDAO.getAllRandosToUpload();
        randoDAO.close();

        Toast.makeText(getApplicationContext(), "Upload service upload randos: " + randosToUpload.size(), Toast.LENGTH_LONG).show();

        for (RandoUpload rando : randosToUpload) {
            boolean isUploaded = uploadFile(rando);
            if (!isUploaded) {
                break;
            }
        }
        Toast.makeText(getApplicationContext(), "Upload service FINISH", Toast.LENGTH_LONG).show();
    }

    private boolean uploadFile(RandoUpload rando) {
        try {
            Toast.makeText(getApplicationContext(), "Upload service try upload", Toast.LENGTH_LONG).show();
            Location location = getLocation(rando);
            File image = new File(rando.file);
            API.uploadImage(image, location);
            deleteRando(rando);
            uploadAttemptsFail = 0;

            Toast.makeText(getApplicationContext(), "Upload service UPLOADED", Toast.LENGTH_LONG).show();



            return true;
        } catch (RequestTooLongException e) {
            //TODO: say user, that his image too long and delete this image from DB
            Toast.makeText(getApplicationContext(), "too long request", Toast.LENGTH_LONG).show();
            deleteRando(rando);
            setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
        } catch (ForbiddenException e) {
            //TODO: say user that his banned
            Toast.makeText(getApplicationContext(), "Forbidden", Toast.LENGTH_LONG).show();
            setTimeout(UPLOAD_SERVICE_FORBIDDEN_PAUSE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Can not upload image", Toast.LENGTH_LONG).show();
            Log.w(UploadService.class, "Can not upload image, because: ", e.getMessage());
            uploadAttemptsFail++;
            sleep();
        }

        return false;
    }

    private void deleteRando(RandoUpload rando) {
        RandoDAO randoDAO = new RandoDAO(getApplicationContext());
        randoDAO.deleteRandoToUpload(rando);
        randoDAO.close();

        File image = new File(rando.file);
        image.delete();
    }

    private Location getLocation(RandoUpload randoUpload) {
        Location location = new Location("Rando4Me.UploadService");
        location.setLatitude(Double.parseDouble(randoUpload.latitude));
        location.setLongitude(Double.parseDouble(randoUpload.longitude));
        return location;
    }

    private void sleep() {
        if (uploadAttemptsFail >= UPLOAD_SERVICE_MANY_ATTEMPTS_FAIL) {
            Toast.makeText(getApplicationContext(), "attempts: " + String.valueOf(uploadAttemptsFail)  + " very long sleep", Toast.LENGTH_LONG).show();
            setTimeout(UPLOAD_SERVICE_VERY_LONG_PAUSE);
            return;
        }

        if (uploadAttemptsFail >= UPLOAD_SERVICE_ATTEMPTS_FAIL) {
            Toast.makeText(getApplicationContext(), "attempts: " + String.valueOf(uploadAttemptsFail)  + "  long sleep", Toast.LENGTH_LONG).show();
            setTimeout(UPLOAD_SERVICE_LONG_PAUSE);
            return;
        }

        Toast.makeText(getApplicationContext(), "attempts: " + String.valueOf(uploadAttemptsFail)  + " shot sleep", Toast.LENGTH_LONG).show();
        setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
    }

    private void setTimeout(long time) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), UploadService.class));
            }
        }, time);
    }

    private void setInterval(long time) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, time, createIntent());
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(getApplicationContext(), UploadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        return pendingIntent;
    }

}
