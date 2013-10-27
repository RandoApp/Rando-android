package com.eucsoft.foodex.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;

import static android.test.ViewAsserts.assertOnScreen;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    //^Activity to test
    private MainActivity foodexMainActivity;
    private Button takePictureButton;

    // Be careful about letting the IDE create the constructor.  As of this writing,
    // it creates a constructor that's compiles cleanly but doesn't run any tests
    public MainActivityTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        foodexMainActivity = getActivity();
        takePictureButton = (Button) foodexMainActivity.findViewById(R.id.takePictureButton);
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testTakePictureButtonExist() {
        assertOnScreen(foodexMainActivity.getWindow().getDecorView(), takePictureButton);
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testTakePictureButtonExistFail() {
        assertOnScreen(foodexMainActivity.getWindow().getDecorView(), null);
    }

}