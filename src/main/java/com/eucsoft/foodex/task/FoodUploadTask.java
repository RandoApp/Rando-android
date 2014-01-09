package com.eucsoft.foodex.task;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.log.Log;

import java.io.File;

public class FoodUploadTask extends BaseTask2 {

    private String fileToUpload;

    public FoodUploadTask(String fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    @Override
    public Integer run() {
        Log.d(FoodUploadTask.class, "run");

        if (fileToUpload == null || TextUtils.isEmpty(fileToUpload)) {
            return error();
        }

        PowerManager pm = (PowerManager) App.context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                FoodUploadTask.class.getName());
        wl.acquire();
        try {
            API.uploadFood(new File(fileToUpload), TakePictureActivity.currentLocation);
        } catch (Exception e) {
            Log.w(FoodUploadTask.class, "File failed to upload. File=", fileToUpload);
            return error();
        } finally {
            wl.release();
        }
        return ok();
    }
}
