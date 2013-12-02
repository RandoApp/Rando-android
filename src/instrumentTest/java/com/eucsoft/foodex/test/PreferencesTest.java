package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.preferences.Preferences;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PreferencesTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        MainActivity.context = this.getContext();
    }

    public void testSetGetSessionCookie() {
        String newSessionCookie = UUID.randomUUID().toString();
        Preferences.setSessionCookie(newSessionCookie);
        assertThat(Preferences.getSessionCookie(), is(newSessionCookie));
    }

    public void testSetGetSessionCookieEmpty() {
        String newSessionCookie = "";
        Preferences.setSessionCookie(newSessionCookie);
        assertThat(Preferences.getSessionCookie(), is(newSessionCookie));
    }

    public void testSetGetSessionCookieNull() {
        String newSessionCookie = null;
        Preferences.setSessionCookie(newSessionCookie);
        assertThat(Preferences.getSessionCookie(), is(Preferences.SEESSION_COOKIE_DEFAULT_VALUE));
    }

    public void testRemoveSessionCookie() {
        String newSessionCookie = UUID.randomUUID().toString();
        Preferences.setSessionCookie(newSessionCookie);
        assertThat(Preferences.getSessionCookie(), is(newSessionCookie));
        Preferences.removeSessionCookie();
        assertThat(Preferences.getSessionCookie(), is(Preferences.SEESSION_COOKIE_DEFAULT_VALUE));
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
