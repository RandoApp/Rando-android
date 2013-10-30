package com.eucsoft.foodex.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.views.FoodexSurfaceView;

import static android.test.ViewAsserts.assertGroupNotContains;
import static android.test.ViewAsserts.assertOnScreen;

public class TakePictureActivityTest extends ActivityInstrumentationTestCase2<TakePictureActivity>

{
    //^Activity to test
    private TakePictureActivity takePictureActivity;
    private RelativeLayout takePictureScreen;
    private Button backButton;
    private Button selectPhotoButton;
    private Button takePictureButton;
    private Button uploadPhotoButton;
    private FoodexSurfaceView cameraPreview;

    // Be careful about letting the IDE create the constructor.  As of this writing,
    // it creates a constructor that's compiles cleanly but doesn't run any tests
    public TakePictureActivityTest() {
        super(TakePictureActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        takePictureActivity = getActivity();
        takePictureScreen = (RelativeLayout) takePictureActivity.findViewById(R.id.takepicture_layout);
        backButton = (Button) takePictureActivity.findViewById(R.id.back_button);
        selectPhotoButton = (Button) takePictureActivity.findViewById(R.id.select_photo_button);
        takePictureButton = (Button) takePictureActivity.findViewById(R.id.take_picture_button);
        uploadPhotoButton = (Button) takePictureActivity.findViewById(R.id.upload_photo_button);
        cameraPreview = (FoodexSurfaceView) takePictureActivity.findViewById(R.id.cameraPreview);
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testTakePictureOnStart() {
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), takePictureButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), backButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), selectPhotoButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), cameraPreview);
        assertGroupNotContains(takePictureScreen, uploadPhotoButton);
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testTakePictureAfterPhotoSelected() {
        assertGroupNotContains(takePictureScreen, takePictureButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), backButton);
        assertGroupNotContains(takePictureScreen, selectPhotoButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), cameraPreview);
        assertOnScreen(takePictureScreen, uploadPhotoButton);
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testTakePictureAfterPictureTaken() {
        TouchUtils.clickView(this, takePictureButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), takePictureButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), backButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), selectPhotoButton);
        assertOnScreen(takePictureActivity.getWindow().getDecorView(), cameraPreview);
        assertGroupNotContains(takePictureScreen, uploadPhotoButton);
    }
}
