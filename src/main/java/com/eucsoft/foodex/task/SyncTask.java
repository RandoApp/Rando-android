package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;

import java.util.Collections;
import java.util.List;

import static com.eucsoft.foodex.Constants.NEED_NOTIFICATION;

public class SyncTask extends BaseTask2 {

    private List<FoodPair> foodPairs;

    public SyncTask(List<FoodPair> foodPairs) {
        this.foodPairs = foodPairs;
    }

    @Override
    public Integer run() {
        Log.v(SyncTask.class, "OnFetchUser");
        FoodDAO foodDAO = new FoodDAO(App.context);

        if (foodPairs.size() != foodDAO.getFoodPairsNumber()) {
            foodDAO.clearFoodPairs();
            foodDAO.insertFoodPairs(foodPairs);
            data.put(NEED_NOTIFICATION, true);
        }

        List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
        Collections.sort(foodPairs);

        for (int i = 0; i < dbFoodPairs.size(); i++) {
            if (!dbFoodPairs.get(i).equals(foodPairs.get(i))) {
                foodDAO.clearFoodPairs();
                foodDAO.insertFoodPairs(foodPairs);
                data.put(NEED_NOTIFICATION, true);
            }
        }

        if (data.get(NEED_NOTIFICATION) != null
                || foodDAO.getNotPairedFoodsNumber() > 0) {
            foodDAO.close();
            return ok();
        }

        foodDAO.close();
        return ok();
    }

}
