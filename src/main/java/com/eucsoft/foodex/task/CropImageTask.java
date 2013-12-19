package com.eucsoft.foodex.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.service.SyncService;
import com.eucsoft.foodex.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CropImageTask extends AsyncTask<byte[], Integer, Long> implements BaseTask {

    private Map<String, Object> data = new HashMap<String, Object>();
    private TaskResultListener taskResultListener;
    public static final int TASK_ID = 500;

    public CropImageTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    public Long doInBackground(byte[]... files) {
        Log.i(CropImageTask.class, "doInBackground");
        if(files==null || files.length == 0 ){
            return RESULT_ERROR;
        }

        byte[] bytes = files[0];
        files = null;

        try {
            Log.i(CropImageTask.class, "decoding");
            Log.i(CropImageTask.class, "1:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            Log.i(CropImageTask.class, "1.1:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
            String tmpFile = FileUtil.writeImageToTempFile(bytes);
            bytes = null;
            System.gc();

            Log.i(CropImageTask.class, "2:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
            int size = Math.min(options.outWidth, options.outHeight);

            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(tmpFile, false);
            Log.i(CropImageTask.class, "3:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
            Log.i(CropImageTask.class, "Size = ", Integer.toString(size));

            Bitmap bitmap = decoder.decodeRegion(new Rect(0, 0, size, size), null);

            decoder = null;
            Log.i(CropImageTask.class, "4:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, size, size, matrix, true);
            Log.i(CropImageTask.class, "5:" + Runtime.getRuntime().freeMemory());

            File file = FileUtil.getOutputMediaFile();
            if (file == null) {
                return RESULT_ERROR;
            }

            FileOutputStream out = new FileOutputStream(file);
            Log.i(CropImageTask.class, "6:" + Runtime.getRuntime().freeMemory());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            Log.i(CropImageTask.class, "7:" + Runtime.getRuntime().freeMemory());
            /*data = new HashMap<String, Object>();*/

            FileUtil.scanImage(App.context, file.getAbsolutePath());
            /*data.put(Constants.FILEPATH, file.getAbsolutePath());*/

          /*  try {
                API.uploadFood(file, TakePictureActivity.currentLocation);
            } catch (Exception e) {
                Log.w(CreateFoodAndUploadTask.class, "File failed to upload. File=", file.getAbsolutePath());
                return RESULT_ERROR;
            }*/

            SyncService.run();
        } catch (IOException ex) {
            Log.e(CreateFoodAndUploadTask.class, "doInBackground", ex.getMessage());
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
