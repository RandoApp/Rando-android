package com.eucsoft.foodex.task;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.callback.OnDone;

import org.apache.http.auth.AuthenticationException;

import java.util.Map;

public class BonAppetitTask extends BaseTask {
    public static final int TASK_ID = 300;

    private FoodPair foodPair;

    public BonAppetitTask(FoodPair foodPair) {
        this.foodPair = foodPair;
    }

    @Override
    public Integer run() {
        Log.d(BonAppetitTask.class, "Task start");

        if (foodPair == null) {
            return ERROR;
        }

        foodPair.stranger.bonAppetit = 1;

        data.put(Constants.FOOD_PAIR, foodPair);

        try {
            API.bonAppetit(String.valueOf(foodPair.stranger.foodId));
        } catch (AuthenticationException exc) {
            new LogoutTask()
                .onDone(new OnDone() {
                    @Override
                    public void onDone(Map<String, Object> data) {
                        FragmentManager fragmentManager = ((ActionBarActivity) MainActivity.activity).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                    }
                })
                .execute();
            foodPair.stranger.bonAppetit = 0;
            return ERROR;
        } catch (Exception e) {
            Log.w(BonAppetitTask.class, "Failed to say Bon Appetit.");
            foodPair.stranger.bonAppetit = 0;
            return ERROR;
        }

        FoodDAO foodDAO = new FoodDAO(App.context);
        foodDAO.updateFoodPair(foodPair);
        foodDAO.close();

        return OK;
    }

}
