package com.eucsoft.foodex.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import static com.eucsoft.foodex.Constants.*;

import com.eucsoft.foodex.MainActivity;

public class Preferences {
    private static final SharedPreferences sharedPref;

    public static final String SEESSION_COOKIE_DEFAULT_VALUE = "";

    public static final String SESSION_COOKIE_DEFAULT_VALUE = "";
    public static final String SESSION_COOKIE_PATH_DEFAULT_VALUE = "/";

    public static String getSessionCookieValue() {
        return sharedPref.getString(SEESSION_COOKIE_VALUE, SESSION_COOKIE_DEFAULT_VALUE);
    }

    public static String getSessionCookie() {
        return sharedPref.getString(Constants.SEESSION_COOKIE_NAME, SEESSION_COOKIE_DEFAULT_VALUE);
    }

    public static void setSessionCookie(String sessionCookie) {
        sharedPref.edit().putString(Constants.SEESSION_COOKIE_NAME, sessionCookie).commit();
    }

    public static void removeSessionCookie() {
        sharedPref.edit().remove(Constants.SEESSION_COOKIE_NAME).commit();
    }

    public static boolean isTrainingFragmentShown() {
        //TODO: change to return real value when Training will be Implemented.
        return true;
        //return 1 == sharedPref.getInt(Constants.TRAINING_FRAGMENT_SHOWN, 0);
    }

    public static void setTrainingFragmentShown(int i) {
        sharedPref.edit().putInt(Constants.TRAINING_FRAGMENT_SHOWN, i).commit();
    }

    public static void removeTrainingFragmentShown() {
        sharedPref.edit().remove(Constants.TRAINING_FRAGMENT_SHOWN).commit();
    }
}
