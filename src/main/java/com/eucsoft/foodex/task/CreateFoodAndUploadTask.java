package com.eucsoft.foodex.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.callback.TaskCallback;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CreateFoodAndUploadTask extends AsyncTask<Bitmap, Integer, Long> implements BaseTask {

    public static final int TASK_ID = 100;

    private TaskCallback taskCallback;
    private HashMap<String, Object> data;
    private Context context;

    public CreateFoodAndUploadTask(TaskCallback taskCallback, Context context) {
        this.taskCallback = taskCallback;
        this.context = context;
    }

    @Override
    protected Long doInBackground(Bitmap... params) {
        Log.d(CreateFoodAndUploadTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }
        Bitmap originalBmp = params[0];

        int size = Math.min(originalBmp.getWidth(), originalBmp.getHeight());
        Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, 0, 0, size, size);

        File file = getOutputMediaFile();
        if (file == null) {
            return RESULT_ERROR;
        }
        String imagePath = file.getAbsolutePath();
        try {
            FileOutputStream out = new FileOutputStream(file);
            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException ex) {
            Log.e(CreateFoodAndUploadTask.class, "doInBackground", ex.getMessage());
        }

        FoodPair foodPair = null;
        try {
            //TODO: set correct location
            foodPair = API.uploadFood(file, null);
        } catch (Exception e) {
            Log.w(CreateFoodAndUploadTask.class, "File failed to upload. File=", file.getAbsolutePath());
            return RESULT_ERROR;
        }

        FoodDAO foodDAO = new FoodDAO(context);
        foodDAO.createFood(foodPair);
        foodDAO.close();

        file.renameTo(new File(getOutputMediaDir().getAbsolutePath() + foodPair.user.getFoodFileName()));

        //scan the image so show up in album
        MediaScannerConnection.scanFile(context,
                new String[]{imagePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });

        return RESULT_OK;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(CreateFoodAndUploadTask.class, "onPostExecute", aLong.toString());
        taskCallback.onTaskResult(TASK_ID, aLong, data);
    }

    private static File getOutputMediaDir() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.ALBUM_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    private static File getOutputMediaFile() {
        Log.d(CreateFoodAndUploadTask.class, "getOutputMediaFile");

        File mediaStorageDir = getOutputMediaDir();
        if (mediaStorageDir == null) {
            return null;
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
}
