package com.github.randoapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.github.randoapp.camera.CameraCaptureFragment;
import com.github.randoapp.camera.CameraUploadFragment;
import com.github.randoapp.camera.RandoCameraHost;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE;
import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class CameraActivity extends SherlockFragmentActivity implements CameraHostProvider {

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String photoPath = (String) extra.get(Constants.RANDO_PHOTO_PATH);
                if (photoPath != null && !photoPath.isEmpty()) {

                    CameraUploadFragment uploadFragment = new CameraUploadFragment();
                    Bundle args = new Bundle();
                    args.putString(Constants.FILEPATH, photoPath);
                    uploadFragment.setArguments(args);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack("CameraCaptureFragment").replace(R.id.camera_screen, uploadFragment).commit();
                    return;
                }
                else  {
                    Toast.makeText(CameraActivity.this, getResources().getText(R.string.image_crop_failed),
                            Toast.LENGTH_LONG).show();
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
        return (new RandoCameraHost(this));
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
