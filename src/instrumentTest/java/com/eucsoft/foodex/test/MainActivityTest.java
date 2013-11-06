package com.eucsoft.foodex.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

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
        Thread.sleep(2000);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (foodexMainActivity != null) {
            getInstrumentation().callActivityOnDestroy(foodexMainActivity);
            foodexMainActivity.finish();
            setActivity(null);
        }
    }

    // Methods whose names are prefixed with test will automatically be run
    @LargeTest
    public void te1stOnStartNotLoggedIn() {
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
        onView(withId(R.id.facebookButton)).check(matches(isDisplayed()));
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
    }

    /*@Override
    protected void runTest() throws Throwable {
        try {
            Log.i(MainActivityTest.class, "Checking........");
            onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
            super.runTest();
        } catch (Exception e) {
            Log.e(MainActivityTest.class, e.getMessage());
        }
    }*/
}