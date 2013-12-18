package com.eucsoft.foodex.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
                + Constants.IMAGE_PREFIX + timeStamp + Constants.IMAGE_POSTFIX);
        return mediaFile;
    }

    public static String getFoodPath(FoodPair.User user) {
        return FileUtil.getOutputMediaDir() + File.separator + user.getFoodFileName();
    }

    public static String getMapPath(FoodPair.User user) {
        return FileUtil.getOutputMediaDir() + File.separator + user.getMapFileName();
    }

    public static boolean isFoodExists(FoodPair.User user) {
        File file = new File(getFoodPath(user));
        return file.exists();
    }

    public static boolean isMapExists(FoodPair.User user) {
        File file = new File(getMapPath(user));
        return file.exists();
    }

    public static boolean areFilesExist(FoodPair foodPair) {
        return isFoodExists(foodPair.user)
                && isMapExists(foodPair.user)
                && isFoodExists(foodPair.stranger)
                && isMapExists(foodPair.stranger);
    }

    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void scanImage(Context context, String imagePath) {
        MediaScannerConnection.scanFile(context,
                new String[]{imagePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    public static String getFilePathByUrl(String url) {
        String fileName = url == null ? null : url.substring(url.lastIndexOf('/') + 1);
        return FileUtil.getOutputMediaDir() + File.separator + fileName;
    }

    public static String writeImageToTempFile(byte[] data){
        File pictureFile = null;
        try {
            File outputDir = App.context.getCacheDir(); // context being the Activity pointer
            pictureFile = File.createTempFile("camera_image", ".jpg", outputDir);
        } catch (IOException e){
            Log.d(FileUtil.class, "Error creating media file, check storage permissions: ",
                    e.getMessage());
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(FileUtil.class, "File not found: ",e.getMessage());
            return null;
        } catch (IOException e) {
            Log.d(FileUtil.class, "Error accessing file: " + e.getMessage());
            return null;
        }
        return pictureFile.getAbsolutePath();
    }
}
