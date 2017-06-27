package com.github.randoapp.test.activity;

import android.test.ActivityInstrumentationTestCase2;

import com.github.randoapp.CameraActivity10;

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
}

