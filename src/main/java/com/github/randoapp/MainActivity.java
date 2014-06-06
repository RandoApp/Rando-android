package com.github.randoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE;
import static com.github.randoapp.Constants.SYNC_SERVICE_BROADCAST_EVENT;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_BROADCAST_EVENT;

public class MainActivity extends FragmentActivity {

    public static Activity activity;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(android.content.BroadcastReceiver.class, "Recieved Update request.");

            if (SYNC_SERVICE_BROADCAST_EVENT.equals(intent.getAction())) {
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
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_screen, getFragment())
                    .commit();
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
        int randoCount = randoDAO.getAllRandosNumber();
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
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceivers();
    }

    private void registerReceivers() {
        registerReceiver(receiver, new IntentFilter(SYNC_SERVICE_BROADCAST_EVENT));
        registerReceiver(receiver, new IntentFilter(UPLOAD_SERVICE_BROADCAST_EVENT));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_screen, new HomeWallFragment()).commit();
        }
    }
}
