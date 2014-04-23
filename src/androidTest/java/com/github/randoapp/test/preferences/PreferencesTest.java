package com.github.randoapp.test.preferences;

import android.content.Context;
import android.test.AndroidTestCase;

import com.github.randoapp.App;
import com.github.randoapp.preferences.Preferences;

import java.util.UUID;

import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static com.github.randoapp.preferences.Preferences.AUTH_TOKEN_DEFAULT_VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PreferencesTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        App.context = this.getContext();
        App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public void testGetAuthToken() {
        String value = UUID.randomUUID().toString();

        Preferences.setAuthToken(value);

        assertThat(Preferences.getAuthToken(), is(value));
    }

    public void testSetEmptyAuthTokenReturnEmptyTokenOnGet() {
        String value = "";

        Preferences.setAuthToken(value);

        assertThat(Preferences.getAuthToken(), is(value));
    }

    public void testGetAuthTokenAsNull() {
        Preferences.setAuthToken(null);

        assertThat(Preferences.getAuthToken(), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    public void testRemoveAuthToken() {
        String value = UUID.randomUUID().toString();

        Preferences.setAuthToken(value);
        assertThat(Preferences.getAuthToken(), is(value));

        Preferences.removeAuthToken();

        assertThat(Preferences.getAuthToken(), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    //This test should pass untill we implement Training logic
    public void testTrainingShownIsTrue() {
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.removeTrainingFragmentShown();
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.setTrainingFragmentShown(0);
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.setTrainingFragmentShown(1);
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.removeTrainingFragmentShown();
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
    }

}