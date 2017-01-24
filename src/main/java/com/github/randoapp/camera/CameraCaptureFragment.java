package com.github.randoapp.camera;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.task.CropToSquareImageTask;
import com.github.randoapp.util.Analytics;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class CameraCaptureFragment extends Fragment {

    private CameraView cameraView;
    private ImageView captureButton;
    private Handler mBackgroundHandler;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static CameraCaptureFragment newInstance() {
        return  new CameraCaptureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_capture, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        cameraView = (com.google.android.cameraview.CameraView) rootView.findViewById(R.id.camera);
        cameraView.addCallback(mCallback);

        AspectRatio aspectRatio = cameraView.getAspectRatio();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int leftRightMargin = (int) getResources().getDimension(R.dimen.rando_padding_portrait_column_left);

        //make preview height to be aligned with width according to AspectRatio
        int heightRatio = Math.max(aspectRatio.getX(),aspectRatio.getY());
        int widthRatio = Math.min(aspectRatio.getX(),aspectRatio.getY());
        int topBottomMargin = (displayMetrics.heightPixels - (displayMetrics.widthPixels-2*leftRightMargin)*heightRatio/widthRatio)/2;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        layoutParams.setMargins(leftRightMargin, topBottomMargin,leftRightMargin, topBottomMargin);

        cameraView.setLayoutParams(layoutParams);

        Log.d(CameraCaptureFragment.class, leftRightMargin + " " + topBottomMargin + " " + cameraView.getAspectRatio() + " ");

        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());
        captureButton.setEnabled(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CameraCaptureFragment.class, "Camera opened: " + cameraView.isCameraOpened());
        if (cameraView!=null) {
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
            Log.d(CameraCaptureFragment.class, "Take Pic Click ");

            captureButton.setEnabled(false);
            cameraView.takePicture();
            cameraView.setAlpha(0.7f);
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
            getBackgroundHandler().post(new CropToSquareImageTask(data, getContext()));

        }
    };
}
