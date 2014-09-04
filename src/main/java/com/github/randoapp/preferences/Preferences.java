package com.github.randoapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.github.randoapp.App;

import static com.github.randoapp.Constants.ACCOUNT;
import static com.github.randoapp.Constants.AUTH_TOKEN;
import static com.github.randoapp.Constants.LATITUDE_PARAM;
import static com.github.randoapp.Constants.LOCATION;
import static com.github.randoapp.Constants.LONGITUDE_PARAM;
import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static com.github.randoapp.Constants.RANDOS_BALANCE;
import static com.github.randoapp.Constants.TRAINING_FRAGMENT_SHOWN;

public class Preferences {
    public static final String AUTH_TOKEN_DEFAULT_VALUE = "";
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
        //Context.MODE_MULTI_PROCESS needs for access from SyncService
        return App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }


    //Randos balance
    public static int getRandosBalance() {
        synchronized (monitor) {
            return getSharedPreferences().getInt(RANDOS_BALANCE, RANDOS_BALANCE_DEFAULT_VALUE);
        }
    }

    public static void incrementRandosBalance() {
        synchronized (monitor) {
            int randosBalance = getRandosBalance();
            getSharedPreferences().edit().putInt(RANDOS_BALANCE, ++randosBalance).commit();
        }
    }

    public static void decrementRandosBalance() {
        synchronized (monitor) {
            int randosBalance = getRandosBalance();
            getSharedPreferences().edit().putInt(RANDOS_BALANCE, --randosBalance).commit();
        }
    }

    public static void zeroRandosBalance() {
        synchronized (monitor) {
            getSharedPreferences().edit().putInt(RANDOS_BALANCE, 0).commit();
        }
    }

}

