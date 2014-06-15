package com.github.randoapp;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.github.randoapp.camera.CameraCaptureFragment;
import com.github.randoapp.camera.CameraUploadFragment;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.task.LocationUpdateTask;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.LocationHelper;
import com.github.randoapp.util.LocationUpdater;

import java.util.Map;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE;
import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class CameraActivity extends SherlockFragmentActivity implements CameraHostProvider {

    private LocationHelper locationHelper;
    public static Location currentLocation;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String photoPath = (String) extra.get(Constants.RANDO_PHOTO_PATH);
                if (photoPath != null) {

                    CameraUploadFragment uploadFragment = new CameraUploadFragment();
                    Bundle args = new Bundle();
                    args.putString(Constants.FILEPATH, photoPath);
                    uploadFragment.setArguments(args);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack("CameraCaptureFragment").replace(R.id.camera_screen, uploadFragment).commit();
                    return;
                }
            }

            CameraActivity.this.setResult(CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE);
            CameraActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.camera_screen, CameraCaptureFragment.newInstance(false))
                    .commit();
        }
    }

    @Override
    public CameraHost getCameraHost() {
        return (new SimpleCameraHost(this));
    }

    private void updateLocation() {
        int locationMode = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCATION_MODE, 0);
        if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.no_location_services))
                    .setPositiveButton(getResources().getString(R.string.enable_location_services),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    startActivity(new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }
                    )
                    .setNegativeButton(getResources().getString(R.string.close_dialog),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }
                    ).create().show();
        } else {
            locationHelper = new LocationHelper(this);
            new LocationUpdateTask(locationHelper).onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    currentLocation = locationHelper.getLocation();
                    Preferences.setLocation(currentLocation);
                }
            }).execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocation();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(CAMERA_BROADCAST_EVENT));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    //TODO: onDestroy vs onPause: Do we really need unregisterReceiver on Destroy event?
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

}
