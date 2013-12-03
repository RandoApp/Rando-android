package com.eucsoft.foodex.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;

import java.util.Date;

public class Preferences {

    private static final SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

    private static final String SESSION_COOKIE_DEFAULT_VALUE = "";
    private static final Long SESSION_COOKIE_EXPIRE_DEFAULT_VALUE = 0L;


    public static String getSessionCookieValue() {
        return sharedPref.getString(Constants.SEESSION_COOKIE_VALUE, SESSION_COOKIE_DEFAULT_VALUE);
    }

    public static String getSessionCookieDomain() {
        return sharedPref.getString(Constants.SEESSION_COOKIE_DOMAIN, SESSION_COOKIE_DEFAULT_VALUE);
    }

    public static String getSessionCookiePath() {
        return sharedPref.getString(Constants.SEESSION_COOKIE_PATH, SESSION_COOKIE_DEFAULT_VALUE);
    }

    public static Date getSessionCookieExpire() {
        return new Date(sharedPref.getLong(Constants.SEESSION_COOKIE_EXPIRE, SESSION_COOKIE_EXPIRE_DEFAULT_VALUE));
    }

    public static void setSessionCookie(String value, String domain, String path, Date expiry) {
        sharedPref.edit()
            .putString(Constants.SEESSION_COOKIE_VALUE, value)
            .putString(Constants.SEESSION_COOKIE_DOMAIN, domain)
            .putString(Constants.SEESSION_COOKIE_PATH, path)
            .putLong(Constants.SEESSION_COOKIE_EXPIRE, expiry.getTime())
            .commit();
    }

    public static void removeSessionCookie() {
        sharedPref.edit()
            .remove(Constants.SEESSION_COOKIE_VALUE)
            .remove(Constants.SEESSION_COOKIE_DOMAIN)
            .remove(Constants.SEESSION_COOKIE_PATH)
            .remove(Constants.SEESSION_COOKIE_EXPIRE)
            .commit();
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
