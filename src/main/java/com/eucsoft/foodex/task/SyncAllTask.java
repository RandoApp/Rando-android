package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SyncAllTask extends AsyncTask<Void, Integer, Long> implements BaseTask {

    private TaskResultListener taskResultListener;

    private HashMap<String, Object> data = new HashMap<String, Object>();

    @Override
    protected Long doInBackground(Void... params) {

        List<FoodPair> serverFoodPairs;

        try {
            serverFoodPairs = API.fetchUser();
        } catch (Exception e) {
            return RESULT_ERROR;
        }

        FoodDAO foodDAO = new FoodDAO(App.context);

        if (serverFoodPairs.size() != foodDAO.getFoodPairsNumber()) {
            foodDAO.clearFoodPairs();
            foodDAO.insertFoodPairs(serverFoodPairs);
            return RESULT_OK;
        }

        List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();

        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(AsyncTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(0, aLong, data);
    }

    public void setTaskResultListener(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    class FoodPairDateComparator implements Comparator<FoodPair> {
        @Override
        public int compare(FoodPair lhs, FoodPair rhs) {
            return (int) (lhs.user.foodDate.getTime() - rhs.user.foodDate.getTime());
        }
    }
}
