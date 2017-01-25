package com.github.randoapp.task;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.media.ExifInterface;
import android.support.v4.content.LocalBroadcastManager;

import com.github.randoapp.log.Log;
import com.github.randoapp.util.FileUtil;
import com.jni.bitmap_operations.JniBitmapHolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class CropToSquareImageTask implements Runnable {
    private byte[] data;
    private Context context;

    public CropToSquareImageTask(byte[] data, Context context) {
        this.data = data;
        this.context = context;
    }

    private File saveSquareImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int rotateDegree = calculateImageRotation(data);

        data = null;
        JniBitmapHolder bitmapHolder = new JniBitmapHolder();
        bitmapHolder.storeBitmap(bitmap);
        bitmap.recycle();

        int size = Math.min(options.outWidth, options.outHeight);

        //We need to crop square image from the center of the image
        int indent = (Math.max(options.outWidth, options.outHeight) - size) / 2;
        if (options.outHeight >= options.outWidth) {
            bitmapHolder.cropBitmap(0, indent, size, size + indent);
        } else {
            bitmapHolder.cropBitmap(indent, 0, size + indent, size);
        }

        switch (rotateDegree) {
            case 90:
                bitmapHolder.rotateBitmapCw90();
                break;
            case 180:
                bitmapHolder.rotateBitmap180();
                break;
            case 270:
                bitmapHolder.rotateBitmapCcw90();
                break;
        }
        File file = saveBitmap(bitmapHolder.getBitmapAndFree());
        bitmap.recycle();
        bitmap = null;
        return file;
    }

    private int calculateImageRotation(byte[] data) {
        int rotation = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(new ByteArrayInputStream(data));
        } catch (IOException e) {
            Log.e(CropToSquareImageTask.class, "Exception parsing JPEG", e);
            // TODO: ripple to client
        }
        if (exifInterface != null) {
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
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
        if (image != null) {
            intent.putExtra(RANDO_PHOTO_PATH, image.getAbsolutePath());
        } else {
            intent.putExtra(RANDO_PHOTO_PATH, "");
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}