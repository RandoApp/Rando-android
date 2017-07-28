package com.github.randoapp.test.preferences;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

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
public class PreferencesTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    //Auth Token Tests
    @Test
    public void testGetAuthToken() {
        String value = UUID.randomUUID().toString();

        Preferences.setAuthToken(context, value);

        assertThat(Preferences.getAuthToken(context), is(value));
    }

    @Test
    public void testSetEmptyAuthTokenReturnEmptyTokenOnGet() {
        String value = "";

        Preferences.setAuthToken(context, value);

        assertThat(Preferences.getAuthToken(context), is(value));
    }

    @Test
    public void testGetAuthTokenAsNull() {
        Preferences.setAuthToken(context, null);

        assertThat(Preferences.getAuthToken(context), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    @Test
    public void testRemoveAuthToken() {
        String value = UUID.randomUUID().toString();

        Preferences.setAuthToken(context, value);
        assertThat(Preferences.getAuthToken(context), is(value));

        Preferences.removeAuthToken(context);

        assertThat(Preferences.getAuthToken(context), is(AUTH_TOKEN_DEFAULT_VALUE));
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

        Preferences.setLocation(context, location);
        assertThat(Preferences.getLocation(context), notNullValue());
        assertThat(Preferences.getLocation(context).getLatitude(), is(lat));
        assertThat(Preferences.getLocation(context).getLongitude(), is(lon));
    }

    @Test
    public void testSetZeroLocationReturnDefaultLocationOnGet() {
        double lat = 0.0;
        double lon = 0.0;

        Location location = new Location(LOCATION);
        location.setLongitude(lon);
        location.setLatitude(lat);

        Preferences.setLocation(context, location);

        assertThat(Preferences.getLocation(context), notNullValue());
        assertThat(Preferences.getLocation(context).getLatitude(), is(lat));
        assertThat(Preferences.getLocation(context).getLongitude(), is(lon));
    }

    @Test
    public void testGetLocationSetAsNull() {
        Preferences.setLocation(context, null);

        double lat = 0.0;
        double lon = 0.0;

        assertThat(Preferences.getLocation(context), notNullValue());
        assertThat(Preferences.getLocation(context).getLatitude(), is(lat));
        assertThat(Preferences.getLocation(context).getLongitude(), is(lon));
    }

    @Test
    public void testRemoveLocation() {
        Random random = new Random();
        double lat = random.nextDouble();
        double lon = random.nextDouble();
        Location location = new Location(LOCATION);
        location.setLongitude(lon);
        location.setLatitude(lat);

        Preferences.setLocation(context, location);
        Preferences.removeLocation(context);

        assertThat(Preferences.getLocation(context), notNullValue());
        assertThat(Preferences.getLocation(context).getLatitude(), is(0.0));
        assertThat(Preferences.getLocation(context).getLongitude(), is(0.0));
    }

    //This test should pass untill we implement Training logic
    @Test
    public void testTrainingShownIsTrue() {
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.removeTrainingFragmentShown(context);
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.setTrainingFragmentShown(context, 0);
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.setTrainingFragmentShown(context, 1);
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
        Preferences.removeTrainingFragmentShown(context);
        assertThat(Preferences.isTrainingFragmentShown(), is(true));
    }

    //Account Tests
    @Test
    public void testGetAccount() {
        String value = UUID.randomUUID().toString();

        Preferences.setAccount(context, value);

        assertThat(Preferences.getAccount(context), is(value));
    }

    @Test
    public void testSetEmptyAccountReturnEmptyTokenOnGet() {
        String value = "";

        Preferences.setAccount(context, value);

        assertThat(Preferences.getAccount(context), is(value));
    }

    @Test
    public void testGetAccountAsNull() {
        Preferences.setAccount(context, null);

        assertThat(Preferences.getAccount(context), is(ACCOUNT_DEFAULT_VALUE));
    }

    @Test
    public void testRemoveAccount() {
        String value = UUID.randomUUID().toString();

        Preferences.setAccount(context, value);
        assertThat(Preferences.getAccount(context), is(value));

        Preferences.removeAccount(context);

        assertThat(Preferences.getAccount(context), is(ACCOUNT_DEFAULT_VALUE));
    }

    //FirebaseInstanceId Tests
    @Test
    public void shouldSetAndGetFirebaseInstanceId() {
        String value = UUID.randomUUID().toString();

        Preferences.setFirebaseInstanceId(context, value);

        assertThat(Preferences.getFirebaseInstanceId(context), is(value));
    }

    @Test
    public void shouldSetEmptyFirebaseInstanceIdAndReturnEmptyFirebaseInstanceIdOnGet() {
        String value = "";

        Preferences.setFirebaseInstanceId(context, value);

        assertThat(Preferences.getFirebaseInstanceId(context), is(value));
    }

    @Test
    public void shouldGetDefaultFirebaseInstanceIdWhenSetAsNull() {
        Preferences.setFirebaseInstanceId(context, null);

        assertThat(Preferences.getFirebaseInstanceId(context), is(AUTH_TOKEN_DEFAULT_VALUE));
    }

    @Test
    public void shouldRemoveFirebaseInstanceId() {
        String value = UUID.randomUUID().toString();

        Preferences.setFirebaseInstanceId(context, value);
        assertThat(Preferences.getFirebaseInstanceId(context), is(value));

        Preferences.removeFirebaseInstanceId(context);

        assertThat(Preferences.getFirebaseInstanceId(context), is(AUTH_TOKEN_DEFAULT_VALUE));
    }
}