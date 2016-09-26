package com.github.randoapp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.fragment.EmptyHomeWallFragment;
import com.github.randoapp.fragment.HomeWallFragment;
import com.github.randoapp.fragment.TrainingHomeFragment;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.UploadService;
import com.github.randoapp.util.GooglePlayServicesUtil;
import com.github.randoapp.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.acra.ACRA;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.github.randoapp.Constants.AUTH_FAILURE_BROADCAST_EVENT;
import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE;
import static com.github.randoapp.Constants.SYNC_BROADCAST_EVENT;
import static com.github.randoapp.Constants.UPDATED;
import static com.github.randoapp.Constants.UPDATE_PLAY_SERVICES_REQUEST_CODE;

public class MainActivity extends FragmentActivity {

    public static Activity activity;

    private int playServicesStatus;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(BroadcastReceiver.class, "Recieved event:", intent.getAction());

            if (SYNC_BROADCAST_EVENT.equals(intent.getAction())) {
                RelativeLayout emptyHome = (RelativeLayout) findViewById(R.id.empty_home);
                Bundle extra = intent.getExtras();
                int randosNumber = (Integer) extra.get(Constants.TOTAL_RANDOS_NUMBER);
                boolean isUpdated = UPDATED.equals(extra.get(Constants.UPDATE_STATUS));
                if (randosNumber == 0 && emptyHome == null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_screen, new EmptyHomeWallFragment()).commit();
                }
                if (randosNumber > 0 && emptyHome != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_screen, new HomeWallFragment()).commit();
                }
                Toast.makeText(MainActivity.this, (isUpdated ? R.string.sync_randos_updated : R.string.sync_nothing_new), Toast.LENGTH_LONG).show();
            } else if(AUTH_FAILURE_BROADCAST_EVENT.equals(intent.getAction())){
                Preferences.removeAuthToken();
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        PermissionUtils.requestMissingPermissions(this, Manifest.permission.CAMERA);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_screen, getFragment())
                    .commit();
        }
    }

    private Fragment getFragment() {
        if (isNotAuthorized()) {
            return new AuthFragment();
        }
        API.syncUserAsync(null, null);
        if (!Preferences.isTrainingFragmentShown()) {
            return new TrainingHomeFragment();
        }

        int randoCount = RandoDAO.countAllRandosNumber();
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
    protected void onResume() {
        super.onResume();
        showUpdatePlayServicesDialogIfNecessary();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceivers();
        startService(new Intent(getApplicationContext(), UploadService.class));
    }

    private void registerReceivers() {
        registerReceiver(receiver, new IntentFilter(SYNC_BROADCAST_EVENT));
        registerReceiver(receiver, new IntentFilter(AUTH_FAILURE_BROADCAST_EVENT));
    }

    private void showUpdatePlayServicesDialogIfNecessary(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            ACRA.getErrorReporter().putCustomData("PlayServicesProblem", googleApiAvailability.getErrorString(status));
            ACRA.getErrorReporter().handleSilentException(null);
            ACRA.getErrorReporter().removeCustomData("PlayServicesProblem");
            if ((status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED && GooglePlayServicesUtil.isGPSVersionLowerThanRequired(getPackageManager()))
                || (status != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED && googleApiAvailability.isUserResolvableError(status)
                    && (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Preferences.getUpdatePlayServicesDateShown().getTime()) > 15))){
                Preferences.setUpdatePlayServicesDateShown(new Date());
                googleApiAvailability.getErrorDialog(this, status, UPDATE_PLAY_SERVICES_REQUEST_CODE).show();
            }

            playServicesStatus = status;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_screen, new HomeWallFragment()).commit();
        } else if (requestCode == UPDATE_PLAY_SERVICES_REQUEST_CODE){
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if(status != ConnectionResult.SUCCESS && status != playServicesStatus) {
                Preferences.removeUpdatePlayServicesDateShown();
            }
        }
    }

}
