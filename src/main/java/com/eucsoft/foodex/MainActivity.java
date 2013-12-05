package com.eucsoft.foodex;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.EmptyHomeWallFragment;
import com.eucsoft.foodex.fragment.HomeWallFragment;
import com.eucsoft.foodex.fragment.TrainingHomeFragment;
import com.eucsoft.foodex.menu.LogoutMenu;
import com.eucsoft.foodex.preferences.Preferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        //TODO: REMOVE THIS METHOD?
        //initDBForTesting();

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_screen, getFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == LogoutMenu.ID) {
            new LogoutMenu().select();
        }
        return true;
    }

    private Fragment getFragment() {
        if (isNotAuthorized()) {
            return new AuthFragment();
        }

        if (!Preferences.isTrainingFragmentShown()) {
            return new TrainingHomeFragment();
        }

        FoodDAO foodDAO = new FoodDAO(getApplicationContext());
        int foodCount = foodDAO.getFoodPairsNumber();
        foodDAO.close();
        if (foodCount == 0) {
            return new EmptyHomeWallFragment();
        } else {
            return new HomeWallFragment();
        }
    }

    private boolean isNotAuthorized() {
        if (Preferences.getSessionCookieValue().isEmpty()) {
            return true;
        }
        return false;
    }

    //TODO: REMOVE when not needed
    private void initDBForTesting() {
        FoodDAO foodDAO = new FoodDAO(App.context);
        for(FoodPair pair: foodDAO.getAllFoodPairs()) {
            foodDAO.deleteFoodPair(pair);
        }

        if (foodDAO.getFoodPairsNumber() <= 30) {

            for (int i = 0; i < 5; i++) {
                List<FoodPair> foods = new ArrayList<FoodPair>();
                FoodPair foodPair = new FoodPair();
                foodPair.user.foodURL = "http://cool-projects.com/foodex/abcd/abcd24jjf4f4f4f.jpg";
                foodPair.user.foodDate = new Date();
                foodPair.user.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.user.bonAppetit = 1;

                foodPair.stranger.foodURL = "http://cool-projects.com/foodex/abcd/abcdfdsjofjo3.jpg";
                foodPair.stranger.foodDate = new Date();
                foodPair.stranger.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.stranger.bonAppetit = 0;
                foods.add(foodPair);

                foodPair = new FoodPair();
                foodPair.user.foodURL = "http://cool-projects.com/foodex/abcd/abcdfdsjofjo3.jpg";
                foodPair.user.foodDate = new Date();
                foodPair.user.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.user.bonAppetit = 0;
                foodPair.stranger.foodURL = "http://cool-projects.com/foodex/abcd/abcd24jjf4f4f4f.jpg";
                foodPair.stranger.foodDate = new Date();
                foodPair.stranger.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.stranger.bonAppetit = 1;
                foods.add(foodPair);

                foodPair = new FoodPair();
                foodPair.user.foodURL = "http://cool-projects.com/foodex/abcd/abcd3fiojdsijf03f.jpg";
                foodPair.user.foodDate = new Date();
                foodPair.user.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.user.bonAppetit = 1;
                foodPair.stranger.foodURL = "http://cool-projects.com/foodex/abcd/abcdfjiowjf32.jpg";
                foodPair.stranger.foodDate = new Date();
                foodPair.stranger.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.stranger.bonAppetit = 0;
                foods.add(foodPair);

                foodPair = new FoodPair();
                foodPair.user.foodURL = "http://cool-projects.com/foodex/abcd/abcdfjiowjf32.jpg";
                foodPair.user.foodDate = new Date();
                foodPair.user.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.user.bonAppetit = 0;
                foodPair.stranger.foodURL = "http://cool-projects.com/foodex/abcd/abcd3fiojdsijf03f.jpg";
                foodPair.stranger.foodDate = new Date();
                foodPair.stranger.mapURL = "http://cool-projects.com/foodex/map/cccc/cccciewf32wfa.png";
                foodPair.stranger.bonAppetit = 1;
                foods.add(foodPair);

                foodDAO.insertFoodPairs(foods);
            }
        }
        foodDAO.close();
    }

}
