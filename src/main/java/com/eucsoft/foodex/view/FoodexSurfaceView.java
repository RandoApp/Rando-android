package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class FoodexSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    boolean previewing = false;
    private Bitmap currentBitmap;


    public FoodexSurfaceView(Context context) {
        this(context, null);
    }

    public FoodexSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoodexSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        getHolder().setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (currentBitmap != null) {
            Canvas canvas = getHolder().lockCanvas();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(currentBitmap, canvas.getWidth(), canvas.getWidth(), true);
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
            getHolder().unlockCanvasAndPost(canvas);
        } else {
            if (camera == null) {
                camera = Camera.open();
            }
            camera.setDisplayOrientation(90);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
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
        }
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        previewing = false;
    }

    public Camera getCamera() {
        return camera;
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
