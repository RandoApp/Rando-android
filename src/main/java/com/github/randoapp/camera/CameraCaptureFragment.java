package com.github.randoapp.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.Analytics;
import com.google.firebase.analytics.FirebaseAnalytics;

public class CameraCaptureFragment extends CameraFragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    private CameraView cameraView;
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

        cameraView = (CameraView) rootView.findViewById(R.id.camera);
        setCameraView(cameraView);

        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());
        captureButton.setEnabled(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private class CaptureButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            captureButton.setEnabled(false);
            try {
                if (isAutoFocusAvailable() && cameraView.getFocushMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    autoFocus();
                } else {
                    cameraView.takePicture(false, true);
                    Analytics.logTakeRando(mFirebaseAnalytics);
                }
            } catch (Exception e) {
                Log.w(CameraCaptureFragment.class, "Can not take picture, because: ", e.getMessage());
            }
        }
    }
}