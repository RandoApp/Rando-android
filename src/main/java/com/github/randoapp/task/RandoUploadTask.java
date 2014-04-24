package com.github.randoapp.task;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;

import com.github.randoapp.App;
import com.github.randoapp.CameraActivity;
import com.github.randoapp.api.API;
import com.github.randoapp.log.Log;

import java.io.File;

public class RandoUploadTask extends BaseTask {

    private String fileToUpload;

    public RandoUploadTask(String fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    @Override
    public Integer run() {
        Log.d(RandoUploadTask.class, "run");

        if (fileToUpload == null || TextUtils.isEmpty(fileToUpload)) {
            return ERROR;
        }

        PowerManager pm = (PowerManager) App.context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                RandoUploadTask.class.getName());
        wl.acquire();
        try {
            API.uploadImage(new File(fileToUpload), CameraActivity.currentLocation);
        } catch (Exception e) {
            Log.w(RandoUploadTask.class, "File failed to upload. File=", fileToUpload);
            return ERROR;
        } finally {
            wl.release();
        }
        return OK;
    }
}
