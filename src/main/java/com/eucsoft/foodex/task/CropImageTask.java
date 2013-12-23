package com.eucsoft.foodex.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CropImageTask extends AsyncTask<String, Integer, Long> implements BaseTask {

    private Map<String, Object> data = new HashMap<String, Object>();
    private TaskResultListener taskResultListener;

    public CropImageTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    public Long doInBackground(String... files) {
        Log.i(CropImageTask.class, "doInBackground");
        if (files == null || files.length == 0) {
            return RESULT_ERROR;
        }

        String srcFile = files[0];

        try {
            Log.i(CropImageTask.class, "1:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcFile, options);
            Log.i(CropImageTask.class, "1.1:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));

            Log.i(CropImageTask.class, "2:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));

            File file = FileUtil.getOutputMediaFile();
            if (file == null) {
                return RESULT_ERROR;
            }

            if (options.outHeight == options.outWidth) {
                FileUtil.copy(new File(srcFile), file);
            } else {

                int size = Math.min(options.outWidth, options.outHeight);

                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(srcFile, false);
                Log.i(CropImageTask.class, "3:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));

                Bitmap bitmap = decoder.decodeRegion(new Rect(0, 0, size, size), null);

                Log.i(CropImageTask.class, "4:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));

                FileOutputStream out = new FileOutputStream(file);
                Log.i(CropImageTask.class, "6:" + Runtime.getRuntime().freeMemory());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
            }
            Log.i(CropImageTask.class, "7:" + Runtime.getRuntime().freeMemory());
            data = new HashMap<String, Object>();

            FileUtil.scanImage(App.context, file.getAbsolutePath());
            data.put(Constants.FILEPATH, file.getAbsolutePath());

        } catch (IOException ex) {
            Log.e(CropImageTask.class, "doInBackground", ex.getMessage());
            return RESULT_ERROR;
        }

        return RESULT_OK;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(CropImageTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(0, aLong, data);
    }
}
