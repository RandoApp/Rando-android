package com.github.randoapp.view;

import android.content.Context;
import android.hardware.Camera;

import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.github.randoapp.util.CameraUtil;
import com.github.randoapp.util.FileUtil;

import java.io.File;

public class RandoCameraHost extends SimpleCameraHost {

    public RandoCameraHost(Context _ctxt) {
        super(_ctxt);
    }

    @Override
    protected String getPhotoFilename() {
        return super.getPhotoFilename();
    }

    @Override
    public boolean useSingleShotMode() {
        return super.useSingleShotMode();
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

    private Camera.Size getBestSize(int width, int height, Camera.Parameters parameters){
            Camera.Size result = null;

            for (Camera.Size size : parameters.getSupportedPictureSizes()){
                if (size.width <= width && size.height <= height){
                    if (result == null){
                        result = size;
                    } else {
                        int resultArea = result.width * result.height;
                        int newArea = size.width * size.height;
                        if (newArea > resultArea){
                            result = size;
                        }
                    }
                }
            }
            return (result);
        }
    }
