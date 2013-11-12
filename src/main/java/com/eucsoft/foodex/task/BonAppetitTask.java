package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;

import java.util.HashMap;

public class BonAppetitTask extends AsyncTask<FoodPair, Integer, Long> implements BaseTask {
    public static final int TASK_ID = 300;

    private TaskResultListener taskResultListener;
    private HashMap<String, Object> data;

    public BonAppetitTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    protected Long doInBackground(FoodPair... params) {

        Log.d(BonAppetitTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }
        FoodPair foodPair = params[0];

        data = new HashMap<String, Object>();
        data.put(Constants.FOOD_PAIR, foodPair);

        try {
            API.bonAppetit(String.valueOf(foodPair.stranger.foodId));
        } catch (Exception e) {
            Log.w(BonAppetitTask.class, "Failed to say Bon Appetit.");
            foodPair.stranger.bonAppetit = 0;
            return RESULT_ERROR;
        }

        FoodDAO foodDAO = new FoodDAO(MainActivity.context);
        foodDAO.updateFoodPair(foodPair);
        foodDAO.close();

        return RESULT_OK;
    }

    @Override
    protected void onPostExecute(Long result) {
        Log.d(BonAppetitTask.class, "onPostExecute", result.toString());
        taskResultListener.onTaskResult(TASK_ID, result, data);
    }
}
