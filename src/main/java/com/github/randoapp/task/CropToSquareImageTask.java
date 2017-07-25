package com.github.randoapp.task;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.media.ExifInterface;
import android.support.v4.content.LocalBroadcastManager;

import com.github.randoapp.log.Log;
import com.github.randoapp.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class CropToSquareImageTask implements Runnable {
    private WeakReference<byte[]> data;
    private Context context;
    private boolean isFrontCamera;
    private AtomicBoolean isCanceled = new AtomicBoolean(false);

    public CropToSquareImageTask(byte[] data, boolean isFrontCamera, Context context) {
        this.data = new WeakReference<>(data);
        this.context = context;
        this.isFrontCamera = isFrontCamera;
    }

    private File saveSquareImage() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (isCanceled.get()) {
            data.clear();
            data = null;
            return null;
        }
        BitmapFactory.decodeByteArray(data.get(), 0, data.get().length, options);
        int rotateDegree = calculateImageRotation(data.get());
        if (isCanceled.get()) {
            data.clear();
            data = null;
            return null;
        }
        WeakReference<Bitmap> bitmap = decodeSquare(data, options);
        data.clear();
        data = null;
        if (isCanceled.get()) {
            bitmap.get().recycle();
            bitmap.clear();
            return null;
        }

        Bitmap resultedBitmap = rotate(bitmap, rotateDegree);

        if (isCanceled.get()) {
            if (bitmap.get()!= null) {
                bitmap.get().recycle();
            }
            bitmap.clear();
            resultedBitmap.recycle();
            return null;
        }

        File file = saveBitmap(resultedBitmap);
        bitmap.get().recycle();
        bitmap.clear();
        bitmap = null;
        resultedBitmap.recycle();
        resultedBitmap = null;

        return file;
    }

    private WeakReference<Bitmap> decodeSquare(WeakReference<byte[]> data, BitmapFactory.Options options) {
        int size = Math.min(options.outWidth, options.outHeight);
        //We need to crop square image from the center of the image
        int indent = (Math.max(options.outWidth, options.outHeight) - size) / 2;
        Rect rect;
        if (options.outHeight >= options.outWidth) {
            rect = new Rect(0, indent, size, size + indent);
        } else {
            rect = new Rect(indent, 0, size + indent, size);
        }

        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;

        WeakReference<Bitmap> result;
        try {
            BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(data.get(), 0, data.get().length, true);
            result = new WeakReference<>(regionDecoder.decodeRegion(rect, options));
            regionDecoder.recycle();
            regionDecoder = null;
        } catch (IOException ex) {
            Log.e(CropToSquareImageTask.class, "exception creating BitmapRegionDecoder", ex);
            throw new RuntimeException("Error creating BitmapRegionDecoder");
        }

        return result;
    }

    private Bitmap rotate(WeakReference<Bitmap> bitmap, int rotateDegree) {
        if (rotateDegree != 0 || isFrontCamera) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateDegree);
            if (isFrontCamera) {
                matrix.postScale(-1, 1);
            }
            return Bitmap.createBitmap(bitmap.get(), 0, 0, bitmap.get().getWidth(), bitmap.get().getHeight(), matrix, true);
        } else {
            return bitmap.get();
        }
    }

    private int calculateImageRotation(byte[] data) {
        int rotation = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(new ByteArrayInputStream(data));
        } catch (IOException e) {
            Log.e(CropToSquareImageTask.class, "Exception parsing JPEG Exif", e);
            // TODO: ripple to client
        }
        if (exifInterface != null) {
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d(CropToSquareImageTask.class, "Orientation: " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                    rotation = 0;
                    break;
                default:
                    break;
            }
        }
        return rotation;
    }

    private File saveBitmap(Bitmap bitmap) {
        try {
            File file = FileUtil.getOutputMediaFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            Log.d(CropToSquareImageTask.class, "Camera Pic Processed.");
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void run() {
        File image = saveSquareImage();
        Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
        if (image != null && !isCanceled.get()) {
            intent.putExtra(RANDO_PHOTO_PATH, image.getAbsolutePath());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    public void cancel() {
        Log.d(CropToSquareImageTask.class, "Cancelling...");
        isCanceled.set(true);
    }
}