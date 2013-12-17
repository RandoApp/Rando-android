package com.eucsoft.foodex.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.util.FileUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class DownloadImageTask extends AsyncTask<String, Integer, Long> implements BaseTask {

    public static final int TASK_ID = 400;

    private TaskResultListener taskResultListener;
    private Context context;
    private HashMap<String, Object> data;


    public DownloadImageTask(TaskResultListener taskResultListener, Context context) {
        this.taskResultListener = taskResultListener;
        this.context = context;
    }

    @Override
    protected Long doInBackground(String... params) {
        Log.d(CreateFoodAndUploadTask.class, "doInBackground");

        if (params == null || params.length == 0) {
            return RESULT_ERROR;
        }
        String url = params[0];

        String filePath = FileUtil.getFilePathByUrl(url);

        if (FileUtil.isFileExists(filePath)) {
            data = new HashMap<String, Object>(1);
            data.put(Constants.FILEPATH, filePath);
            return RESULT_OK;
        }

        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                DownloadImageTask.class.getName());
        wl.acquire();
        boolean result;
        try {
            result = downloadFile(url, filePath);
            FileUtil.scanImage(context, filePath);
        } finally {
            wl.release();
        }
        if (result) {
            data = new HashMap<String, Object>(1);
            data.put(Constants.FILEPATH, filePath);
            return RESULT_OK;
        } else {
            return RESULT_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(DownloadImageTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(TASK_ID, aLong, data);
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