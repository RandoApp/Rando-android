package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.broadcast.Broadcast;
import com.eucsoft.foodex.service.SyncService;

import java.util.Collections;
import java.util.List;

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
            Broadcast.send(SyncService.NOTIFICATION);
        }

        List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
        Collections.sort(foodPairs);

        for (int i = 0; i < dbFoodPairs.size(); i++) {
            if (!dbFoodPairs.get(i).equals(foodPairs.get(i))) {
                foodDAO.clearFoodPairs();
                foodDAO.insertFoodPairs(foodPairs);
                Broadcast.send(SyncService.NOTIFICATION);
            }
        }

        if (foodDAO.getNotPairedFoodsNumber() > 0) {
            foodDAO.close();
            return ok();
        }

        foodDAO.close();
        return done();
    }

}
