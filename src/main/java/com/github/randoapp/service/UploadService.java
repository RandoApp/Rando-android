package com.github.randoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
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
import java.util.List;

public class UploadService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(UploadService.class, "Upload service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(UploadService.class, "Upload service onStartCommand");
        if (RandoDAO.countAllRandosToUpload() > 0 && NetworkUtil.isOnline(getApplicationContext())) {
            upload();
        }
        return Service.START_NOT_STICKY;
    }

    private void upload() {
        final List<RandoUpload> randosToUpload = RandoDAO.getAllRandosToUpload("ASC");
        Log.d(UploadService.class, "Need upload ", String.valueOf(randosToUpload.size()), " randos");
        if (randosToUpload.size() > 0) {
            for (final RandoUpload randoToUpload : randosToUpload) {
                long pastFromLastTry = new Date().getTime() - randoToUpload.lastTry.getTime();
                if (pastFromLastTry >= Constants.UPLOAD_RETRY_TIMEOUT) {
                    randoToUpload.lastTry = new Date();
                    RandoDAO.updateRandoToUpload(randoToUpload);
                    Log.d(UploadService.class, "Starting Upload:", randoToUpload.toString());
                    API.uploadImageVolley(randoToUpload, new UploadRandoListener() {
                        @Override
                        public void onUpload(Rando rando) {
                            Log.d(UploadService.class, rando.toString());

                            if (rando != null) {
                                RandoDAO.createRando(rando);
                            }
                            Log.d(UploadService.class, "Delete rando",randoToUpload.toString());
                            RandoDAO.deleteRandoToUpload(randoToUpload);
                            Intent intent = new Intent(Constants.UPLOAD_SERVICE_BROADCAST_EVENT);
                            UploadService.this.sendBroadcast(intent);
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
                                    String status = response.getString("status");
                                    String message = response.getString("message");

                                    Log.e(UploadService.class, "Error Status", status);
                                    Log.e(UploadService.class, "Error Message", message);

                                    if (networkResponse.statusCode == 404) {
                                        errorMessage = "Resource not found";
                                    } else if (networkResponse.statusCode == 401) {
                                        errorMessage = message + " Please login again";
                                        Intent intent = new Intent(Constants.AUTH_FAILURE_BROADCAST_EVENT);
                                        UploadService.this.sendBroadcast(intent);
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
                            Log.e(UploadService.class, "Error" + errorMessage, error);
                        }
                    });
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        Log.e(UploadService.class, "Sleep Error:");
                    }
                }
            }
        }
    }
}
