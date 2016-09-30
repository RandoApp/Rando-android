package com.github.randoapp.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.LocationHelper;
import com.github.randoapp.util.PermissionUtils;

import static com.github.randoapp.Constants.LOCATION_PERMISSION_REQUEST_CODE;

public class CameraCaptureFragment extends CameraFragment {
    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    private CameraView cameraView;
    private ImageView captureButton;

    private boolean requestLoacation = true;

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

        setCameraView(cameraView);

        captureButton = (ImageView) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new CaptureButtonListener());
        captureButton.setEnabled(false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestLoacation && !PermissionUtils.checkAndRequestMissingPermissions(getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            updateLocation();
        }
        requestLoacation = false;
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
                }
            } catch (Exception e) {
                Log.w(CameraCaptureFragment.class, "Can not take picture, because: ", e.getMessage());
            }
        }
    }

    private void updateLocation() {
        if (LocationHelper.isGpsEnabled(getActivity())) {

            LocationHelper locationHelper = new LocationHelper(getActivity());
            locationHelper.updateLocationAsync();
        } else {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ((grantResults.length > 0) && (permissions.length > 0)) {
            if (LOCATION_PERMISSION_REQUEST_CODE == requestCode) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
                        new android.support.v7.app.AlertDialog.Builder(getActivity()).setTitle(R.string.location_needed_title).setMessage(R.string.location_needed_message).setPositiveButton(R.string.permission_positive_button, null).create().show();
                    }
                } else {
                    updateLocation();
                }
            }
        }
    }

}