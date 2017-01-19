package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.CameraUtil;
import com.github.randoapp.util.FileUtil;

import java.io.File;
import java.util.List;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class RandoCameraHost {
    private Activity activity;
    private boolean shutterSoundDisabled = false;
    public CameraSizes cameraSizes;

    public RandoCameraHost(Activity activity) {
        this.activity = activity;
    }

    public boolean useSingleShotMode() {
        return true;
    }

    public Camera.Size getPictureSize(Camera.Parameters parameters) {
        Log.i(CameraCaptureFragment.class, "Selected picture size:", String.valueOf(cameraSizes.pictureSize.height), "x", String.valueOf(cameraSizes.pictureSize.width));
        return cameraSizes.pictureSize;
    }

    public Camera.Size getPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
        cameraSizes = CameraUtil.getCameraSizes(parameters, width, height, Constants.DESIRED_PICTURE_SIZE);

        Log.i(CameraCaptureFragment.class, "Available Preview screen size:", String.valueOf(height), "x", String.valueOf(width), "display orientation: ", String.valueOf(displayOrientation));
        Log.i(CameraCaptureFragment.class, "Selected preview camera size:", String.valueOf(cameraSizes.previewSize.height), "x", String.valueOf(cameraSizes.previewSize.width));
        return cameraSizes.previewSize;
    }

    public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        }
        return parameters;
    }

    protected File getPhotoPath() {
        return FileUtil.getOutputMediaFile();
    }

    public void OnImageSaved(File image) {
        Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
        if (image != null) {
            intent.putExtra(RANDO_PHOTO_PATH, image.getAbsolutePath());
        } else {
            intent.putExtra(RANDO_PHOTO_PATH, "");
        }
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    protected boolean useFrontFacingCamera() {
        return false;
    }

    public boolean useFullBleedPreview() {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void disableShutterSound(Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        //Camera.getCameraInfo(getCameraId(), info);
        if (info.canDisableShutterSound) {
            shutterSoundDisabled = camera.enableShutterSound(false);
        }
    }

    public void previewReady(Camera camera) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !shutterSoundDisabled) {
            disableShutterSound(camera);
        }
        activity.findViewById(R.id.capture_button).setEnabled(true);
    }

    public void onAutoFocus(boolean success, Camera camera) {
        //((CameraView) activity.findViewById(R.id.camera)).takePicture(false, true);
    }
}
