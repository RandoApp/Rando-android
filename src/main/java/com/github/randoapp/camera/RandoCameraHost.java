package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.SquareCameraHost;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.CameraUtil;
import com.github.randoapp.util.FileUtil;

import java.io.File;
import java.util.List;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class RandoCameraHost extends SquareCameraHost {
    private Activity activity;
    private boolean shutterSoundDisabled = false;

    public RandoCameraHost(Activity activity) {
        super(activity.getBaseContext());
        this.activity = activity;
    }

    @Override
    public boolean useSingleShotMode() {
        return true;
    }

    @Override
    public Camera.Size getPictureSize(Camera.Parameters parameters) {
        Camera.Size size = CameraUtil.getBestPictureSizeForOldDevices(parameters.getSupportedPictureSizes(), getDeviceProfile());
        Log.i(CameraCaptureFragment.class, "Selected picture size:", String.valueOf(size.height), "x", String.valueOf(size.width));
        return size;
    }

    @Override
    public Camera.Size getPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayWidth = display.getWidth();

        Camera.Size size = CameraUtils.getBestAspectPreviewSize(displayOrientation, displayWidth, (int) (displayWidth / Constants.PICTURE_DESIRED_ASPECT_RATIO), parameters);
        Log.i(CameraCaptureFragment.class, "Available Preview screen size:", String.valueOf(height), "x", String.valueOf(width), "display orientation: ", String.valueOf(displayOrientation));
        Log.i(CameraCaptureFragment.class, "Selected preview camera size:", String.valueOf(size.height), "x", String.valueOf(size.width));
        return size;
    }

    @Override
    public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        return parameters;
    }

    @Override
    public void onCameraFail(CameraHost.FailureReason reason) {
        super.onCameraFail(reason);
        Toast.makeText(activity,
                "Sorry, but you cannot use the camera now!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected File getPhotoPath() {
        return FileUtil.getOutputMediaFile();
    }

    @Override
    public void OnImageSaved(File image) {
        Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
        intent.putExtra(RANDO_PHOTO_PATH, image.getAbsolutePath());
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    @Override
    protected boolean useFrontFacingCamera() {
        return false;
    }

    @Override
    public boolean useFullBleedPreview() {
        return false;
    }

    @Override
    public RecordingHint getRecordingHint() {
        RecordingHint recordingHint = getDeviceProfile().getDefaultRecordingHint();
        if (recordingHint==RecordingHint.NONE) {
            recordingHint=RecordingHint.STILL_ONLY;
        }
        return recordingHint;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (!shutterSoundDisabled) {
            disableShutterSound(camera);
        }
        ((CameraView) activity.findViewById(R.id.camera)).takePicture(false, true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void disableShutterSound(Camera camera){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(getCameraId(), info);
        if (info.canDisableShutterSound) {
            shutterSoundDisabled = camera.enableShutterSound(false);
        }
    }

}
