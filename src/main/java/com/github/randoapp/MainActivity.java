package com.github.randoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.fragment.EmptyHomeWallFragment;
import com.github.randoapp.fragment.HomeWallFragment;
import com.github.randoapp.fragment.TrainingHomeFragment;
import com.github.randoapp.log.Log;
import com.github.randoapp.menu.LogoutMenu;
import com.github.randoapp.menu.ReportMenu;
import com.github.randoapp.preferences.Preferences;

public class MainActivity extends ActionBarActivity {

    public static Activity activity;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(android.content.BroadcastReceiver.class, "Recieved Update request.");

            RelativeLayout emptyHome = (RelativeLayout) findViewById(R.id.empty_home);

            Bundle extra = intent.getExtras();
            int randoPairsNumber = (Integer) extra.get(Constants.RANDO_PAIRS_NUMBER);
            if (randoPairsNumber == 0 && emptyHome != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, new EmptyHomeWallFragment()).commit();
            }
            if (randoPairsNumber > 0 && emptyHome != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, new HomeWallFragment()).commit();
            }
        }
    };

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
    public void onBackPressed() {
        if (ReportMenu.isReport) {
            ReportMenu.off();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem actionReportItem = menu.findItem(R.id.action_report);
        actionReportItem.setTitle(ReportMenu.getMenuTitle());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ReportMenu.ID:
                new ReportMenu(item, this).select();
                break;
            case LogoutMenu.ID:
                new LogoutMenu(item, this).select();
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

        RandoDAO randoDAO = new RandoDAO(getApplicationContext());
        int randoCount = randoDAO.getRandoPairsNumber();
        randoDAO.close();
        if (randoCount == 0) {
            return new EmptyHomeWallFragment();
        } else {
            return new HomeWallFragment();
        }
    }

    private boolean isNotAuthorized() {
        return Preferences.getAuthToken().isEmpty();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(receiver, new IntentFilter(Constants.SYNC_SERVICE_BROADCAST));
    }
}
