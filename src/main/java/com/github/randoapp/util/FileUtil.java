package com.github.randoapp.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static String getImagePath(Rando rando) {
        return FileUtil.getOutputMediaDir() + File.separator + rando.getRandoFileName();
    }

    public static String getMapPath(Rando rando) {
        return FileUtil.getOutputMediaDir() + File.separator + rando.getMapFileName();
    }

    public static boolean isImageExists(Rando rando) {
        File file = new File(getImagePath(rando));
        return file.exists();
    }

    public static boolean isMapExists(Rando rando) {
        File file = new File(getMapPath(rando));
        return file.exists();
    }

    public static boolean areFilesExist(Rando rando) {
        return isImageExists(rando)
                && isMapExists(rando);
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

    public static void writeImageFile(byte[] data, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(FileUtil.class, "File not found: ", e.getMessage());
        } catch (IOException e) {
            Log.d(FileUtil.class, "Error accessing file: " + e.getMessage());
        }
    }

    public static String writeImageToTempFile(byte[] data) {
        File pictureFile = null;
        try {
            File outputDir = App.context.getCacheDir(); // context being the Activity pointer
            pictureFile = File.createTempFile("camera_image", ".jpg", outputDir);
        } catch (IOException e) {
            Log.d(FileUtil.class, "Error creating media file, check storage permissions: ",
                    e.getMessage());
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(FileUtil.class, "File not found: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.d(FileUtil.class, "Error accessing file: " + e.getMessage());
            return null;
        }
        return pictureFile.getAbsolutePath();
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
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
