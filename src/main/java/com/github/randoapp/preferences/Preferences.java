package com.github.randoapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.flurgle.camerakit.CameraKit;
import com.github.randoapp.App;

import java.util.Date;

import static com.github.randoapp.Constants.ACCOUNT;
import static com.github.randoapp.Constants.AUTH_TOKEN;
import static com.github.randoapp.Constants.BAN_RESET_AT;
import static com.github.randoapp.Constants.CAMERA_FACING;
import static com.github.randoapp.Constants.CAMERA_FLASH_MODE;
import static com.github.randoapp.Constants.CAMERA_GRID;
import static com.github.randoapp.Constants.ENABLE_VIBRATE;
import static com.github.randoapp.Constants.FIREBASE_INSTANCE_ID;
import static com.github.randoapp.Constants.LATITUDE_PARAM;
import static com.github.randoapp.Constants.LOCATION;
import static com.github.randoapp.Constants.LONGITUDE_PARAM;
import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static com.github.randoapp.Constants.TRAINING_FRAGMENT_SHOWN;
import static com.github.randoapp.Constants.UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE;

public class Preferences {
    public static final String AUTH_TOKEN_DEFAULT_VALUE = "";
    public static final String FIREBASE_INSTANCE_ID_DEFAULT_VALUE = "";
    public static final String ACCOUNT_DEFAULT_VALUE = "";

    private static Object monitor = new Object();

    public static String getAuthToken() {
        return getSharedPreferences().getString(AUTH_TOKEN, AUTH_TOKEN_DEFAULT_VALUE);
    }

    public static void setAuthToken(String token) {
        if (token != null) {
            getSharedPreferences().edit().putString(AUTH_TOKEN, token).apply();
        }
    }

    public static void removeAuthToken() {
        getSharedPreferences().edit().remove(AUTH_TOKEN).apply();
    }


    public static String getAccount() {
        return getSharedPreferences().getString(ACCOUNT, ACCOUNT_DEFAULT_VALUE);
    }

    public static void setAccount(String token) {
        if (token != null) {
            getSharedPreferences().edit().putString(ACCOUNT, token).apply();
        }
    }

    public static void removeAccount() {
        getSharedPreferences().edit().remove(ACCOUNT).apply();
    }

    public static Location getLocation() {
        double lat = Double.valueOf(getSharedPreferences().getString(LATITUDE_PARAM, "0"));
        double lon = Double.valueOf(getSharedPreferences().getString(LONGITUDE_PARAM, "0"));
        Location location = new Location(LOCATION);
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }

    public static void setLocation(Location location) {
        if (location != null) {
            getSharedPreferences().edit().putString(LONGITUDE_PARAM, String.valueOf(location.getLongitude())).apply();
            getSharedPreferences().edit().putString(LATITUDE_PARAM, String.valueOf(location.getLatitude())).apply();
        }
    }

    public static void removeLocation() {
        getSharedPreferences().edit().remove(LATITUDE_PARAM).apply();
        getSharedPreferences().edit().remove(LONGITUDE_PARAM).apply();
    }

    public static boolean isTrainingFragmentShown() {
        //TODO: change to return real value when Training will be Implemented.
        return true;
        //return 1 == getSharedPreferences().getInt(Constants.TRAINING_FRAGMENT_SHOWN, 0);
    }

    public static void setTrainingFragmentShown(int i) {
        getSharedPreferences().edit().putInt(TRAINING_FRAGMENT_SHOWN, i).apply();
    }

    public static void removeTrainingFragmentShown() {
        getSharedPreferences().edit().remove(TRAINING_FRAGMENT_SHOWN).apply();
    }

    public static void setBanResetAt(long resetAt) {
        getSharedPreferences().edit().putLong(BAN_RESET_AT, resetAt).apply();
    }

    public static long getBanResetAt() {
        return getSharedPreferences().getLong(BAN_RESET_AT, 0L);
    }

    private static SharedPreferences getSharedPreferences() {
        return App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }

    public static String getFirebaseInstanceId() {
        return getSharedPreferences().getString(FIREBASE_INSTANCE_ID, FIREBASE_INSTANCE_ID_DEFAULT_VALUE);
    }

    public static void setFirebaseInstanceId(String token) {
        if (token != null) {
            getSharedPreferences().edit().putString(FIREBASE_INSTANCE_ID, token).apply();
        }
    }

    //UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE
    public static void removeUpdatePlayServicesDateShown() {
        getSharedPreferences().edit().remove(UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE).apply();
    }

    public static Date getUpdatePlayServicesDateShown() {
        return new Date(getSharedPreferences().getLong(UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE, 0L));
    }

    public static void setUpdatePlayServicesDateShown(Date time) {
        if (time != null) {
            getSharedPreferences().edit().putLong(UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE, time.getTime()).apply();
        }
    }

    public static void removeFirebaseInstanceId() {
        getSharedPreferences().edit().remove(FIREBASE_INSTANCE_ID).apply();
    }

    //Selected Camera Facing
    public static int getCameraFacing() {
        synchronized (monitor) {
            int facing = getSharedPreferences().getInt(CAMERA_FACING, CameraKit.Constants.FACING_BACK);
            return facing == CameraKit.Constants.FACING_BACK ? CameraKit.Constants.FACING_BACK : CameraKit.Constants.FACING_FRONT;
        }
    }

    public static void setCameraFacing(int cameraFacing) {
        synchronized (monitor) {
            getSharedPreferences().edit().putInt(CAMERA_FACING, cameraFacing).apply();
        }
    }

    public static boolean getCameraGrid() {
        synchronized (monitor) {
            return getSharedPreferences().getBoolean(CAMERA_GRID, false);
        }
    }

    public static void setCameraGrid(boolean cameraGrid) {
        synchronized (monitor) {
            getSharedPreferences().edit().putBoolean(CAMERA_GRID, cameraGrid).apply();
        }
    }

    //Camera Flash Mode
    public static int getCameraFlashMode() {
        synchronized (monitor) {
            return getSharedPreferences().getInt(CAMERA_FLASH_MODE, CameraKit.Constants.FLASH_OFF);
        }
    }

    public static void setCameraFlashMode(int cameraFacing) {
        synchronized (monitor) {
            getSharedPreferences().edit().putInt(CAMERA_FLASH_MODE, cameraFacing).apply();
        }
    }

    public static void removeCameraFlashMode() {
        synchronized (monitor) {
            getSharedPreferences().edit().remove(CAMERA_FLASH_MODE).apply();
        }
    }

    public static boolean getEnableVibrate() {
        synchronized (monitor) {
            return getSharedPreferences().getBoolean(ENABLE_VIBRATE, true);
        }
    }

    public static void setEnableVibrate(boolean cameraGrid) {
        synchronized (monitor) {
            getSharedPreferences().edit().putBoolean(ENABLE_VIBRATE, cameraGrid).apply();
        }
    }

}

