package com.eucsoft.foodex.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.service.SyncService;

import java.io.File;

public class FoodUploadTask extends AsyncTask<String, Integer, Long> implements BaseTask {

    public static final int TASK_ID = 100;

    private TaskResultListener taskResultListener;

    public FoodUploadTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    protected Long doInBackground(String... params) {
        Log.d(FoodUploadTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }

        String filename = params[0];
        PowerManager pm = (PowerManager) App.context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                FoodUploadTask.class.getName());
        wl.acquire();
        try {
            API.uploadFood(new File(filename), TakePictureActivity.currentLocation);
        } catch (Exception e) {
            Log.w(FoodUploadTask.class, "File failed to upload. File=", filename);
            return RESULT_ERROR;
        } finally {
            wl.release();
        }
        SyncService.run();
        return RESULT_OK;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(FoodUploadTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(TASK_ID, aLong, null);
    }
}
