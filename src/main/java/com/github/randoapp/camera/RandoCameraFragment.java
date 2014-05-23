package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.github.randoapp.R;

public class RandoCameraFragment extends CameraFragment {
    private static final String KEY_USE_FFC=
            "com.commonsware.cwac.camera.demo.USE_FFC";
    /*private MenuItem singleShotItem=null;
    private MenuItem autoFocusItem=null;
    private MenuItem takePictureItem=null;*/
    private boolean singleShotProcessing=false;

    public static RandoCameraFragment newInstance(boolean useFFC) {
        RandoCameraFragment f=new RandoCameraFragment();
        Bundle args=new Bundle();

        args.putBoolean(KEY_USE_FFC, useFFC);
        f.setArguments(args);

        return(f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content=inflater.inflate(R.layout.camera_capture, container, false);
        CameraView cameraView=(CameraView)content.findViewById(R.id.camera);

        setCameraView(cameraView);

        return(content);
    }


}