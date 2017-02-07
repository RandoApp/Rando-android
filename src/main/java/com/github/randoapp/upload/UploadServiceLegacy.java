package com.github.randoapp.upload;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.UploadRandoListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.FileUtil;
import com.github.randoapp.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.github.randoapp.Constants.UPLOAD_SERVICE_SHORT_PAUSE;

public class UploadServiceLegacy extends Service {

    public static final String FORBIDDEN_ERROR = "ForbiddenException";
    public static final String FILE_NOT_FOUND_ERROR = "FileNotFound";
    public static final String REQUEST_TOO_LONG_ERROR = "RequestTooLongException";
    public static final String INCORRECT_ARGS_ERROR = "IncorrectArgs";

    public static final long UPLOAD_SERVICE_LONG_PAUSE = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static final long UPLOAD_SERVICE_VERY_LONG_PAUSE = AlarmManager.INTERVAL_HOUR;
    public static final long UPLOAD_SERVICE_FORBIDDEN_PAUSE = AlarmManager.INTERVAL_HOUR * 6;
    public static final long UPLOAD_SERVICE_INTERVAL = AlarmManager.INTERVAL_HOUR * 3;

    public static final int UPLOAD_SERVICE_ATTEMPTS_FAIL = 50;
    public static final int UPLOAD_SERVICE_MANY_ATTEMPTS_FAIL = 150;

    private int uploadAttemptsFail = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(UploadServiceLegacy.class, "Upload service created");
        super.onCreate();
        setInterval(UPLOAD_SERVICE_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            upload();
        } else {
            stopSelf();
        }
        return Service.START_NOT_STICKY;
    }

    private void upload() {
        Log.d(UploadServiceLegacy.class, "Upload service try upload next file");
        if (!NetworkUtil.isOnline(getApplicationContext())) {
            Log.d(UploadServiceLegacy.class, "No network. sleep.");
            uploadAttemptsFail++;
            sleep();
            return;
        }

        List<RandoUpload> randosToUpload = RandoDAO.getAllRandosToUpload("ASC");
        Log.d(UploadServiceLegacy.class, "Need upload ", String.valueOf(randosToUpload.size()), " randos");
        if (randosToUpload.size() > 0) {
            boolean isNeedTryAgain = true;
            for (RandoUpload randoToUpload : randosToUpload) {
                long pastFromLastTry = new Date().getTime() - randoToUpload.lastTry.getTime();
                if (pastFromLastTry >= Constants.UPLOAD_RETRY_TIMEOUT) {
                    randoToUpload.lastTry = new Date();
                    RandoDAO.updateRandoToUpload(randoToUpload);
                    uploadFile(randoToUpload);
                    isNeedTryAgain = false;
                    //One service call = one file upload
                    break;
                }
            }

            if (isNeedTryAgain) {
                Log.d(UploadServiceLegacy.class, "Need retry upload. Sleep shot pause and will try again.");
                setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
            }
        }
    }

    private void uploadFile(final RandoUpload rando) {

        API.uploadImage(rando, new UploadRandoListener() {
            @Override
            public void onUpload(Rando randoUploaded) {
                if (randoUploaded != null) {
                    RandoDAO.createRando(randoUploaded);
                }
                uploadAttemptsFail = 0;
                deleteRando(rando);
                setTimeout(0);  //go to next rando to upload immediately
                Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
                UploadServiceLegacy.this.sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString(Constants.ERROR_STATUS_PARAM);
                        String message = response.getString(Constants.ERROR_MESSAGE_PARAM);

                        Log.e(UploadServiceLegacy.class, "Error Status", status);
                        Log.e(UploadServiceLegacy.class, "Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                            Intent intent = new Intent(Constants.AUTH_FAILURE_BROADCAST_EVENT);
                            UploadServiceLegacy.this.sendBroadcast(intent);
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Wrong Request when uploading Rando: " + rando.toString();
                            deleteRando(rando);
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                uploadAttemptsFail++;
                Log.e(UploadServiceLegacy.class, "Error" + errorMessage + " Errors in a raw count: " + uploadAttemptsFail, error);
                if (uploadAttemptsFail < Constants.UPLOAD_SERVICE_ATTEMPTS_FAIL) {
                    setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
                } else {
                    setTimeout(UPLOAD_SERVICE_LONG_PAUSE);
                }
            }
        });
    }

    private void deleteRando(RandoUpload rando) {
        Log.d(UploadServiceLegacy.class, "Delete rando:",rando.toString());
        FileUtil.removeFileIfExist(rando.file);
        RandoDAO.deleteRandoToUpload(rando);
    }

    private void sleep() {
        Log.d(UploadServiceLegacy.class, "Sleep: ", String.valueOf(uploadAttemptsFail));
        if (uploadAttemptsFail >= UPLOAD_SERVICE_MANY_ATTEMPTS_FAIL) {
            Log.d(UploadServiceLegacy.class, "Very long sleep");
            setTimeout(UPLOAD_SERVICE_VERY_LONG_PAUSE);
            return;
        }

        if (uploadAttemptsFail >= UPLOAD_SERVICE_ATTEMPTS_FAIL) {
            Log.d(UploadServiceLegacy.class, "Long sleep");
            setTimeout(UPLOAD_SERVICE_LONG_PAUSE);
            return;
        }

        Log.d(UploadServiceLegacy.class, "Shot sleep");
        setTimeout(UPLOAD_SERVICE_SHORT_PAUSE);
    }

    private void setTimeout(long time) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), UploadServiceLegacy.class));
            }
        }, time);
    }

    private void setInterval(long time) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, time, createIntent());
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(getApplicationContext(), UploadServiceLegacy.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        return pendingIntent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(UploadServiceLegacy.class, "Service destroy");
    }

    public static boolean isRunning() {
        ActivityManager manager = (ActivityManager) App.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (UploadServiceLegacy.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}