package com.eucsoft.foodex;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.HomeWallFragment;
import com.eucsoft.foodex.menu.LogoutMenu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public static Activity activity;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();

        //TODO: REMOVE THIS METHOD?
        initDBForTesting();

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
        if (isNeedAuth()) {
            return new AuthFragment();
        }
        return new HomeWallFragment();
    }

    private boolean isNeedAuth() {
        SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE);
        if (sharedPref.getString(Constants.SEESSION_COOKIE_NAME, null) == null) {
            return true;
        }
        return false;
    }

    //TODO: REMOVE
    private void initDBForTesting() {
        FoodDAO foodDAO = new FoodDAO(context);
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
