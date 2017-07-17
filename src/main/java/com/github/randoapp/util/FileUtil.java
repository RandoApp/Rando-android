package com.github.randoapp.util;

import android.os.Environment;

import com.github.randoapp.Constants;
import com.github.randoapp.log.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public static void removeFileIfExist(String filename) {
        File file = new File(filename);
        if (file.isFile()) {
            file.delete();
        }
    }

    public static byte[] readFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = null;
        try {
            buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
        } catch (FileNotFoundException e) {
            Log.e(FileUtil.class, "Error Reading File", e);
        } catch (IOException e) {
            Log.e(FileUtil.class, "Error Reading File", e);
        } finally {
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                }
            }
        }
        return bytes;
    }
}
