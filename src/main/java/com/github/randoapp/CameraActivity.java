package com.github.randoapp;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.task.CropToSquareImageTask;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.LocationHelper;
import com.github.randoapp.util.PermissionUtils;
import com.github.randoapp.view.CircleMaskView;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_CAMERA_PERMISSION_REQUIRED;
import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.CAMERA_PERMISSION_REQUEST_CODE;
import static com.github.randoapp.Constants.LOCATION_PERMISSION_REQUEST_CODE;

public class CameraActivity extends Activity {

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String photoPath = (String) extra.get(Constants.RANDO_PHOTO_PATH);
                if (photoPath != null && !photoPath.isEmpty()) {

                    Intent activityIntent = new Intent(CameraActivity.this, ImageReviewUploadActivity.class);
                    activityIntent.putExtra(Constants.FILEPATH, photoPath);
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(activityIntent);

                    finish();
                    return;
                } else {
                    Toast.makeText(CameraActivity.this, getResources().getText(R.string.image_crop_failed),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    private boolean isReturningFromCameraPermissionRequest = false;
    private boolean isReturningFromLocationPermissionRequest = false;

    private CameraView cameraView;
    private ImageView captureButton;
    private ImageButton cameraSwitchButton;
    private ImageButton gridButton;
    private LinearLayout progressBar;
    private Handler mBackgroundHandler;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Animation[] leftToRightAnimation;
    private CircleMaskView circleMaskView;

    private static final SparseArrayCompat<Integer> CAMERA_FACING_ICONS = new SparseArrayCompat<>();

    static {
        CAMERA_FACING_ICONS.put(CameraView.FACING_FRONT, R.drawable.ic_camera_front_white_24dp);
        CAMERA_FACING_ICONS.put(CameraView.FACING_BACK, R.drawable.ic_camera_rear_white_24dp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_capture);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        cameraView = (CameraView) findViewById(R.id.camera);
        cameraView.addCallback(mCallback);
        cameraView.setFlash(CameraView.FLASH_OFF);

        AspectRatio aspectRatio = cameraView.getAspectRatio();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int leftRightMargin = (int) getResources().getDimension(R.dimen.rando_padding_portrait_column_left);

        //make preview height to be aligned with width according to AspectRatio
        int heightRatio = Math.max(aspectRatio.getX(), aspectRatio.getY());
        int widthRatio = Math.min(aspectRatio.getX(), aspectRatio.getY());
        int topBottomMargin = (displayMetrics.heightPixels - (displayMetrics.widthPixels - 2 * leftRightMargin) * heightRatio / widthRatio) / 2;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        layoutParams.setMargins(leftRightMargin, topBottomMargin, leftRightMargin, topBottomMargin);

        cameraView.setLayoutParams(layoutParams);
        cameraView.setFlash(Preferences.getCameraFlashMode());

        Log.d(CameraActivity.class, leftRightMargin + " " + topBottomMargin + " " + cameraView.getAspectRatio() + " ");

        captureButton = (ImageView) findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());
        enableButtons(false);

        progressBar = (LinearLayout) findViewById(R.id.progressBar);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Camera.getNumberOfCameras() > 1) {
            leftToRightAnimation = AnimationFactory.flipAnimation(getResources().getDimensionPixelSize(R.dimen.switch_camera_button_size), AnimationFactory.FlipDirection.LEFT_RIGHT, 150, null);
            cameraSwitchButton = (ImageButton) findViewById(R.id.camera_switch_button);
            RelativeLayout.LayoutParams cameraSwitchButtonLayoutParams = (RelativeLayout.LayoutParams) cameraSwitchButton.getLayoutParams();
            int marginLeft = (displayMetrics.widthPixels - getResources().getDimensionPixelSize(R.dimen.rando_button_size)) / 4 - getResources().getDimensionPixelSize(R.dimen.switch_camera_button_size) / 2;
            cameraSwitchButtonLayoutParams.setMargins(marginLeft, 0, 0, getResources().getDimensionPixelSize(R.dimen.switch_camera_margin_bottom));
            cameraSwitchButton.setLayoutParams(cameraSwitchButtonLayoutParams);
            cameraView.setFacing(Preferences.getCameraFacing());
            cameraSwitchButton.setImageResource(CAMERA_FACING_ICONS.get(cameraView.getFacing()));
            cameraSwitchButton.setVisibility(View.VISIBLE);
            cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableButtons(false);
                    if (cameraView != null) {
                        int facing = cameraView.getFacing() == CameraView.FACING_FRONT ?
                                CameraView.FACING_BACK : CameraView.FACING_FRONT;
                        imageViewAnimatedChange(cameraSwitchButton, CAMERA_FACING_ICONS.get(facing));
                        enableButtons(false);
                        cameraView.setFacing(facing);
                        Preferences.setCameraFacing(facing);
                    }
                }
            });
        }
        circleMaskView = (CircleMaskView) findViewById(R.id.circle_mask);
        circleMaskView.setDrawGrid(Preferences.getCameraGrid());
        gridButton = (ImageButton) findViewById(R.id.grid_button);
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleMaskView.setDrawGrid(!circleMaskView.isDrawGrid());
                circleMaskView.invalidate();
                Preferences.setCameraGrid(circleMaskView.isDrawGrid());
                updateGridIcon();
            }
        });
        updateGridIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(CAMERA_BROADCAST_EVENT));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isReturningFromCameraPermissionRequest) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraView.start();
            } else {
                setResult(CAMERA_ACTIVITY_CAMERA_PERMISSION_REQUIRED);
                finish();
            }
            isReturningFromCameraPermissionRequest = false;
        } else {
            if (!PermissionUtils.checkAndRequestMissingPermissions(this, CAMERA_PERMISSION_REQUEST_CODE, android.Manifest.permission.CAMERA)) {
                cameraView.start();
            }
        }

        if (isReturningFromLocationPermissionRequest) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            }
        } else {
            if (!PermissionUtils.checkAndRequestMissingPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                updateLocation();
            }
        }
        isReturningFromLocationPermissionRequest = false;
    }

    private void enableButtons(boolean enable) {
        captureButton.setEnabled(enable);
        if (cameraSwitchButton != null) {
            cameraSwitchButton.setEnabled(enable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
        if (progressBar.getVisibility() != View.GONE) {
            progressBar.setVisibility(View.GONE);
        }
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ((grantResults.length > 0) && (permissions.length > 0)) {
            switch (requestCode) {
                case CAMERA_PERMISSION_REQUEST_CODE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        isReturningFromCameraPermissionRequest = true;
                    } else {
                        setResult(CAMERA_ACTIVITY_CAMERA_PERMISSION_REQUIRED);
                        finish();
                    }
                    break;
                case LOCATION_PERMISSION_REQUEST_CODE:
                    isReturningFromLocationPermissionRequest = true;
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            new AlertDialog.Builder(this).setTitle(R.string.location_needed_title).setMessage(R.string.location_needed_message).setPositiveButton(R.string.permission_positive_button, null).create().show();
                        } else {
                            updateLocation();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void updateLocation() {
        if (LocationHelper.isGpsEnabled(this)) {

            LocationHelper locationHelper = new LocationHelper(this);
            locationHelper.updateLocationAsync();
        } else {
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
        }
    }

    private void updateGridIcon(){
        if (circleMaskView.isDrawGrid()) {
            gridButton.setBackgroundResource(R.drawable.ic_grid_on_gray_36dp);
        } else {
            gridButton.setBackgroundResource(R.drawable.ic_grid_off_gray_36dp);
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private class CaptureButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(CameraActivity.class, "Take Pic Click ");

            enableButtons(false);
            cameraView.takePicture();
            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
            }
            if (Build.VERSION.SDK_INT >= 11) {
                cameraView.setAlpha(0.7f);
            }
            Analytics.logTakeRando(mFirebaseAnalytics);
        }
    }

    private void imageViewAnimatedChange(final ImageView v, final int imageResource) {
        final Animation anim_out = leftToRightAnimation[0];
        final Animation anim_in = leftToRightAnimation[1];
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Do nothing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageResource(imageResource);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //Do nothing
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //Do nothing
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //Do nothing
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(CameraView.Callback.class, "onCameraOpened" + Thread.currentThread());
            enableButtons(true);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(CameraView.Callback.class, "onCameraClosed");
            enableButtons(false);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(CameraView.Callback.class, "onPictureTaken " + data.length + "Thread " + Thread.currentThread());
            cameraView.stop();
            getBackgroundHandler().post(new CropToSquareImageTask(data, cameraView.getFacing() == CameraView.FACING_FRONT, getBaseContext()));
            progressBar.setVisibility(View.VISIBLE);
        }

    };
}
