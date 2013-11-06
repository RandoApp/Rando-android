package com.eucsoft.foodex.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;

import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.callback.TaskCallback;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        File file = FileUtil.getOutputMediaFile();
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
            return RESULT_ERROR;
        }

        FoodPair foodPair = null;
        try {
            foodPair = API.uploadFood(file, TakePictureActivity.currentLocation);
        } catch (Exception e) {
            Log.w(CreateFoodAndUploadTask.class, "File failed to upload. File=", file.getAbsolutePath());
            return RESULT_ERROR;
        }

        FoodDAO foodDAO = new FoodDAO(context);
        foodDAO.createFood(foodPair);
        foodDAO.close();

        file.renameTo(new File(FileUtil.getOutputMediaDir().getAbsolutePath() + foodPair.user.getFoodFileName()));

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
}
