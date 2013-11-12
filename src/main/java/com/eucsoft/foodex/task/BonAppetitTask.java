package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;

import java.util.HashMap;

public class BonAppetitTask extends AsyncTask<FoodPair.User, Integer, Long> implements BaseTask {
    public static final int TASK_ID = 300;

    private TaskResultListener taskResultListener;
    private HashMap<String, Object> data;

    @Override
    protected Long doInBackground(FoodPair.User... params) {

        Log.d(BonAppetitTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }
        FoodPair.User user = params[0];


        try {
            API.bonAppetit(String.valueOf(user.foodId));
        } catch (Exception e) {
            Log.w(BonAppetitTask.class, "Failed to say Bon Appetit.");
            return RESULT_ERROR;
        }

        data = new HashMap<String, Object>();
        user.bonAppetit = 1;
        data.put(Constants.FOOD_PAIR, user);

        return RESULT_OK;
    }

    @Override
    protected void onPostExecute(Long result) {
        Log.d(BonAppetitTask.class, "onPostExecute", result.toString());
        taskResultListener.onTaskResult(TASK_ID, result, data);
    }
}
