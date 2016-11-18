package com.github.randoapp.upload;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.evernote.android.job.Job;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.UploadRandoListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UploadJob extends Job {

    public static final String TAG = "job_upload_tag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        if (!NetworkUtil.isOnline(getContext())) {
            return Result.RESCHEDULE;
        }
        params.getExtras();

        final RandoUpload randoToUpload = RandoDAO.getRandoToUploadById(1);

        final JobResultFuture resultFuture = new JobResultFuture();
        long pastFromLastTry = new Date().getTime() - randoToUpload.lastTry.getTime();
        if (pastFromLastTry >= Constants.UPLOAD_RETRY_TIMEOUT) {
            randoToUpload.lastTry = new Date();
            RandoDAO.updateRandoToUpload(randoToUpload);
            Log.d(UploadService.class, "Starting Upload:", randoToUpload.toString());
            API.uploadImage(randoToUpload, new UploadRandoListener() {
                @Override
                public void onUpload(Rando rando) {
                    Log.d(UploadService.class, rando.toString());

                    if (rando != null) {
                        RandoDAO.createRando(rando);
                    }
                    Log.d(UploadService.class, "Delete rando", randoToUpload.toString());
                    RandoDAO.deleteRandoToUpload(randoToUpload);
                    Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
                    UploadJob.this.getContext().sendBroadcast(intent);
                    resultFuture.put(Result.SUCCESS);
                    //scheduleNextRun(Constants.UPLOAD_SERVICE_SHORT_PAUSE);
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

                            Log.e(UploadService.class, "Error Status", status);
                            Log.e(UploadService.class, "Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message + " Please login again";
                                Intent intent = new Intent(Constants.AUTH_FAILURE_BROADCAST_EVENT);
                                UploadJob.this.getContext().sendBroadcast(intent);
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message + " Wrong Request when uploading Rando: " + randoToUpload.toString();
                                RandoDAO.deleteRandoToUpload(randoToUpload);
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message + " Something is getting wrong";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e(UploadService.class, "Error" + errorMessage + " Errors in a raw count: ", error);
                    resultFuture.put(Result.FAILURE);
                }
            });
        } else {
            return Result.FAILURE;
        }
        Log.d(UploadJob.class, "Run Job!!!", Thread.currentThread().toString());
        Result result;
        try {
            result = resultFuture.get(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            result = Result.FAILURE;
        } catch (TimeoutException e) {
            result = Result.FAILURE;
        }
        return result;
    }
}
