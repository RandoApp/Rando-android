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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.fragment.EmptyHomeWallFragment;
import com.github.randoapp.fragment.HomeWallFragment;
import com.github.randoapp.fragment.TrainingHomeFragment;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.UploadService;
import com.github.randoapp.task.LogoutTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.view.Progress;

import java.util.Map;

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

        initNavigationMenu();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_screen, getFragment())
                    .commit();
        }
    }

    private void initNavigationMenu() {
        TextView versionText = (TextView) findViewById(R.id.app_version);
        PackageManager manager = getPackageManager();
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ex) {

        }
        String version = "";
        if (info != null) {
            version = info.versionName;
        }
        versionText.setText(versionText.getText() + " " + version);

        findViewById(R.id.main_drawer_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) findViewById(R.id.main_drawer_layout)).closeDrawers();
                Progress.show(App.context.getResources().getString(R.string.logout_progress));
                new LogoutTask()
                        .onDone(new OnDone() {
                            @Override
                            public void onDone(Map<String, Object> data) {
                                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                                Progress.hide();

                            }
                        })
                        .execute();
            }
        });

    }

    private Fragment getFragment() {
        if (isNotAuthorized()) {
            return new AuthFragment();
        }

        if (!Preferences.isTrainingFragmentShown()) {
            return new TrainingHomeFragment();
        }

        int randoCount = RandoDAO.getAllRandosNumber();
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
        startService(new Intent(getApplicationContext(), UploadService.class));
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
