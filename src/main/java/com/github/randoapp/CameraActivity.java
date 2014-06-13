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
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.github.randoapp.camera.CameraCaptureFragment;
import com.github.randoapp.camera.CameraUploadFragment;
import com.github.randoapp.util.LocationHelper;
import com.github.randoapp.util.LocationUpdater;

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
            CameraActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocation();
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
        // get location providers
            int locationMode = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.LOCATION_MODE, 0);
            if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {

                // build a new alert dialog to inform the user that they have no
                // location services enabled
                new AlertDialog.Builder(this)

                        //set the message to display to the user
                        .setMessage("No Location Services Enabled")

                                // add the 'positive button' to the dialog and give it a
                                // click listener
                        .setPositiveButton("Enable Location Services",
                                new DialogInterface.OnClickListener() {
                                    // setup what to do when clicked
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // start the settings menu on the correct
                                        // screen for the user
                                        startActivity(new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }

                                    // add the 'negative button' to the dialog and
                                    // give it a click listener
                                }
                        )
                        .setNegativeButton("Close",
                                new DialogInterface.OnClickListener() {
                                    // setup what to do when clicked
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // remove the dialog
                                        dialog.cancel();
                                    }

                                    // finish creating the dialog and show to the
                                    // user
                                }
                        ).create().show();
            }
        locationHelper = new LocationHelper(this);
        LocationWorker locationTask = new LocationWorker();
        locationTask .execute();
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


    /***
     * This task waits for the Location Services helper to acquire a location in a worker thread
     * so that we don't lock the UI thread whilst waiting.
     *
     * @author Scott Helme
     */
    class LocationWorker extends AsyncTask<Boolean, Integer, Boolean> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onPostExecute(Boolean result) {
		    currentLocation = locationHelper.getLocation();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {

            //while the location helper has not got a lock
            while(locationHelper.gotLocation() == false){
                //do nothing, just wait
            }
            return true;
        }
    }
}
