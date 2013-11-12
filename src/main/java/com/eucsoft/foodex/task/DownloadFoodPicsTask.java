package com.eucsoft.foodex.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.util.FileUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFoodPicsTask extends AsyncTask<FoodPair, Integer, Long> implements BaseTask {

    public static final int TASK_ID = 200;

    private TaskResultListener taskResultListener;
    private Context context;

    public DownloadFoodPicsTask(TaskResultListener taskResultListener, Context context) {
        this.taskResultListener = taskResultListener;
        this.context = context;
    }

    @Override
    protected Long doInBackground(FoodPair... params) {
        Log.d(CreateFoodAndUploadTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }
        FoodPair foodPair = params[0];

        if (FileUtil.isFoodExists(foodPair.stranger)
                && FileUtil.isMapExists(foodPair.stranger)
                && FileUtil.isFoodExists(foodPair.user)
                && FileUtil.isMapExists(foodPair.user)) {
            return RESULT_OK;
        }
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                DownloadFoodPicsTask.class.getName());
        wl.acquire();

        boolean result;
        try {
            result = downloadFile(foodPair.stranger.foodURL, FileUtil.getFoodPath(foodPair.stranger));
            FileUtil.scanImage(context, FileUtil.getFoodPath(foodPair.stranger));
            result = result && downloadFile(foodPair.stranger.mapURL, FileUtil.getMapPath(foodPair.stranger));
            FileUtil.scanImage(context, FileUtil.getMapPath(foodPair.stranger));
            result = result && downloadFile(foodPair.user.foodURL, FileUtil.getFoodPath(foodPair.user));
            FileUtil.scanImage(context, FileUtil.getFoodPath(foodPair.user));
            result = result && downloadFile(foodPair.user.mapURL, FileUtil.getMapPath(foodPair.user));
            FileUtil.scanImage(context, FileUtil.getMapPath(foodPair.user));
        } finally {
            wl.release();
        }
        if (result) {
            return RESULT_OK;
        } else {
            return RESULT_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(DownloadFoodPicsTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(TASK_ID, aLong, null);
    }

    private boolean downloadFile(String url, String filename) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL URL = new URL(url);
            connection = (HttpURLConnection) URL.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return false;
            // instead of the file

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(filename);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled())
                    return false;
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return true;
    }
}