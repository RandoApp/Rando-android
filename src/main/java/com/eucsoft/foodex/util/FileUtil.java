package com.eucsoft.foodex.util;

import android.os.Environment;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    public static File getOutputMediaDir() {
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

    public static File getOutputMediaFile() {
        Log.d(FileUtil.class, "getOutputMediaFile");

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

    public static String getFoodPath(FoodPair.User user) {
        return FileUtil.getOutputMediaDir() + File.separator + user.getFoodFileName();
    }

    public static boolean isFoodExists(FoodPair.User user) {
        File file = new File(getFoodPath(user));
        return file.exists();
    }
}
