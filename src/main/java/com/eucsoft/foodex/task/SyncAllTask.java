package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SyncAllTask extends AsyncTask<Void, Integer, Long> implements BaseTask {

    public static final long FOOD_PAIRS_UPDATED = 3;
    public static final long NOT_UPDATED = 4;

    private TaskResultListener taskResultListener;

    private HashMap<String, Object> data = new HashMap<String, Object>();

    @Override
    protected Long doInBackground(Void... params) {

        List<FoodPair> serverFoodPairs;

        try {
            serverFoodPairs = API.fetchUser();
        } catch (Exception e) {
            //TODO: Work on error.
            return RESULT_ERROR;
        }

        FoodDAO foodDAO = new FoodDAO(App.context);

        if (serverFoodPairs.size() != foodDAO.getFoodPairsNumber()) {
            foodDAO.clearFoodPairs();
            foodDAO.insertFoodPairs(serverFoodPairs);
            return FOOD_PAIRS_UPDATED;
        }

        List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
        Collections.sort(serverFoodPairs);

        for (int i = 0; i < dbFoodPairs.size(); i++) {
            if (!dbFoodPairs.get(i).equals(serverFoodPairs.get(i))) {
                foodDAO.clearFoodPairs();
                foodDAO.insertFoodPairs(serverFoodPairs);
                return FOOD_PAIRS_UPDATED;
            }
        }
        return NOT_UPDATED;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(AsyncTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(0, aLong, data);
    }

    public void setTaskResultListener(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }
}
