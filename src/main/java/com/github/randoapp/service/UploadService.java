package com.github.randoapp.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;
import com.github.randoapp.task.UploadTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.ConnectionUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.github.randoapp.Constants.FILE_NOT_FOUND_ERROR;
import static com.github.randoapp.Constants.FORBIDDEN_ERROR;
import static com.github.randoapp.Constants.INCORRECT_ARGS_ERROR;
import static com.github.randoapp.Constants.REQUEST_TOO_LONG_ERROR;
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
        Log.d(UploadService.class, "Upload service created");
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            Log.i(UploadService.class,  "Firebase ID: " + FirebaseInstanceId.getInstance().getToken());
            Log.i(UploadService.class,  "Firebase App: " + FirebaseApp.getInstance());
        }
        setInterval(UPLOAD_SERVICE_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        upload();
        return Service.START_NOT_STICKY;
    }

    private void upload() {
        Log.d(UploadService.class, "Upload service try upload next file");
        if (!ConnectionUtil.isOnline(getApplicationContext())) {
            Log.d(UploadService.class, "No network. sleep.");
            uploadAttemptsFail++;
            sleep();
            return;
        }

        List<RandoUpload> randosToUpload = RandoDAO.getAllRandosToUpload();
        Log.d(UploadService.class, "Need upload ", String.valueOf(randosToUpload.size()), " randos");
        if (randosToUpload.size() > 0) {
            boolean isNeedTryAgain = true;
            for (RandoUpload randoToUpload: randosToUpload) {
                long pastFromLastTry = new Date().getTime() - randoToUpload.lastTry.getTime();
                if (pastFromLastTry >= Constants.UPLOAD_RETRY_TIMEOUT){
                    randoToUpload.lastTry = new Date();
                    RandoDAO.updateRandoToUpload(randoToUpload);
                    uploadFile(randoToUpload);
                    isNeedTryAgain = false;
                    //One service call = one file upload
                    break;
                }
            }

            if (isNeedTryAgain) {
                Log.d(UploadService.class, "Need retry upload. Sleep shot pause and will try again.");
                setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
            }
        }
    }

    private void uploadFile(final RandoUpload rando) {
        new UploadTask(rando)
                .onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        uploadAttemptsFail = 0;
                        deleteRando(rando);
                        setTimeout(0);  //go to next rando to upload immediately
                        Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
                        UploadService.this.sendBroadcast(intent);
                    }
                }).onError(new OnError() {
            @Override
            public void onError(Map<String, Object> data) {
                String error = (String) data.get("error");
                if (FILE_NOT_FOUND_ERROR.equals(error)) {
                    Log.d(UploadService.class, "Can not upload image, because file not found");
                    deleteRando(rando);
                    setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
                } else if (FORBIDDEN_ERROR.equals(error)) {
                    Log.d(UploadService.class, "Can not upload image, because forbidden");
                    setTimeout(UPLOAD_SERVICE_FORBIDDEN_PAUSE);
                } else if (REQUEST_TOO_LONG_ERROR.equals(error) || INCORRECT_ARGS_ERROR.equals(error)) {
                    Log.d(UploadService.class, "Can not upload image, because request is too long or incorrect args");
                    deleteRando(rando);
                    setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
                } else {
                    Log.w(UploadService.class, "Can not upload image, because: ", error);
                    rando.lastTry = new Date(0);
                    RandoDAO.updateRandoToUpload(rando);
                    uploadAttemptsFail++;
                    sleep();
                }
            }
        }).execute();
    }

    private void deleteRando(RandoUpload rando) {
        Log.d(UploadService.class, "Delete rando");
        RandoDAO.deleteRandoToUpload(rando);
    }

    private void sleep() {
        Log.d(UploadService.class, "Sleep: ", String.valueOf(uploadAttemptsFail));
        if (uploadAttemptsFail >= UPLOAD_SERVICE_MANY_ATTEMPTS_FAIL) {
            Log.d(UploadService.class, "Very long sleep");
            setTimeout(UPLOAD_SERVICE_VERY_LONG_PAUSE);
            return;
        }

        if (uploadAttemptsFail >= UPLOAD_SERVICE_ATTEMPTS_FAIL) {
            Log.d(UploadService.class, "Long sleep");
            setTimeout(UPLOAD_SERVICE_LONG_PAUSE);
            return;
        }

        Log.d(UploadService.class, "Shot sleep");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(UploadService.class, "Service destroy");
    }

    public static boolean isRunning() {
        ActivityManager manager = (ActivityManager) App.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SyncService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
