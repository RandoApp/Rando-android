package com.github.randoapp.test.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.github.randoapp.CameraActivity10;
import com.github.randoapp.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

public class TakePictureActivityTest extends ActivityInstrumentationTestCase2<CameraActivity10> implements ActivityTestI {

    //Activity to test
    private CameraActivity10 takePictureActivity;

    // Be careful about letting the IDE create the constructor.  As of this writing,
    // it creates a constructor that's compiles cleanly but doesn't run any tests
    public TakePictureActivityTest() {
        super(CameraActivity10.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        takePictureActivity = getActivity();
        Thread.sleep(ONE_SECOND * UGLY_DELAY_FOR_TRAVIS);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (takePictureActivity != null) {
            getInstrumentation().callActivityOnDestroy(takePictureActivity);
            takePictureActivity.finish();
            setActivity(null);
        }
        //Sleep is necessary because Camera Service is not always freed in time
        Thread.sleep(ONE_SECOND * UGLY_DELAY_FOR_TRAVIS);
    }

    // Methods whose names are prefixed with test will automatically be run
    @LargeTest
    public void ignoretestTakePictureOnStart() {
        onView(withId(R.id.camera_screen)).check(matches(isDisplayed()));
        onView(withId(R.id.capture_button)).check(matches(isDisplayed()));
        onView(withId(R.id.upload_button)).check(matches(not(isDisplayed())));
    }
}

