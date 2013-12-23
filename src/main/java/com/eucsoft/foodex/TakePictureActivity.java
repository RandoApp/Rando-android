package com.eucsoft.foodex;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eucsoft.foodex.activity.BaseActivity;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.CropImageTask;
import com.eucsoft.foodex.task.FoodUploadTask;
import com.eucsoft.foodex.util.BitmapUtil;
import com.eucsoft.foodex.util.FileUtil;
import com.eucsoft.foodex.util.LocationUpdater;
import com.eucsoft.foodex.view.FoodexSurfaceView;

import java.util.Map;

public class TakePictureActivity extends BaseActivity implements TaskResultListener {
    private FoodexSurfaceView foodexSurfaceView;
    private Camera camera;
    private FrameLayout preview;

    private static final int REQ_CODE_SELECT_PHOTO = 100;
    private LocationUpdater locationUpdater = new LocationUpdater();

    public static Location currentLocation;
    public static String picFileName = null;
    private ImageButton uploadPictureButton;


    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String tmpFile = FileUtil.writeImageToTempFile(data);
            new CropImageTask(new CropTaskResult()).execute(tmpFile);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQ_CODE_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    showProgressbar("Processing...");
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

                   /* int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion < Build.VERSION_CODES.HONEYCOMB) {
                        foodexSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
                    }
                    foodexSurfaceView.setCurrentBitmap(BitmapFactory.decodeFile(filePath));*/
                    new CropImageTask(new CropTaskResult()).execute(filePath);
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        updateLocation();
        setBackButtonListener();
        setTakePictureButtonListener();
        setImageSelectButtonListener();
        setUploadButtonListener();
        normalizeCameraPreview();
    }

    private void setBackButtonListener() {
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setResult(RESULT_CANCELED);
                if (TextUtils.isEmpty(picFileName)) {
                    finish();
                } else {
                    picFileName = null;
                    releaseCamera();
                    hideUploadButton();
                    createCameraPreview();
                }
            }
        });
    }

    private void setTakePictureButtonListener() {
        ImageButton takePictureButton = (ImageButton) findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i(CropImageTask.class, "TOTAL 0:" + Runtime.getRuntime().totalMemory() / (1024 * 1024));
                Log.i(CropImageTask.class, "0:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
                showProgressbar("processing...");
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    private void setImageSelectButtonListener() {
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

    private void setUploadButtonListener() {
        uploadPictureButton = (ImageButton) findViewById(R.id.upload_photo_button);
        uploadPictureButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (picFileName != null) {
                    showProgressbar("uploading...");
                    uploadPictureButton.setEnabled(false);
                    ((LinearLayout) uploadPictureButton.getParent()).setBackgroundColor(getResources().getColor(R.color.button_disabled_background));
                    Log.i(CropImageTask.class, "00:" + Runtime.getRuntime().freeMemory() / (1024 * 1024));
                    new FoodUploadTask(new TaskResultListener() {
                        @Override
                        public void onTaskResult(int taskCode, long resultCode, Map<String, Object> data) {
                            hideProgressbar();
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
                        }
                    }).execute(picFileName);
                }
            }
        });
    }

    private void createCameraPreview() {
        camera = getCameraInstance();

        if (camera != null) {
            foodexSurfaceView = new FoodexSurfaceView(getApplicationContext(), camera);

            preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.removeAllViews();
            preview.addView(foodexSurfaceView);

        } else {
            //TODO: Handle camera not available.
        }
    }

    private void normalizeCameraPreview() {

        WindowManager windowManager = (WindowManager) App.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        int bottomToolbarHeight = display.getHeight() - display.getWidth() - Constants.TOP_PANEL_ON_TAKEPICSCREEN_HEIGHT;

        LinearLayout bottomPanel = (LinearLayout) findViewById(R.id.bottom_panel);
        RelativeLayout.LayoutParams bottomPanelParams = (RelativeLayout.LayoutParams) bottomPanel.getLayoutParams();

        bottomPanelParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        bottomPanelParams.height = bottomToolbarHeight;
    }

    private void updateLocation() {
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

        uploadPictureButton.setEnabled(true);
        ((LinearLayout) uploadPictureButton.getParent()).setBackgroundColor(getResources().getColor(R.color.auth_button));
        ImageButton uploadPhotoButton = (ImageButton) findViewById(R.id.upload_photo_button);
        uploadPhotoButton.setVisibility(View.VISIBLE);
    }

    private void hideUploadButton() {
        ImageButton takePictureButton = (ImageButton) findViewById(R.id.take_picture_button);
        takePictureButton.setVisibility(View.VISIBLE);

        ImageButton selectPhotoButton = (ImageButton) findViewById(R.id.select_photo_button);
        LinearLayout selectPhotoContainer = (LinearLayout) findViewById(R.id.select_photo_button_container);
        selectPhotoButton.setVisibility(View.VISIBLE);
        selectPhotoContainer.setVisibility(View.VISIBLE);

        ImageButton uploadPhotoButton = (ImageButton) findViewById(R.id.upload_photo_button);
        uploadPhotoButton.setVisibility(View.GONE);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, Map<String, Object> data) {

        switch (taskCode) {
            case FoodUploadTask.TASK_ID:
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

    private void releaseCamera() {
        if (preview != null) {
            preview.removeAllViews();
        }
        if (camera != null) {
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
        locationUpdater.cancelTimer();
    }

    class CropTaskResult implements TaskResultListener {
        @Override
        public void onTaskResult(int taskCode, long resultCode, Map<String, Object> data) {
            if (resultCode == BaseTask.RESULT_OK) {
                picFileName = (String) data.get(Constants.FILEPATH);
                showUploadButton();
                Log.i(TakePictureActivity.class, "" + foodexSurfaceView.getWidth());

                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picFileName, options);

                // Calculate inSampleSize
                options.inSampleSize = BitmapUtil.calculateInSampleSize(options, preview.getWidth(), preview.getWidth());

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(picFileName, options);
                preview.removeAllViews();
                ImageView imagePreview = new ImageView(getApplicationContext());
                imagePreview.setImageBitmap(bitmap);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(preview.getWidth(), preview.getWidth());
                imagePreview.setLayoutParams(layoutParams);
                preview.addView(imagePreview);

            } else {
                Toast.makeText(TakePictureActivity.this, "Crop Failed.",
                        Toast.LENGTH_LONG).show();
            }
            hideProgressbar();
        }
    }
}

