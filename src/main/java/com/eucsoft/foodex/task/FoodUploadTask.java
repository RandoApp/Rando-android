package com.eucsoft.foodex.task;

import android.content.Context;
import android.os.AsyncTask;

import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.service.SyncService;

import java.io.File;

public class FoodUploadTask extends AsyncTask<String, Integer, Long> implements BaseTask {

    public static final int TASK_ID = 100;

    private TaskResultListener taskResultListener;
    private Context context;

    public FoodUploadTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
        this.context = context;
    }

    @Override
    protected Long doInBackground(String... params) {
        Log.d(FoodUploadTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }

        String filename = params[0];
        try {
            API.uploadFood(new File(filename), TakePictureActivity.currentLocation);
        } catch (Exception e) {
            Log.w(FoodUploadTask.class, "File failed to upload. File=", filename);
            return RESULT_ERROR;
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
