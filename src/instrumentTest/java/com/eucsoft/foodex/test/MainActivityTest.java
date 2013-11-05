package com.eucsoft.foodex.test;

import android.test.ActivityInstrumentationTestCase2;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    //^Activity to test
    private MainActivity foodexMainActivity;

    // Be careful about letting the IDE create the constructor.  As of this writing,
    // it creates a constructor that's compiles cleanly but doesn't run any tests
    public MainActivityTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        foodexMainActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testOnStartNotLoggedIn() {
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
        onView(withId(R.id.facebookButton)).check(matches(isDisplayed()));
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
    }
}