package com.eucsoft.foodex.task;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.eucsoft.foodex.log.Log;

public class CropImageTask extends AsyncTask<String, Integer, Long> implements BaseTask {

    @Override
    protected Long doInBackground(String... files) {

        if(files==null || files.length == 0 ){
            return RESULT_ERROR;
        }

        String filePath = files[0];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        Log.i(CropImageTask.class, "img Height=",Integer.toString(imageHeight), " img width=", Integer.toString(imageWidth));

        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
    }
}
