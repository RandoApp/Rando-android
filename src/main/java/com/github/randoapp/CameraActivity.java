package com.github.randoapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.github.randoapp.camera.CameraCaptureFragment;
import com.github.randoapp.camera.CameraUploadFragment;
import com.github.randoapp.util.LocationUpdater;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class CameraActivity extends SherlockFragmentActivity implements CameraHostProvider {

    private LocationUpdater locationUpdater = new LocationUpdater();
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
                    fragmentManager.beginTransaction().replace(R.id.camera_screen, uploadFragment).commit();
                    return;
                }
            }
            CameraActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocation();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        LocationUpdater.LocationResult locationResult = new LocationUpdater.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                currentLocation = location;
            }
        };
        locationUpdater.getLocation(getApplicationContext(), locationResult);
    }

    @Override
    public void onResume() {
        super.onResume();
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
