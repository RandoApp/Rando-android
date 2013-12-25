package com.eucsoft.foodex;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.EmptyHomeWallFragment;
import com.eucsoft.foodex.fragment.HomeWallFragment;
import com.eucsoft.foodex.fragment.TrainingHomeFragment;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.menu.LogoutMenu;
import com.eucsoft.foodex.menu.ReportMenu;
import com.eucsoft.foodex.preferences.Preferences;

public class MainActivity extends ActionBarActivity {

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setAppTitle();

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_screen, getFragment())
                    .commit();
        }
    }

    //Just for alpha testers. Remove when release.
    private void setAppTitle() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String name = getResources().getString(R.string.app_name);
            String version = packageInfo.versionName;
            String codeVersion = String.valueOf(packageInfo.versionCode);
            setTitle(name + " " + version + " [build: " + codeVersion + "]");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MainActivity.class, "Can't set app title, because: ", e.getMessage());
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
        switch (item.getItemId()) {
            case ReportMenu.ID:
                new ReportMenu().select();
                break;
            case LogoutMenu.ID:
                new LogoutMenu().select();
                break;
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

}
