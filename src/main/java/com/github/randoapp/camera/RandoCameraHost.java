package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.SquareCameraHost;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.task.SendLogTask;
import com.github.randoapp.util.CameraUtil;
import com.github.randoapp.util.FileUtil;

import java.io.File;
import java.util.List;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class RandoCameraHost extends SquareCameraHost {
    private Activity activity;
    private boolean shutterSoundDisabled = false;
    public CameraSizes cameraSizes;

    private List<Camera.Size> pictureSizes;
    private List<Camera.Size> previewSizes;

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
        /*Camera.Size size = CameraUtil.getBestPictureSize(parameters.getSupportedPictureSizes(), getDeviceProfile());*/
        Log.i(CameraCaptureFragment.class, "Selected picture size:", String.valueOf(cameraSizes.pictureSize.height), "x", String.valueOf(cameraSizes.pictureSize.width));
        return cameraSizes.pictureSize;
    }

    @Override
    public Camera.Size getPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
        cameraSizes = CameraUtil.getCameraSizes(parameters, this, width, height, Constants.DESIRED_PICTURE_SIZE, true);

        pictureSizes = parameters.getSupportedPictureSizes();
        previewSizes = parameters.getSupportedPreviewSizes();
        /*WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayWidth = display.getWidth();

        Camera.Size size = CameraUtils.getBestAspectPreviewSize(displayOrientation, displayWidth, (int) (displayWidth / Constants.PICTURE_DESIRED_ASPECT_RATIO), parameters);*/
        Log.i(CameraCaptureFragment.class, "Available Preview screen size:", String.valueOf(height), "x", String.valueOf(width), "display orientation: ", String.valueOf(displayOrientation));
        Log.i(CameraCaptureFragment.class, "Selected preview camera size:", String.valueOf(cameraSizes.previewSize.height), "x", String.valueOf(cameraSizes.previewSize.width));
        return cameraSizes.previewSize;
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
                activity.getResources().getText(R.string.camera_init_failed),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected File getPhotoPath() {
        return FileUtil.getOutputMediaFile();
    }

    @Override
    public void OnImageSaved(File image) {

        PackageManager manager = App.context.getPackageManager();
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(App.context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ex){

        }
        String version = "";
        String versionCode  = "";
        if (info != null){
            version = info.versionName;
            versionCode = String.valueOf(info.versionCode);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Deice: ").append(Build.DEVICE).append(" Manufacturer: ").append(Build.MANUFACTURER)
                .append(" Build.VERSION.CODENAME: ").append(Build.VERSION.CODENAME).append(" Product: ").append(Build.PRODUCT)
                .append(" App version: ").append(version).append(" Code version: ").append(versionCode)
                .append(" Pic size: ").append(cameraSizes.pictureSize.height).append("x").append(cameraSizes.pictureSize.width)
                .append(" Preview size: ").append(cameraSizes.previewSize.height).append("x").append(cameraSizes.previewSize.width)
                .append(" Max Pic Height: ").append(getDeviceProfile().getMaxPictureHeight()).append("Min Pic Height:").append(getDeviceProfile().getMinPictureHeight());

        sb.append("Preview Sizes:{");
        for (Camera.Size size: previewSizes){
            sb.append(size.width).append("x").append(size.height);
        }

        sb.append("},Pic Sizes:{");

        for (Camera.Size size: pictureSizes){
            sb.append(size.width).append("x").append(size.height);
        }

        new SendLogTask(sb.toString()).execute();
        Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
        if (image != null) {
            intent.putExtra(RANDO_PHOTO_PATH, image.getAbsolutePath());
        } else {
            intent.putExtra(RANDO_PHOTO_PATH, "");
        }
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
        if (recordingHint == RecordingHint.NONE) {
            recordingHint = RecordingHint.STILL_ONLY;
        }
        return recordingHint;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !shutterSoundDisabled) {
            disableShutterSound(camera);
        }
        ((CameraView) activity.findViewById(R.id.camera)).takePicture(false, true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void disableShutterSound(Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(getCameraId(), info);
        if (info.canDisableShutterSound) {
            shutterSoundDisabled = camera.enableShutterSound(false);
        }
    }

    @Override
    public void autoFocusAvailable() {
        //temporary enabling button here... until https://github.com/commonsguy/cwac-camera/issues/179 is resolved
        activity.findViewById(R.id.capture_button).setEnabled(true);
    }
}
