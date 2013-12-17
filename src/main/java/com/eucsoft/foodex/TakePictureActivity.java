package com.eucsoft.foodex;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.CreateFoodAndUploadTask;
import com.eucsoft.foodex.task.CropImageTask;
import com.eucsoft.foodex.util.FileUtil;
import com.eucsoft.foodex.util.LocationUpdater;
import com.eucsoft.foodex.view.FoodexSurfaceView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TakePictureActivity extends Activity implements TaskResultListener {

    private FoodexSurfaceView foodexSurfaceView;
    private Camera camera;
    private FrameLayout preview;

    private static final int REQ_CODE_SELECT_PHOTO = 100;
    private LocationUpdater locationUpdater = new LocationUpdater();

    public static Location currentLocation;

    private ImageButton uploadPictureButton;


    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String tmpFile = FileUtil.writeImageToTempFile(data);
            if(tmpFile != null){
                Toast.makeText(getApplicationContext(), tmpFile, Toast.LENGTH_LONG).show();
                new CropImageTask().execute(tmpFile);
            } else {
                Toast.makeText(getApplicationContext(), "error saving file", Toast.LENGTH_LONG).show();
            }
            showUploadButton();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQ_CODE_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    if (cursor == null) {
                        Log.w(TakePictureActivity.class, "Selecting from Album failed.");
                        break;
                    }
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    /*foodexSurfaceView.releaseCamera();*/
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion < Build.VERSION_CODES.HONEYCOMB)
                    {
                        foodexSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
                    }
                    foodexSurfaceView.setCurrentBitmap(BitmapFactory.decodeFile(filePath));
                    showUploadButton();
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        updateLocation();
        /*createCameraPreview();*/
        setBackButtonListener();
        setTakePictureButtonListener();
        setImageSelectButtonListener();
        setUploadButtonListener();

    }

    private void setBackButtonListener(){
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setTakePictureButtonListener(){
        ImageButton takePictureButton = (ImageButton) findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*foodexSurfaceView.takePicture();
                showUploadButton();*/
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    private void setImageSelectButtonListener(){
        ImageButton openPictureButton = (ImageButton) findViewById(R.id.select_photo_button);
        openPictureButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType(Constants.IMAGE_FILTER);
                startActivityForResult(photoPickerIntent, REQ_CODE_SELECT_PHOTO);
            }
        });
    }

    private void setUploadButtonListener(){
        uploadPictureButton = (ImageButton) findViewById(R.id.upload_photo_button);
        uploadPictureButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                uploadPictureButton.setEnabled(false);
                ((LinearLayout) uploadPictureButton.getParent()).setBackgroundColor(getResources().getColor(R.color.button_disabled_background));

                Bitmap originalBmp = foodexSurfaceView.getCurrentBitmap();

                CreateFoodAndUploadTask uploadTask = new CreateFoodAndUploadTask(TakePictureActivity.this, getApplicationContext());
                uploadTask.execute(originalBmp);
            }
        });

    }

    private void createCameraPreview(){
        camera = getCameraInstance();

        if (camera != null){
            foodexSurfaceView = new FoodexSurfaceView(getApplicationContext(), camera);

            preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(foodexSurfaceView);

        } else {
            //TODO: Handle camera not available.
        }


        /*getWindow().setFormat(PixelFormat.UNKNOWN);

        WindowManager windowManager = (WindowManager) App.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        foodexSurfaceView = (FoodexSurfaceView) findViewById(R.id.cameraPreview);

        int bottomToolbarHeight = display.getHeight() - display.getWidth() - Constants.TOP_PANEL_ON_TAKEPICSCREEN_HEIGHT;

        LinearLayout bottomPanel = (LinearLayout) findViewById(R.id.bottom_panel);
        RelativeLayout.LayoutParams bottomPanelParams = (RelativeLayout.LayoutParams) bottomPanel.getLayoutParams();

        bottomPanelParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        bottomPanelParams.height = bottomToolbarHeight;*/
    }

    private void updateLocation(){
        LocationUpdater.LocationResult locationResult = new LocationUpdater.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                currentLocation = location;
            }
        };
        locationUpdater.getLocation(getApplicationContext(), locationResult);
    }

    private void showUploadButton() {
        ImageButton takePictureButton = (ImageButton) findViewById(R.id.take_picture_button);
        takePictureButton.setVisibility(View.GONE);

        ImageButton selectPhotoButton = (ImageButton) findViewById(R.id.select_photo_button);
        LinearLayout selectPhotoContainer = (LinearLayout) findViewById(R.id.select_photo_button_container);
        selectPhotoButton.setVisibility(View.GONE);
        selectPhotoContainer.setVisibility(View.GONE);

        ImageButton uploadPhotoButton = (ImageButton) findViewById(R.id.upload_photo_button);
        uploadPhotoButton.setVisibility(View.VISIBLE);
    }

    /** A safe way to get an instance of the Camera object. */
    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Camera.Parameters params = c.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            List<Camera.Size> sizeList = params.getSupportedPictureSizes();

            //TODO: Set MAXIMUM Size
            params.setPictureSize(sizeList.get(0).width, sizeList.get(0).height);
            c.setParameters(params);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {

        switch (taskCode) {
            case CreateFoodAndUploadTask.TASK_ID:
                if (resultCode == BaseTask.RESULT_OK) {
                    Toast.makeText(TakePictureActivity.this,
                            R.string.photo_upload_ok,
                            Toast.LENGTH_LONG).show();
                    TakePictureActivity.this.setResult(Activity.RESULT_OK);
                    TakePictureActivity.this.finish();
                } else {
                    Toast.makeText(TakePictureActivity.this, R.string.photo_upload_failed,
                            Toast.LENGTH_LONG).show();
                }

                if (uploadPictureButton != null) {
                    uploadPictureButton.setEnabled(true);
                    ((LinearLayout) uploadPictureButton.getParent()).setBackgroundColor(getResources().getColor(R.color.auth_button));
                }
                break;
        }
    }

    private void releaseCamera(){
        preview.removeAllViews();
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createCameraPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        /*locationUpdater.cancelTimer();
        foodexSurfaceView.setCurrentBitmap(null);
        foodexSurfaceView.releaseCamera();
        foodexSurfaceView = null;*/
    }
}

