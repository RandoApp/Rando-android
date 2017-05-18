package com.github.randoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.YuvImage;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flurgle.camerakit.AspectRatio;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.flurgle.camerakit.Facing;
import com.flurgle.camerakit.Size;
import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.task.CropToSquareImageTask;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.LocationHelper;
import com.github.randoapp.util.PermissionUtils;
import com.github.randoapp.view.CircleMaskView;
import com.github.randoapp.view.FocusMarkerLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_CAMERA_PERMISSION_REQUIRED;
import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.CAMERA_PERMISSION_REQUEST_CODE;
import static com.github.randoapp.Constants.LOCATION_PERMISSION_REQUEST_CODE;

public class CameraActivity16 extends Activity {

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String photoPath = (String) extra.get(Constants.RANDO_PHOTO_PATH);
                if (photoPath != null && !photoPath.isEmpty()) {

                    Intent activityIntent = new Intent(CameraActivity16.this, ImageReviewUploadActivity.class);
                    activityIntent.putExtra(Constants.FILEPATH, photoPath);
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(activityIntent);

                    finish();
                    return;
                } else {
                    Toast.makeText(CameraActivity16.this, getResources().getText(R.string.image_crop_failed),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private CameraActivity16.UnexpectedTerminationHelper mUnexpectedTerminationHelper = new CameraActivity16.UnexpectedTerminationHelper();

    private boolean isReturningFromCameraPermissionRequest = false;
    private boolean isReturningFromLocationPermissionRequest = false;

    private CameraView cameraView;
    private int mCameraViewleftRightMargin = 0;
    private int mCameraViewtopBottomMargin = 0;
    private ImageView captureButton;
    private ImageView cameraSwitchButton;
    private ImageView gridButton;
    private LinearLayout progressBar;
    private Handler mBackgroundHandler;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Animation[] leftToRightAnimation;
    private CircleMaskView circleMaskView;
    private FocusMarkerLayout focusMarker;
    private CropToSquareImageTask mCropTask;
    @Facing
    private int mCurrentFacing;

    private static final SparseArrayCompat<Integer> CAMERA_FACING_ICONS = new SparseArrayCompat<>();

    static {
        CAMERA_FACING_ICONS.put(CameraKit.Constants.FACING_FRONT, R.drawable.ic_camera_front_white_24dp);
        CAMERA_FACING_ICONS.put(CameraKit.Constants.FACING_BACK, R.drawable.ic_camera_rear_white_24dp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_capture16);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        cameraView = (CameraView) findViewById(R.id.camera);
        cameraView.setCameraListener(mCallback);
        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP);

        focusMarker = (FocusMarkerLayout) findViewById(R.id.focusMarker);

        cameraView.setFlash(Preferences.getCameraFlashMode());

        //Log.d(CameraActivity16.class, leftRightMargin + " " + topBottomMargin + " " + cameraView.getAspectRatio() + " ");

        captureButton = (ImageView) findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CameraActivity16.CaptureButtonListener());
        enableButtons(false);

        progressBar = (LinearLayout) findViewById(R.id.progressBar);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int buttonsSideMargin = (displayMetrics.widthPixels - getResources().getDimensionPixelSize(R.dimen.rando_button_size)) / 4 - getResources().getDimensionPixelSize(R.dimen.switch_camera_button_size) / 2;
        leftToRightAnimation = AnimationFactory.flipAnimation(getResources().getDimensionPixelSize(R.dimen.switch_camera_button_size), AnimationFactory.FlipDirection.LEFT_RIGHT, 150, null);
        if (Camera.getNumberOfCameras() > 1) {
            cameraSwitchButton = (ImageView) findViewById(R.id.camera_switch_button);
            RelativeLayout.LayoutParams cameraSwitchButtonLayoutParams = (RelativeLayout.LayoutParams) cameraSwitchButton.getLayoutParams();
            cameraSwitchButtonLayoutParams.setMargins(buttonsSideMargin, 0, 0, getResources().getDimensionPixelSize(R.dimen.switch_camera_margin_bottom));
            cameraSwitchButton.setLayoutParams(cameraSwitchButtonLayoutParams);
            mCurrentFacing = Preferences.getCameraFacing();
            cameraView.setFacing(mCurrentFacing);
            cameraSwitchButton.setImageResource(CAMERA_FACING_ICONS.get(mCurrentFacing));
            cameraSwitchButton.setVisibility(View.VISIBLE);
            cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableButtons(false);
                    if (cameraView != null) {
                        int facing;
                        if (mCurrentFacing == CameraKit.Constants.FACING_FRONT) {
                            facing = CameraKit.Constants.FACING_BACK;
                            Analytics.logSwitchCameraToBack(mFirebaseAnalytics);
                        } else {
                            facing = CameraKit.Constants.FACING_FRONT;
                            Analytics.logSwitchCameraToFront(mFirebaseAnalytics);
                        }
                        imageViewAnimatedChange(cameraSwitchButton, CAMERA_FACING_ICONS.get(facing), 0, null);
                        enableButtons(false);
                        cameraView.setFacing(facing);
                        mCurrentFacing = facing;
                        Preferences.setCameraFacing(facing);
                    }
                }
            });
        }
        circleMaskView = (CircleMaskView) findViewById(R.id.circle_mask);
        circleMaskView.setDrawGrid(Preferences.getCameraGrid());
        circleMaskView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mCurrentFacing == CameraKit.Constants.FACING_FRONT) {
                            return true;
                        }
                        Log.d(CameraActivity16.class, event.toString());
                        int size = Math.min(displayMetrics.heightPixels, displayMetrics.widthPixels);
                        float radius = size / 2.0f;
                        float delta = (displayMetrics.heightPixels - displayMetrics.widthPixels) / 2;
                        float eX = event.getX() - radius;
                        float eY = event.getY() - radius - delta;
                        float vector = (float) Math.sqrt(eX * eX + eY * eY);
                        if (vector < radius) {
                            focusMarker.focus(event.getX(), event.getY(), mCameraViewleftRightMargin, mCameraViewtopBottomMargin);
                            return false;
                        }
                        return true;
                    }
                }
        );
        gridButton = (ImageView) findViewById(R.id.grid_button);
        RelativeLayout.LayoutParams gridButtonLayoutParams = (RelativeLayout.LayoutParams) gridButton.getLayoutParams();
        gridButtonLayoutParams.setMargins(0, 0, buttonsSideMargin, getResources().getDimensionPixelSize(R.dimen.switch_camera_margin_bottom));
        gridButton.setLayoutParams(gridButtonLayoutParams);
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleMaskView.setDrawGrid(!circleMaskView.isDrawGrid());
                Preferences.setCameraGrid(circleMaskView.isDrawGrid());
                CameraActivity16.OnAnimationEnd onAnimationEnd = new CameraActivity16.OnAnimationEnd() {
                    @Override
                    public void onEnd() {
                        circleMaskView.invalidate();
                    }
                };
                if (circleMaskView.isDrawGrid()) {
                    imageViewAnimatedChange(gridButton, R.drawable.ic_grid_on_white_24dp, R.drawable.switch_camera_background, onAnimationEnd);
                } else {
                    imageViewAnimatedChange(gridButton, R.drawable.ic_grid_off_white_24dp, R.drawable.camera_action_button_background_off, onAnimationEnd);
                }
            }
        });
        setupGridIcon();
    }

    private void adjustPreviewSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        Size size = cameraView.getPreviewSize();
        AspectRatio aspectRatio = AspectRatio.of(size.getWidth(), size.getHeight());

        mCameraViewleftRightMargin = (int) getResources().getDimension(R.dimen.rando_padding_portrait_column_left);

        //make preview height to be aligned with width according to AspectRatio
        int heightRatio = Math.max(aspectRatio.getX(), aspectRatio.getY());
        int widthRatio = Math.min(aspectRatio.getX(), aspectRatio.getY());
        mCameraViewtopBottomMargin = (displayMetrics.heightPixels - (displayMetrics.widthPixels - 2 * mCameraViewleftRightMargin) * heightRatio / widthRatio) / 2;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        layoutParams.setMargins(mCameraViewleftRightMargin, mCameraViewtopBottomMargin, mCameraViewleftRightMargin, mCameraViewtopBottomMargin);
        cameraView.setLayoutParams(layoutParams);
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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mUnexpectedTerminationHelper.init();
                cameraView.start();
            } else {
                setResult(CAMERA_ACTIVITY_CAMERA_PERMISSION_REQUIRED);
                finish();
            }
            isReturningFromCameraPermissionRequest = false;
        } else {
            if (!PermissionUtils.checkAndRequestMissingPermissions(this, CAMERA_PERMISSION_REQUEST_CODE, android.Manifest.permission.CAMERA)) {
                mUnexpectedTerminationHelper.init();
                cameraView.start();
            }
        }

        if (isReturningFromLocationPermissionRequest) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            }
        } else {
            if (!PermissionUtils.checkAndRequestMissingPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
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

    private void setupGridIcon() {
        if (circleMaskView.isDrawGrid()) {
            gridButton.setImageResource(R.drawable.ic_grid_on_white_24dp);
            gridButton.setBackgroundResource(R.drawable.switch_camera_background);
        } else {
            gridButton.setImageResource(R.drawable.ic_grid_off_white_24dp);
            gridButton.setBackgroundResource(R.drawable.camera_action_button_background_off);
        }
        circleMaskView.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
        mUnexpectedTerminationHelper.fini();
        if (progressBar.getVisibility() != View.GONE) {
            progressBar.setVisibility(View.GONE);
        }
        stopCropTask();
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
            Log.d(CameraActivity16.class, "Take Pic Click ");
            stopCropTask();
            enableButtons(false);
            cameraView.captureImage();
            if (Preferences.getEnableVibrate()
                    && ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
            }
            Analytics.logTakeRando(mFirebaseAnalytics);
        }
    }

    private void stopCropTask() {
        if (mCropTask != null) {
            mCropTask.cancel();
        }
        mCropTask = null;
    }

    private void imageViewAnimatedChange(final ImageView v, final int imageResource, final int backgroundResource, final CameraActivity16.OnAnimationEnd onAnimationEnd) {
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
                if (backgroundResource > 0) {
                    v.setBackgroundResource(backgroundResource);
                }
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
                        if (onAnimationEnd != null) {
                            onAnimationEnd.onEnd();
                        }
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    private CameraListener mCallback
            = new CameraListener() {
        @Override
        public void onCameraOpened() {
            adjustPreviewSize();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      enableButtons(true);
                                                  }
                                              },
                            500);
                }
            });
        }

        @Override
        public void onCameraClosed() {
            Log.d(CameraListener.class, "onCameraClosed");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    enableButtons(false);
                }
            });
        }

        @Override
        public void onPictureTaken(byte[] jpeg) {
            Log.d(CameraListener.class, "onPictureTaken " + jpeg.length + "Thread " + Thread.currentThread());
            cameraView.stop();
            mCropTask = new CropToSquareImageTask(jpeg, mCurrentFacing == CameraKit.Constants.FACING_FRONT, getBaseContext());
            getBackgroundHandler().post(mCropTask);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPictureTaken(YuvImage yuv) {
            super.onPictureTaken(yuv);
        }

        @Override
        public void onVideoTaken(File video) {
            super.onVideoTaken(video);
        }
    };

    private abstract class OnAnimationEnd {
        public abstract void onEnd();
    }

    private class UnexpectedTerminationHelper {
        private Thread mThread;
        private Thread.UncaughtExceptionHandler mOldUncaughtExceptionHandler = null;
        private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) { // gets called on the same (main) thread
                cameraView.stop();
                if (mOldUncaughtExceptionHandler != null) {
                    // it displays the "force close" dialog
                    mOldUncaughtExceptionHandler.uncaughtException(thread, ex);
                }
            }
        };

        public void init() {
            mThread = Thread.currentThread();
            mOldUncaughtExceptionHandler = mThread.getUncaughtExceptionHandler();
            mThread.setUncaughtExceptionHandler(mUncaughtExceptionHandler);
        }

        public void fini() {
            if (mThread != null) {
                mThread.setUncaughtExceptionHandler(mOldUncaughtExceptionHandler);
            }
            mOldUncaughtExceptionHandler = null;
            mThread = null;
        }
    }
}
