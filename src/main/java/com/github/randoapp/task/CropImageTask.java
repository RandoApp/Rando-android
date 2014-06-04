package com.github.randoapp.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CropImageTask extends BaseTask {

    private String srcFile;

    public CropImageTask(String srcFile) {
        this.srcFile = srcFile;
    }

    @Override
    public Integer run() {
        Log.i(CropImageTask.class, "Start task");
        if (srcFile == null) {
            return ERROR;
        }

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcFile, options);

            File file = FileUtil.getOutputMediaFile();
            if (file == null) {
                return ERROR;
            }

            if (options.outHeight == options.outWidth) {
                FileUtil.copy(new File(srcFile), file);
            } else {

                int size = Math.min(options.outWidth, options.outHeight);
                //We need to crop square image from the center of the image
                int topY = (Math.max(options.outWidth, options.outHeight)-size)/2;
                Log.i(CropImageTask.class, "Orig W:",String.valueOf(options.outWidth), "Orig H:",String.valueOf(options.outHeight), "Size: ", String.valueOf(size),"Top Y: ", String.valueOf(topY));

                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(srcFile, false);
                Bitmap bitmap = decoder.decodeRegion(new Rect(0, topY, size, size+topY), null);

                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.JPEG_QUALITY, out);
                out.close();
            }
            data = new HashMap<String, Object>();

            FileUtil.scanImage(App.context, file.getAbsolutePath());
            data.put(Constants.FILEPATH, file.getAbsolutePath());
        } catch (IOException ex) {
            Log.e(CropImageTask.class, "CropImageTask catch exception: ", ex.getMessage());
            return ERROR;
        }

        return OK;
    }

}
