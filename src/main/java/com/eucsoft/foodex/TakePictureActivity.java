package com.eucsoft.foodex;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.CreateFoodAndUploadTask;
import com.eucsoft.foodex.util.LocationUpdater;
import com.eucsoft.foodex.view.FoodexSurfaceView;

import java.util.HashMap;

public class TakePictureActivity extends Activity implements TaskResultListener {

    private FoodexSurfaceView foodexSurfaceView;

    private static final int REQ_CODE_SELECT_PHOTO = 100;
    private LocationUpdater locationUpdater = new LocationUpdater();

    public static Location currentLocation;

    private ImageButton uploadPictureButton;

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

                    foodexSurfaceView.releaseCamera();
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

        getWindow().setFormat(PixelFormat.UNKNOWN);

        WindowManager windowManager = (WindowManager) App.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        foodexSurfaceView = (FoodexSurfaceView) findViewById(R.id.cameraPreview);

        int bottomToolbarHeight = display.getHeight() - display.getWidth() - Constants.TOP_PANEL_ON_TAKEPICSCREEN_HEIGHT;

        LinearLayout bottomPanel = (LinearLayout) findViewById(R.id.bottom_panel);
        RelativeLayout.LayoutParams bottomPanelParams = (RelativeLayout.LayoutParams) bottomPanel.getLayoutParams();

        bottomPanelParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        bottomPanelParams.height = bottomToolbarHeight;

        ImageButton takePictureButton = (ImageButton) findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                foodexSurfaceView.takePicture();
                showUploadButton();
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        ImageButton openPictureButton = (ImageButton) findViewById(R.id.select_photo_button);
        openPictureButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType(Constants.IMAGE_FILTER);
                startActivityForResult(photoPickerIntent, REQ_CODE_SELECT_PHOTO);
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationUpdater.cancelTimer();
        foodexSurfaceView.setCurrentBitmap(null);
        foodexSurfaceView.releaseCamera();
        foodexSurfaceView = null;
    }
}

