package com.github.randoapp.test.preferences;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;

import com.github.randoapp.App;
import com.github.randoapp.preferences.Preferences;

import java.util.Random;
import java.util.UUID;

import static com.github.randoapp.Constants.LOCATION;
import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static com.github.randoapp.preferences.Preferences.ACCOUNT_DEFAULT_VALUE;
import static com.github.randoapp.preferences.Preferences.AUTH_TOKEN_DEFAULT_VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PreferencesTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        App.context = this.getContext();
        App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    //Auth Token Tests
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

    // Location tests
    public void testGetLocation() {
        Random random = new Random();
        double lat = random.nextDouble();
        double lon = random.nextDouble();

        Location location = new Location(LOCATION);
        location.setLongitude(lon);
        location.setLatitude(lat);

        Preferences.setLocation(location);
        assertThat(Preferences.getLocation(), notNullValue());
        assertThat(Preferences.getLocation().getLatitude(), is(lat));
        assertThat(Preferences.getLocation().getLongitude(), is(lon));
    }

    public void testSetZeroLocationReturnDefaultLocationOnGet() {
        double lat = 0.0;
        double lon = 0.0;

        Location location = new Location(LOCATION);
        location.setLongitude(lon);
        location.setLatitude(lat);

        Preferences.setLocation(location);

        assertThat(Preferences.getLocation(), notNullValue());
        assertThat(Preferences.getLocation().getLatitude(), is(lat));
        assertThat(Preferences.getLocation().getLongitude(), is(lon));
    }

    public void testGetLocationSetAsNull() {
        Preferences.setLocation(null);

        double lat = 0.0;
        double lon = 0.0;

        assertThat(Preferences.getLocation(), notNullValue());
        assertThat(Preferences.getLocation().getLatitude(), is(lat));
        assertThat(Preferences.getLocation().getLongitude(), is(lon));
    }

    public void testRemoveLocation() {
        Random random = new Random();
        double lat = random.nextDouble();
        double lon = random.nextDouble();
        Location location = new Location(LOCATION);
        location.setLongitude(lon);
        location.setLatitude(lat);

        Preferences.setLocation(location);
        Preferences.removeLocation();

        lat = 0.0;
        lon = 0.0;

        assertThat(Preferences.getLocation(), notNullValue());
        assertThat(Preferences.getLocation().getLatitude(), is(lat));
        assertThat(Preferences.getLocation().getLongitude(), is(lon));
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

    //Account Tests
    public void testGetAccount() {
        String value = UUID.randomUUID().toString();

        Preferences.setAccount(value);

        assertThat(Preferences.getAccount(), is(value));
    }

    public void testSetEmptyAccountReturnEmptyTokenOnGet() {
        String value = "";

        Preferences.setAccount(value);

        assertThat(Preferences.getAccount(), is(value));
    }

    public void testGetAccountAsNull() {
        Preferences.setAccount(null);

        assertThat(Preferences.getAccount(), is(ACCOUNT_DEFAULT_VALUE));
    }

    public void testRemoveAccount() {
        String value = UUID.randomUUID().toString();

        Preferences.setAccount(value);
        assertThat(Preferences.getAccount(), is(value));

        Preferences.removeAccount();

        assertThat(Preferences.getAccount(), is(ACCOUNT_DEFAULT_VALUE));
    }

    //Randos balance
    public void testIncrement() {
        int val = Preferences.getRandosBalance();
        Preferences.incrementRandosBalance();
        assertThat("Increment doesn't work",Preferences.getRandosBalance(), is(val+1));
    }

    public void testDecrement() {
        int val = Preferences.getRandosBalance();
        Preferences.decrementRandosBalance();
        assertThat("Increment doesn't work",Preferences.getRandosBalance(), is(val-1));
    }

    public void testSetZero() {
        Preferences.zeroRandosBalance();
        assertThat(Preferences.getRandosBalance(), is(0));

        Preferences.incrementRandosBalance();
        Preferences.incrementRandosBalance();
        Preferences.zeroRandosBalance();
        assertThat(Preferences.getRandosBalance(), is(0));

        Preferences.incrementRandosBalance();
        Preferences.incrementRandosBalance();
        Preferences.zeroRandosBalance();
        assertThat(Preferences.getRandosBalance(), is(0));
    }

}