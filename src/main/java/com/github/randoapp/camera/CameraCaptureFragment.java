package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.github.randoapp.CameraActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.task.CropImageTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.CameraUtil;
import com.github.randoapp.util.FileUtil;

import java.io.File;
import java.util.Map;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.JPEG_QUALITY;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class CameraCaptureFragment extends CameraFragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    private CameraView cameraView;
    private ImageView captureButton;

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
        setCameraView(cameraView);

        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());

        return rootView;
    }

    private class CaptureButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            cameraView.takePicture(false, true);
        }
    }

    class RandoCameraHost extends SimpleCameraHost {
        public RandoCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        public boolean useSingleShotMode() {
            return true;
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return CameraUtil.getBestPictureSizeForOldDevices(parameters.getSupportedPictureSizes());
        }

        @Override
        protected File getPhotoDirectory() {
            return FileUtil.getOutputMediaDir();
        }

        @Override
        public Camera.ShutterCallback getShutterCallback() {
            return super.getShutterCallback();
        }

        @Override
        public void onCameraFail(CameraHost.FailureReason reason) {
            super.onCameraFail(reason);

            Toast.makeText(getActivity(),
                    "Sorry, but you cannot use the camera now!",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        @TargetApi(16)
        public void onAutoFocus(boolean success, Camera camera) {
            super.onAutoFocus(success, camera);
            captureButton.setEnabled(true);
        }

        @Override
        public Camera.Parameters adjustPictureParameters(PictureTransaction xact, Camera.Parameters parameters) {
            parameters.setJpegQuality(JPEG_QUALITY);
            return parameters;
        }


        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            final String tmpFile = FileUtil.writeImageToTempFile(image);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((CameraActivity) getActivity()).showProgressbar("Cropping...");
                    new CropImageTask(tmpFile)
                            .onOk(new OnOk() {
                                @Override
                                public void onOk(Map<String, Object> data) {
                                    String picFileName = (String) data.get(Constants.FILEPATH);
                                    Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
                                    intent.putExtra(RANDO_PHOTO_PATH, picFileName);
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                }
                            })
                            .onError(new OnError() {
                                @Override
                                public void onError(Map<String, Object> data) {
                                    Toast.makeText(getActivity(), "Crop Failed.",
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                            .onDone(new OnDone() {
                                @Override
                                public void onDone(Map<String, Object> data) {
                                    ((CameraActivity) getActivity()).hideProgressbar();

                                }
                            })
                            .execute();
                }
            });
        }
    }
}