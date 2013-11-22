package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;

import java.util.HashMap;

public class AnonymousSignupTask extends AsyncTask<String, Integer, Long> implements BaseTask {

    private TaskResultListener taskResultListener;

    private HashMap<String, Object> errors = new HashMap<String, Object>();

    public AnonymousSignupTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    protected Long doInBackground(String ... params) {
        try {
            String uuid = params[0];
            API.anonymous(uuid);
            return RESULT_OK;
        } catch (Exception exc) {
            errors.put(Constants.ERROR, exc.getMessage());
            return RESULT_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(DownloadFoodPicsTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(0, aLong, errors);
    }
}
