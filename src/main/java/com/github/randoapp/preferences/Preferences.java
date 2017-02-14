package com.github.randoapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.github.randoapp.App;
import com.google.android.cameraview.CameraView;

import java.util.Date;

import static com.github.randoapp.Constants.ACCOUNT;
import static com.github.randoapp.Constants.AUTH_TOKEN;
import static com.github.randoapp.Constants.CAMERA_FACING;
import static com.github.randoapp.Constants.CAMERA_FLASH_MODE;
import static com.github.randoapp.Constants.CAMERA_GRID;
import static com.github.randoapp.Constants.FIREBASE_INSTANCE_ID;
import static com.github.randoapp.Constants.LATITUDE_PARAM;
import static com.github.randoapp.Constants.LOCATION;
import static com.github.randoapp.Constants.LONGITUDE_PARAM;
import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static com.github.randoapp.Constants.RANDOS_BALANCE;
import static com.github.randoapp.Constants.TRAINING_FRAGMENT_SHOWN;
import static com.github.randoapp.Constants.UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE;

public class Preferences {
    public static final String AUTH_TOKEN_DEFAULT_VALUE = "";
    public static final String FIREBASE_INSTANCE_ID_DEFAULT_VALUE = "";
    public static final String ACCOUNT_DEFAULT_VALUE = "";
    public static final int RANDOS_BALANCE_DEFAULT_VALUE = 0;

    private static Object monitor = new Object();

    public static String getAuthToken() {
        return getSharedPreferences().getString(AUTH_TOKEN, AUTH_TOKEN_DEFAULT_VALUE);
    }

    public static void setAuthToken(String token) {
        if (token != null) {
            getSharedPreferences().edit().putString(AUTH_TOKEN, token).commit();
        }
    }

    public static void removeAuthToken() {
        getSharedPreferences().edit().remove(AUTH_TOKEN).commit();
    }


    public static String getAccount() {
        return getSharedPreferences().getString(ACCOUNT, ACCOUNT_DEFAULT_VALUE);
    }

    public static void setAccount(String token) {
        if (token != null) {
            getSharedPreferences().edit().putString(ACCOUNT, token).commit();
        }
    }

    public static void removeAccount() {
        getSharedPreferences().edit().remove(ACCOUNT).commit();
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
            getSharedPreferences().edit().putString(LONGITUDE_PARAM, String.valueOf(location.getLongitude())).commit();
            getSharedPreferences().edit().putString(LATITUDE_PARAM, String.valueOf(location.getLatitude())).commit();
        }
    }

    public static void removeLocation() {
        getSharedPreferences().edit().remove(LATITUDE_PARAM).commit();
        getSharedPreferences().edit().remove(LONGITUDE_PARAM).commit();
    }

    public static boolean isTrainingFragmentShown() {
        //TODO: change to return real value when Training will be Implemented.
        return true;
        //return 1 == getSharedPreferences().getInt(Constants.TRAINING_FRAGMENT_SHOWN, 0);
    }

    public static void setTrainingFragmentShown(int i) {
        getSharedPreferences().edit().putInt(TRAINING_FRAGMENT_SHOWN, i).commit();
    }

    public static void removeTrainingFragmentShown() {
        getSharedPreferences().edit().remove(TRAINING_FRAGMENT_SHOWN).commit();
    }

    private static SharedPreferences getSharedPreferences() {
        return App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }

    public static String getFirebaseInstanceId() {
        return getSharedPreferences().getString(FIREBASE_INSTANCE_ID, FIREBASE_INSTANCE_ID_DEFAULT_VALUE);
    }

    public static void setFirebaseInstanceId(String token) {
        if (token != null) {
            getSharedPreferences().edit().putString(FIREBASE_INSTANCE_ID, token).commit();
        }
    }

    //UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE
    public static void removeUpdatePlayServicesDateShown() {
        getSharedPreferences().edit().remove(UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE).commit();
    }

    public static Date getUpdatePlayServicesDateShown() {
        return new Date(getSharedPreferences().getLong(UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE, 0L));
    }

    public static void setUpdatePlayServicesDateShown(Date time) {
        if (time != null) {
            getSharedPreferences().edit().putLong(UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE, time.getTime()).commit();
        }
    }

    public static void removeFirebaseInstanceId() {
        getSharedPreferences().edit().remove(FIREBASE_INSTANCE_ID).commit();
    }

    //Selected Camera Facing
    public static int getCameraFacing() {
        synchronized (monitor) {
            int facing = getSharedPreferences().getInt(CAMERA_FACING, CameraView.FACING_BACK);
            return facing == CameraView.FACING_BACK ? CameraView.FACING_BACK : CameraView.FACING_FRONT;
        }
    }

    public static void setCameraFacing(int cameraFacing) {
        synchronized (monitor) {
            getSharedPreferences().edit().putInt(CAMERA_FACING, cameraFacing).commit();
        }
    }

    public static boolean getCameraGrid() {
        synchronized (monitor) {
            return getSharedPreferences().getBoolean(CAMERA_GRID, false);
        }
    }

    public static void setCameraGrid( boolean cameraGrid) {
        synchronized (monitor) {
            getSharedPreferences().edit().putBoolean(CAMERA_GRID, cameraGrid).commit();
        }
    }

    //Camera Flash Mode
    public static int getCameraFlashMode() {
        synchronized (monitor) {
            return getSharedPreferences().getInt(CAMERA_FLASH_MODE, CameraView.FLASH_OFF);
        }
    }

    public static void setCameraFlashMode(int cameraFacing) {
        synchronized (monitor) {
            getSharedPreferences().edit().putInt(CAMERA_FLASH_MODE, cameraFacing).commit();
        }
    }

    public static void removeCameraFlashMode() {
        synchronized (monitor) {
            getSharedPreferences().edit().remove(CAMERA_FLASH_MODE).commit();
        }
    }

}

