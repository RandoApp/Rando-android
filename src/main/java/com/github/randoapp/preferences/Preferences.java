package com.github.randoapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.wonderkiln.camerakit.CameraKit;

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

public class Preferences {
    public static final String AUTH_TOKEN_DEFAULT_VALUE = "";
    public static final String FIREBASE_INSTANCE_ID_DEFAULT_VALUE = "";
    public static final String ACCOUNT_DEFAULT_VALUE = "";

    private static Object monitor = new Object();

    public static String getAuthToken(Context context) {
        synchronized (monitor) {
            return getSharedPreferences(context).getString(AUTH_TOKEN, AUTH_TOKEN_DEFAULT_VALUE);
        }
    }

    public static void setAuthToken(Context context, String token) {
        if (token != null) {
            synchronized (monitor) {
                getSharedPreferences(context).edit().putString(AUTH_TOKEN, token).apply();
            }
        }
    }

    public static void removeAuthToken(Context context) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().remove(AUTH_TOKEN).apply();
        }
    }


    public static String getAccount(Context context) {
        synchronized (monitor) {
            return getSharedPreferences(context).getString(ACCOUNT, ACCOUNT_DEFAULT_VALUE);
        }
    }

    public static void setAccount(Context context, String token) {
        if (token != null) {
            synchronized (monitor) {
                getSharedPreferences(context).edit().putString(ACCOUNT, token).apply();
            }
        }
    }

    public static void removeAccount(Context context) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().remove(ACCOUNT).apply();
        }
    }

    public static Location getLocation(Context context) {
        Location location = new Location(LOCATION);
        synchronized (monitor) {
            double lat = Double.valueOf(getSharedPreferences(context).getString(LATITUDE_PARAM, "0"));
            double lon = Double.valueOf(getSharedPreferences(context).getString(LONGITUDE_PARAM, "0"));
            location.setLatitude(lat);
            location.setLongitude(lon);
        }
        return location;
    }

    public static void setLocation(Context context, Location location) {
        if (location != null) {
            synchronized (monitor) {
                getSharedPreferences(context).edit().putString(LONGITUDE_PARAM, String.valueOf(location.getLongitude())).apply();
                getSharedPreferences(context).edit().putString(LATITUDE_PARAM, String.valueOf(location.getLatitude())).apply();
            }
        }
    }

    public static void removeLocation(Context context) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().remove(LONGITUDE_PARAM).apply();
            getSharedPreferences(context).edit().remove(LATITUDE_PARAM).apply();
        }
    }

    public static boolean isTrainingFragmentShown() {
        //TODO: change to return real value when Training will be Implemented.
        return true;
        //return 1 == getSharedPreferences().getInt(Constants.TRAINING_FRAGMENT_SHOWN, 0);
    }

    public static void setTrainingFragmentShown(Context context, int i) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().putInt(TRAINING_FRAGMENT_SHOWN, i).apply();
        }
    }

    public static void removeTrainingFragmentShown(Context context) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().remove(TRAINING_FRAGMENT_SHOWN).apply();
        }
    }

    public static void setBanResetAt(Context context, long resetAt) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().putLong(BAN_RESET_AT, resetAt).apply();
        }
    }

    public static long getBanResetAt(Context context) {
        synchronized (monitor) {
            return getSharedPreferences(context).getLong(BAN_RESET_AT, 0L);
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        synchronized (monitor) {
            return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        }
    }

    public static String getFirebaseInstanceId(Context context) {
        synchronized (monitor) {
            return getSharedPreferences(context).getString(FIREBASE_INSTANCE_ID, FIREBASE_INSTANCE_ID_DEFAULT_VALUE);
        }
    }

    public static void setFirebaseInstanceId(Context context, String token) {
        if (token != null) {
            synchronized (monitor) {
                getSharedPreferences(context).edit().putString(FIREBASE_INSTANCE_ID, token).apply();
            }
        }
    }

    public static void removeFirebaseInstanceId(Context context) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().remove(FIREBASE_INSTANCE_ID).apply();
        }
    }

    public static int getCameraFacing(Context context) {
        synchronized (monitor) {
            int facing = getSharedPreferences(context).getInt(CAMERA_FACING, CameraKit.Constants.FACING_BACK);
            return facing == CameraKit.Constants.FACING_BACK ? CameraKit.Constants.FACING_BACK : CameraKit.Constants.FACING_FRONT;
        }
    }

    public static void setCameraFacing(Context context, int cameraFacing) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().putInt(CAMERA_FACING, cameraFacing).apply();
        }
    }

    public static boolean getCameraGrid(Context context) {
        synchronized (monitor) {
            return getSharedPreferences(context).getBoolean(CAMERA_GRID, false);
        }
    }

    public static void setCameraGrid(Context context, boolean cameraGrid) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().putBoolean(CAMERA_GRID, cameraGrid).apply();
        }
    }

    public static int getCameraFlashMode(Context context) {
        synchronized (monitor) {
            return getSharedPreferences(context).getInt(CAMERA_FLASH_MODE, CameraKit.Constants.FLASH_OFF);
        }
    }

    public static void setCameraFlashMode(Context context, int cameraFacing) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().putInt(CAMERA_FLASH_MODE, cameraFacing).apply();
        }
    }

    public static void removeCameraFlashMode(Context context) {
        synchronized (monitor) {
            getSharedPreferences(context).edit().remove(CAMERA_FLASH_MODE).apply();
        }
    }

}

