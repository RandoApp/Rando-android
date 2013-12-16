package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.eucsoft.foodex.log.Log;

import java.io.IOException;

public class FoodexSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    boolean previewing = false;
    private Bitmap currentBitmap;
    private SurfaceHolder holder;


   /* public FoodexSurfaceView(Context context) {
        this(context, null);
    }

    public FoodexSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }*/

    public FoodexSurfaceView(Context context, Camera camera) {
        super(context);

        this.camera = camera;

        holder = getHolder();
        holder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.HONEYCOMB)
        {
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        getHolder().setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /*if (currentBitmap != null) {
            Canvas canvas = getHolder().lockCanvas();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(currentBitmap, canvas.getWidth(), canvas.getWidth(), true);
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
            getHolder().unlockCanvasAndPost(canvas);
        } else {
            if (camera == null) {
                camera = Camera.open();
            }
            camera.setDisplayOrientation(90);
        }*/

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(FoodexSurfaceView.class, "Error setting camera preview: ",e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (Exception e){
            Log.d(FoodexSurfaceView.class, "Error starting camera preview: ", e.getMessage());
        }


       /* if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(getHolder());
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
    }
/*
    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        previewing = false;
    }*/

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void takePicture() {
        camera.takePicture(null, null, myPictureCallback_JPG);
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            setCurrentBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    };

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            setCurrentBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    };
}
