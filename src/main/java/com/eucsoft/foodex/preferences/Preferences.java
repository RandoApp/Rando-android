package com.eucsoft.foodex.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.eucsoft.foodex.App;

import static com.eucsoft.foodex.Constants.AUTH_TOKEN;
import static com.eucsoft.foodex.Constants.PREFERENCES_FILE_NAME;
import static com.eucsoft.foodex.Constants.TRAINING_FRAGMENT_SHOWN;

public class Preferences {

    public static final String AUTH_TOKEN_DEFAULT_VALUE = "";

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
        return App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }
}
