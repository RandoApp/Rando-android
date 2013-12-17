package com.eucsoft.foodex.service.listener;

import com.eucsoft.foodex.App;

import com.eucsoft.foodex.api.OnFetchUser;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.broadcast.Broadcast;
import com.eucsoft.foodex.service.SyncService;

import java.util.Collections;
import java.util.List;

public class FetchUserListener implements OnFetchUser {

    private OnFetched fetchedListener;

    @Override
    public void onFetchUser(List<FoodPair> foodPairs) {
        Log.v(FetchUserListener.class, "OnFetchUser");
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

        if (fetchedListener != null && foodDAO.getNotPairedFoodsNumber() > 0) {
            fetchedListener.onFetched();
        }

        foodDAO.close();
    }

    public FetchUserListener onOk(OnFetched listener) {
        this.fetchedListener = listener;
        return this;
    }
}
