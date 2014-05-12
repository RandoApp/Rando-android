package com.github.randoapp.view;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.github.randoapp.log.Log;
import com.github.randoapp.util.CameraUtil;

import java.io.IOException;

public class RandoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceHolder holder;

    public RandoSurfaceView(Context context, Camera camera) {
        super(context);

        this.camera = camera;

        holder = getHolder();
        holder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0(HONEYCOMB)
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        getHolder().setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the ic_camera where to draw the preview.
        try {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(com.github.randoapp.view.RandoSurfaceView.class, "Error setting ic_camera preview: ", e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        Camera.Parameters params = camera.getParameters();

        Camera.Size cameraSize;
      /*  if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            cameraSize = CameraUtil.getBestPictureSizeForOldDevices(params.getSupportedPictureSizes());
        } else {
            cameraSize = CameraUtil.getBestPictureSize(params.getSupportedPictureSizes());
        }*/
        //we want small picture size for all devices
        cameraSize = CameraUtil.getBestPictureSizeForOldDevices(params.getSupportedPictureSizes());

        params.setPictureSize(cameraSize.width, cameraSize.height);

        Camera.Size previewSize = CameraUtil.getBestPreviewSize(params.getSupportedPreviewSizes(), width, height);
        params.setPreviewSize(previewSize.width, previewSize.height);

        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            Log.d(com.github.randoapp.view.RandoSurfaceView.class, "Error starting ic_camera preview: ", e.getMessage());
        }
    }

}
