package com.eucsoft.foodex.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import static com.eucsoft.foodex.Constants.*;

import com.eucsoft.foodex.MainActivity;

public class Preferences {

    private static final SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

    public static final String SESSION_COOKIE_DEFAULT_VALUE = "";
    public static final String SESSION_COOKIE_PATH_DEFAULT_VALUE = "/";

    public static String getSessionCookieValue() {
        return sharedPref.getString(SEESSION_COOKIE_VALUE, SESSION_COOKIE_DEFAULT_VALUE);
    }

    public static String getSessionCookieDomain() {
        return sharedPref.getString(SEESSION_COOKIE_DOMAIN, SERVER_HOST);
    }

    public static String getSessionCookiePath() {
        return sharedPref.getString(SEESSION_COOKIE_PATH, SESSION_COOKIE_PATH_DEFAULT_VALUE);
    }

    public static void setSessionCookie(String value, String domain, String path) {
        if (value == null) return;
        if (domain == null) domain = SERVER_HOST;
        if (path == null) path = SESSION_COOKIE_PATH_DEFAULT_VALUE;

        sharedPref.edit()
            .putString(SEESSION_COOKIE_VALUE, value)
            .putString(SEESSION_COOKIE_DOMAIN, domain)
            .putString(SEESSION_COOKIE_PATH, path)
            .commit();
    }

    public static void removeSessionCookie() {
        sharedPref.edit()
            .remove(SEESSION_COOKIE_VALUE)
            .remove(SEESSION_COOKIE_DOMAIN)
            .remove(SEESSION_COOKIE_PATH)
            .commit();
    }

    public static boolean isTrainingFragmentShown() {
        //TODO: change to return real value when Training will be Implemented.
        return true;
        //return 1 == sharedPref.getInt(Constants.TRAINING_FRAGMENT_SHOWN, 0);
    }

    public static void setTrainingFragmentShown(int i) {
        sharedPref.edit().putInt(TRAINING_FRAGMENT_SHOWN, i).commit();
    }

    public static void removeTrainingFragmentShown() {
        sharedPref.edit().remove(TRAINING_FRAGMENT_SHOWN).commit();
    }
}
