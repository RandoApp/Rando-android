package com.github.randoapp.camera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.Analytics;
import com.google.android.cameraview.CameraView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class CameraCaptureFragment extends Fragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    CameraView cameraView;
    private ImageView captureButton;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        cameraView = (com.google.android.cameraview.CameraView) rootView.findViewById(R.id.camera1);
        cameraView.addCallback(mCallback);

        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());
        captureButton.setEnabled(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraView!=null) {
            cameraView.setFocusableInTouchMode(true);
            cameraView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView!=null) {
            cameraView.stop();
        }
    }

    private class CaptureButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

           /* if (cameraView!= null) {
                int facing = cameraView.getFacing();
                cameraView.setFacing(facing == CameraView.FACING_FRONT ?
                        CameraView.FACING_BACK : CameraView.FACING_FRONT);
            }*/

            //captureButton.setEnabled(false);
            cameraView.takePicture();
            Analytics.logTakeRando(mFirebaseAnalytics);
        }
    }


    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(CameraView.Callback.class, "onCameraOpened");
            captureButton.setEnabled(true);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(CameraView.Callback.class, "onCameraClosed");
            //captureButton.setEnabled(false);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(CameraView.Callback.class, "onPictureTaken " + data.length);
           /* Toast.makeText(cameraView.getContext(), R.string.picture_taken, Toast.LENGTH_SHORT)
                    .show();
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "picture.jpg");
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                }
            });*/
        }

    };
}