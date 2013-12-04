package com.eucsoft.foodex.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import static com.eucsoft.foodex.Constants.*;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.preferences.Preferences;
import static com.eucsoft.foodex.preferences.Preferences.*;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PreferencesTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        MainActivity.context = this.getContext();
        MainActivity.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public void testGetSessionCookie() {
        String value = UUID.randomUUID().toString();
        String domain = "domain.com";
        String path = "/";

        Preferences.setSessionCookie(value, domain, path);
        assertThat(Preferences.getSessionCookieValue(), is(value));
        assertThat(Preferences.getSessionCookieDomain(), is(domain));
        assertThat(Preferences.getSessionCookiePath(), is(path));
    }

    public void testGetSessionCookieSetEmptyValueDomainAndPath() {
        String value = "";
        String domain = "";
        String path = "";

        Preferences.setSessionCookie(value, domain, path);
        assertThat(Preferences.getSessionCookieValue(), is(value));
        assertThat(Preferences.getSessionCookieDomain(), is(domain));
        assertThat(Preferences.getSessionCookiePath(), is(path));
    }

    public void testGetSessionCookieSetCookieValueAsNull() {
        String domain = "domain.com";
        String path = "/";

        Preferences.setSessionCookie(null, domain, path);
        assertThat(Preferences.getSessionCookieValue(), is(SESSION_COOKIE_DEFAULT_VALUE));
        assertThat(Preferences.getSessionCookieDomain(), is(SERVER_HOST));
        assertThat(Preferences.getSessionCookiePath(), is(SESSION_COOKIE_PATH_DEFAULT_VALUE));
    }

    public void testGetSessionCookieSetDomainAndPathAsNull() {
        String value = UUID.randomUUID().toString();
        Preferences.setSessionCookie(value, null, null);
        assertThat(Preferences.getSessionCookieValue(), is(value));
        assertThat(Preferences.getSessionCookieDomain(), is(SERVER_HOST));
        assertThat(Preferences.getSessionCookiePath(), is(SESSION_COOKIE_PATH_DEFAULT_VALUE));
    }

    public void testRemoveSessionCookie() {
        String value = UUID.randomUUID().toString();
        String domain = "domain.com";
        String path = "/";

        Preferences.setSessionCookie(value, domain, path);
        assertThat(Preferences.getSessionCookieValue(), is(value));
        assertThat(Preferences.getSessionCookieDomain(), is(domain));
        assertThat(Preferences.getSessionCookiePath(), is(path));

        Preferences.removeSessionCookie();

        assertThat(Preferences.getSessionCookieValue(), is(SESSION_COOKIE_DEFAULT_VALUE));
        assertThat(Preferences.getSessionCookieDomain(), is(SERVER_HOST));
        assertThat(Preferences.getSessionCookiePath(), is(SESSION_COOKIE_PATH_DEFAULT_VALUE));
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
