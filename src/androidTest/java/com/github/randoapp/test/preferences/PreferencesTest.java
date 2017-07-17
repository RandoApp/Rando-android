package com.github.randoapp.test.preferences;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.App;
import com.github.randoapp.preferences.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.UUID;

import static com.github.randoapp.Constants.LOCATION;
import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static com.github.randoapp.preferences.Preferences.ACCOUNT_DEFAULT_VALUE;
import static com.github.randoapp.preferences.Preferences.AUTH_TOKEN_DEFAULT_VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class PreferencesTest{

    @Before
    public void setUp() throws Exception {
        App.context = InstrumentationRegistry.getTargetContext();
        App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    //Auth Token Tests
    @Test
    public void testGetAuthToken() {
        String value = UUID.randomUUID().toString();

        Preferences.setAuthToken(value);

        assertThat(Preferences.getAuthToken(), is(value));
    }

    @Test
    public void testSetEmptyAuthTokenReturnEmptyTokenOnGet() {
        String value = "";

        Preferences.setAuthToken(value);

        assertThat(Preferences.getAuthToken(), is(value));
    }

    @Test
    public void testGetAuthTokenAsNull() {
        Preferences.setAuthToken(null);

        assertThat(Preferences.getAuthToken(), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    @Test
    public void testRemoveAuthToken() {
        String value = UUID.randomUUID().toString();

        Preferences.setAuthToken(value);
        assertThat(Preferences.getAuthToken(), is(value));

        Preferences.removeAuthToken();

        assertThat(Preferences.getAuthToken(), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    // Location tests
    @Test
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

    @Test
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

    @Test
    public void testGetLocationSetAsNull() {
        Preferences.setLocation(null);

        double lat = 0.0;
        double lon = 0.0;

        assertThat(Preferences.getLocation(), notNullValue());
        assertThat(Preferences.getLocation().getLatitude(), is(lat));
        assertThat(Preferences.getLocation().getLongitude(), is(lon));
    }

    @Test
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
    @Test
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
    @Test
    public void testGetAccount() {
        String value = UUID.randomUUID().toString();

        Preferences.setAccount(value);

        assertThat(Preferences.getAccount(), is(value));
    }

    @Test
    public void testSetEmptyAccountReturnEmptyTokenOnGet() {
        String value = "";

        Preferences.setAccount(value);

        assertThat(Preferences.getAccount(), is(value));
    }

    @Test
    public void testGetAccountAsNull() {
        Preferences.setAccount(null);

        assertThat(Preferences.getAccount(), is(ACCOUNT_DEFAULT_VALUE));
    }

    @Test
    public void testRemoveAccount() {
        String value = UUID.randomUUID().toString();

        Preferences.setAccount(value);
        assertThat(Preferences.getAccount(), is(value));

        Preferences.removeAccount();

        assertThat(Preferences.getAccount(), is(ACCOUNT_DEFAULT_VALUE));
    }

    //FirebaseInstanceId Tests
    @Test
    public void shouldSetAndGetFirebaseInstanceId() {
        String value = UUID.randomUUID().toString();

        Preferences.setFirebaseInstanceId(value);

        assertThat(Preferences.getFirebaseInstanceId(), is(value));
    }

    @Test
    public void shouldSetEmptyFirebaseInstanceIdAndReturnEmptyFirebaseInstanceIdOnGet() {
        String value = "";

        Preferences.setFirebaseInstanceId(value);

        assertThat(Preferences.getFirebaseInstanceId(), is(value));
    }

    @Test
    public void shouldGetDefaultFirebaseInstanceIdWhenSetAsNull() {
        Preferences.setFirebaseInstanceId(null);

        assertThat(Preferences.getFirebaseInstanceId(), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    @Test
    public void shouldRemoveFirebaseInstanceId() {
        String value = UUID.randomUUID().toString();

        Preferences.setFirebaseInstanceId(value);
        assertThat(Preferences.getFirebaseInstanceId(), is(value));

        Preferences.removeFirebaseInstanceId();

        assertThat(Preferences.getFirebaseInstanceId(), is(AUTH_TOKEN_DEFAULT_VALUE));
    }
}