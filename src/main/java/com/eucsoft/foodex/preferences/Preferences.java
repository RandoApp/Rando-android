package com.eucsoft.foodex.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.eucsoft.foodex.App;

import static com.eucsoft.foodex.Constants.AUTH_TOKEN;
import static com.eucsoft.foodex.Constants.PREFERENCES_FILE_NAME;
import static com.eucsoft.foodex.Constants.TRAINING_FRAGMENT_SHOWN;

public class Preferences {

    private static final SharedPreferences sharedPref = App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

    public static final String AUTH_TOKEN_DEFAULT_VALUE = "";

    public static String getAuthToken() {
        return sharedPref.getString(AUTH_TOKEN, AUTH_TOKEN_DEFAULT_VALUE);
    }

    public static void setAuthToken(String token) {
        if (token != null) {
            sharedPref.edit().putString(AUTH_TOKEN, token).commit();
        }
    }

    public static void removeAuthToken() {
        sharedPref.edit().remove(AUTH_TOKEN).commit();
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
