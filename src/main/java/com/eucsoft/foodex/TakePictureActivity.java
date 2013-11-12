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
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

                    foodexSurfaceView.setCurrentBitmap(BitmapFactory.decodeFile(filePath));
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        WindowManager windowManager = (WindowManager) MainActivity.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        foodexSurfaceView = (FoodexSurfaceView) findViewById(R.id.cameraPreview);

        int bottomToolbarHeight = display.getHeight() - display.getWidth() - 50;

        LinearLayout bottomPanel = (LinearLayout) findViewById(R.id.bottom_panel);
        RelativeLayout.LayoutParams bottomPanelParams = (RelativeLayout.LayoutParams) bottomPanel.getLayoutParams();

        bottomPanelParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        bottomPanelParams.height = bottomToolbarHeight;

        Button takePictureButton = (Button) findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                foodexSurfaceView.takePicture();
                showUploadButton();
            }
        });

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button openPictureButton = (Button) findViewById(R.id.select_photo_button);
        openPictureButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQ_CODE_SELECT_PHOTO);
            }
        });

        Button uploadPictureButton = (Button) findViewById(R.id.upload_photo_button);
        uploadPictureButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
        Button takePictureButton = (Button) findViewById(R.id.take_picture_button);
        takePictureButton.setVisibility(View.GONE);

        Button selectPhotoButton = (Button) findViewById(R.id.select_photo_button);
        selectPhotoButton.setVisibility(View.GONE);

        Button uploadPhotoButton = (Button) findViewById(R.id.upload_photo_button);
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
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationUpdater.cancelTimer();
        foodexSurfaceView.setCurrentBitmap(null);
        foodexSurfaceView = null;
    }
}

