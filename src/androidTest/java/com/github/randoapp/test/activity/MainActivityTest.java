package com.github.randoapp.test.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.github.randoapp.MainActivity;
import com.github.randoapp.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> implements ActivityTestI {
    //^Activity to test
    private MainActivity randoMainActivity;

    // Be careful about letting the IDE create the constructor.  As of this writing,
    // it creates a constructor that's compiles cleanly but doesn't run any tests
    public MainActivityTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        randoMainActivity = getActivity();
        Thread.sleep(ONE_SECOND * UGLY_DELAY_FOR_TRAVIS);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (randoMainActivity != null) {
            getInstrumentation().callActivityOnDestroy(randoMainActivity);
            randoMainActivity.finish();
            setActivity(null);
        }
        Thread.sleep(ONE_SECOND * UGLY_DELAY_FOR_TRAVIS);
    }

    @LargeTest
    public void ignoretestSkip() {
        onView(withId(R.id.auth_root_scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewSkipLink)).perform(scrollTo());
        onView(withId(R.id.textViewSkipLink)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewSkipLink)).perform(click());
        onView(withId(R.id.camera_button)).check(matches(isDisplayed()));
    }
}