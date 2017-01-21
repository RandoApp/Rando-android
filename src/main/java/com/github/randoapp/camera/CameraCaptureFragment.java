package com.github.randoapp.camera;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.task.CropToSquareImageTask;
import com.github.randoapp.util.Analytics;
import com.google.android.cameraview.CameraView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class CameraCaptureFragment extends Fragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    private CameraView cameraView;
    private ImageView captureButton;
    private Handler mBackgroundHandler;
    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean cameraActive = false;

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

        cameraView = (com.google.android.cameraview.CameraView) rootView.findViewById(R.id.camera);
        cameraView.addCallback(mCallback);


        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());
        captureButton.setEnabled(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CameraCaptureFragment.class, "Camera opened: " + cameraView.isCameraOpened());
        if (cameraView!=null && !cameraActive) {
            cameraView.start();
            cameraActive = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView!=null) {
            cameraView.stop();
            cameraActive = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
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

            /*if (cameraView!= null) {
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
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(CameraView.Callback.class, "onPictureTaken " + data.length);
            PackageManager m = getActivity().getPackageManager();
            String s = getActivity().getPackageName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(CameraView.Callback.class, "Error Package name not found ", e);
            }
            getBackgroundHandler().post(new CropToSquareImageTask(data, getContext()));

        }
    };
}