package com.github.randoapp.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.github.randoapp.R;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class CameraCaptureFragment extends CameraFragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

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

        CameraView cameraView = (CameraView) rootView.findViewById(R.id.camera);
        setCameraView(cameraView);

        ImageView captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());

        return rootView;
    }

    private class CaptureButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO: impement capture button listener

            Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
            intent.putExtra(RANDO_PHOTO_PATH, "photo path");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }

}