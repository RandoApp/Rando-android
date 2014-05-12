package com.github.randoapp.task;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;

import com.github.randoapp.App;
import com.github.randoapp.CameraActivity;
import com.github.randoapp.api.API;
import com.github.randoapp.log.Log;

import java.io.File;

public class UploadTask extends BaseTask {

    private String fileToUpload;

    public UploadTask(String fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    @Override
    public Integer run() {
        Log.d(UploadTask.class, "run");

        if (fileToUpload == null || TextUtils.isEmpty(fileToUpload)) {
            return ERROR;
        }

        PowerManager pm = (PowerManager) App.context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                UploadTask.class.getName());
        wl.acquire();
        try {
            API.uploadImage(new File(fileToUpload), CameraActivity.currentLocation);
        } catch (Exception e) {
            data.put("error", e.getMessage());
            Log.w(UploadTask.class, "File failed to upload. File=", fileToUpload, " because: ", e.getMessage());
            return ERROR;
        } finally {
            wl.release();
        }
        return OK;
    }
}
