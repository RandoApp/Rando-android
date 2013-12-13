package com.eucsoft.foodex.service.listener;

import com.eucsoft.foodex.App;
import static com.eucsoft.foodex.Constants.*;
import com.eucsoft.foodex.api.onFetchUser;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.notification.Notification;
import com.eucsoft.foodex.service.SyncService;

import java.util.Collections;
import java.util.List;

public class FetchUserListener implements onFetchUser {

    private SyncService syncService;

    public FetchUserListener(SyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void onFetchUser(List<FoodPair> foodPairs) {
        Log.v(onFetchUser.class, "foosPairsRecieved");
        FoodDAO foodDAO = new FoodDAO(App.context);

        if (foodPairs.size() != foodDAO.getFoodPairsNumber()) {
            foodDAO.clearFoodPairs();
            foodDAO.insertFoodPairs(foodPairs);
            Notification.show();
        }

        List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
        Collections.sort(foodPairs);

        for (int i = 0; i < dbFoodPairs.size(); i++) {
            if (!dbFoodPairs.get(i).equals(foodPairs.get(i))) {
                foodDAO.clearFoodPairs();
                foodDAO.insertFoodPairs(foodPairs);
                Notification.show();
            }
        }

        if (foodDAO.getNotPairedFoodsNumber() > 0) {
            syncService.setAlarm(System.currentTimeMillis() + SERVICE_SHORT_PAUSE);
            Log.i(FetchUserListener.class, "Service will start in " + SERVICE_SHORT_PAUSE / 1000 + " seconds");
        } else {
            syncService.setAlarm(System.currentTimeMillis() + SERVICE_LONG_PAUSE);
            Log.i(FetchUserListener.class, "Service will start in " + SERVICE_LONG_PAUSE / 1000 + " seconds");
        }

        foodDAO.close();
    }

}
