package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.CameraUtil;
import com.github.randoapp.util.FileUtil;
import com.github.randoapp.util.LocationHelper;

import java.util.List;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.JPEG_QUALITY;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class CameraCaptureFragment extends CameraFragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    private CameraView cameraView;
    private ImageView captureButton;
    private boolean shutterSoundDisabled = false;

    private int displayWidth;

    public static CameraCaptureFragment newInstance(boolean useFFC) {
        CameraCaptureFragment fragment = new CameraCaptureFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_USE_FFC, useFFC);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_capture, container, false);

        cameraView = (CameraView) rootView.findViewById(R.id.camera);
        cameraView.setHost(new RandoCameraHost(getActivity().getBaseContext()));

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        displayWidth = display.getWidth();

        setCameraView(cameraView);

        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocation();
    }

    private class CaptureButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            captureButton.setEnabled(false);
            if (isAutoFocusAvailable()) {
                autoFocus();
            } else {
                cameraView.takePicture(false, true);
            }
        }
    }

    private void updateLocation() {
        if (LocationHelper.isGpsEnabled(getActivity())){

            LocationHelper locationHelper = new LocationHelper(getActivity());
            locationHelper.updateLocationAsync();
    }
    else
    {
        new AlertDialog.Builder(getActivity())
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

class RandoCameraHost extends SimpleCameraHost {

    public RandoCameraHost(Context _ctx) {
        super(_ctx);
    }

    @Override
    public boolean useSingleShotMode() {
        return true;
    }

    @Override
    public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
        Camera.Size size = CameraUtil.getBestPictureSizeForOldDevices(parameters.getSupportedPictureSizes(), getDeviceProfile());
        Log.i(CameraCaptureFragment.class, "Selected picture size:", String.valueOf(size.height), "x", String.valueOf(size.width));
        return size;
    }

    @Override
    public Camera.Size getPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
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
        Toast.makeText(getActivity(),
                "Sorry, but you cannot use the camera now!",
                Toast.LENGTH_LONG).show();
    }


    @Override
    public Camera.Parameters adjustPictureParameters(PictureTransaction xact, Camera.Parameters parameters) {
        parameters.setJpegQuality(JPEG_QUALITY);
        return parameters;
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
        final String tmpFile = FileUtil.writeImageToTempFile(image);
        Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
        intent.putExtra(RANDO_PHOTO_PATH, tmpFile);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
        cameraView.takePicture(false, true);
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
}