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
import com.github.randoapp.util.FileUtil;
import com.github.randoapp.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UploadJob extends Job {

    public static final String TAG = "job_upload_tag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(UploadJob.class, "Starting Job Execution");

        if (!NetworkUtil.isOnline(getContext())) {
            Log.d(UploadJob.class, "No network, reschedule. BackOff Millis:" + params.getBackoffMs());
            return Result.RESCHEDULE;
        }

        RandoUpload randoUpload;
        while ((randoUpload = RandoDAO.getNextRandoToUpload()) != null){
            upload(randoUpload);
        }

        if (RandoDAO.countAllRandosToUpload() > 0){
            return Result.RESCHEDULE;
        } else {
            return Result.SUCCESS;
        }
    }

    private Result upload(final RandoUpload randoToUpload) {
        Result result;
        final JobResultFuture resultFuture = new JobResultFuture();
        long pastFromLastTry = new Date().getTime() - randoToUpload.lastTry.getTime();
        if (pastFromLastTry >= Constants.UPLOAD_RETRY_TIMEOUT) {
            randoToUpload.lastTry = new Date();
            RandoDAO.updateRandoToUpload(randoToUpload);
            Log.d(UploadJob.class, "Starting Upload:", randoToUpload.toString());
            API.uploadImage(randoToUpload, new UploadRandoListener() {
                @Override
                public void onUpload(Rando rando) {
                    Log.d(UploadJob.class, rando.toString());
                    if (rando != null) {
                        RandoDAO.createRando(rando);
                    }
                    Log.d(UploadJob.class, "Delete rando", randoToUpload.toString());
                    FileUtil.removeFileIfExist(randoToUpload.file);
                    RandoDAO.deleteRandoToUploadById(randoToUpload.id);
                    Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
                    UploadJob.this.getContext().sendBroadcast(intent);
                    resultFuture.put(Result.SUCCESS);
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

                            Log.e(UploadJob.class, "Error Status", status);
                            Log.e(UploadJob.class, "Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message + " Please login again";
                                Intent intent = new Intent(Constants.AUTH_FAILURE_BROADCAST_EVENT);
                                UploadJob.this.getContext().sendBroadcast(intent);
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message + " Wrong Request when uploading Rando: " + randoToUpload.toString();
                                FileUtil.removeFileIfExist(randoToUpload.file);
                                RandoDAO.deleteRandoToUploadById(randoToUpload.id);
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message + " Something is getting wrong";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e(UploadJob.class, "Error" + errorMessage + " Errors in a raw count: ", error);
                    resultFuture.put(Result.RESCHEDULE);
                }
            });
        } else {
            return Result.RESCHEDULE;
        }
        try {
            result = resultFuture.get(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Log.e(UploadJob.class, "Reschedule: InterruptedException", e);
            result = Result.RESCHEDULE;
        } catch (TimeoutException e) {
            Log.e(UploadJob.class, "Reschedule: TimeoutException", e);
            result = Result.RESCHEDULE;
        }
        return result;
    }
}
