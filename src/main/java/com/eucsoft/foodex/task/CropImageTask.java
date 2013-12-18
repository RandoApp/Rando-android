package com.eucsoft.foodex.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CropImageTask extends AsyncTask<byte[], Integer, Long> implements BaseTask {

    private Map<String, Object> data = new HashMap<String, Object>();
    private TaskResultListener taskResultListener;

    public CropImageTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    protected Long doInBackground(byte[]... files) {
        Log.i(CropImageTask.class, "doInBackground");
        if(files==null || files.length == 0 ){
            return RESULT_ERROR;
        }

        byte[] bytes = files[0];

        try {
            Log.i(CropImageTask.class, "decoding");
           // String tmpFile = FileUtil.writeImageToTempFile(bytes);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);
            int size = Math.min(options.outWidth, options.outHeight);
            Log.i(CropImageTask.class, "decoded"+bytes.length);

            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(bytes,0, bytes.length, false);

            bitmap = decoder.decodeRegion(new Rect(0,0,size,size),null);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, size, size, matrix, true);

            File file = FileUtil.getOutputMediaFile();
            if (file == null) {
                return RESULT_ERROR;
            }

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            data = new HashMap<String, Object>();

            FileUtil.scanImage(App.context, file.getAbsolutePath());
            data.put(Constants.FILEPATH, file.getAbsolutePath());

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
