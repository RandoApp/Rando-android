package com.eucsoft.foodex.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import static com.eucsoft.foodex.Constants.RESULT;
import static com.eucsoft.foodex.Constants.UPLOAD_SERVICE_NOTIFICATION;

public class UploadService extends IntentService {


    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO: Implement Upload
        publishResults(Activity.RESULT_OK);
    }

    private void publishResults(int result) {
        Intent intent = new Intent(UPLOAD_SERVICE_NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

}
