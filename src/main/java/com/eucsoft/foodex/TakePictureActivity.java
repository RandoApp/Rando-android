package com.eucsoft.foodex;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eucsoft.foodex.service.UploadService;
import com.eucsoft.foodex.view.FoodexSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePictureActivity extends Activity {

    private FoodexSurfaceView foodexSurfaceView;

    private static final int REQ_CODE_SELECT_PHOTO = 100;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(Constants.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(TakePictureActivity.this,
                            R.string.photo_upload_ok,
                            Toast.LENGTH_LONG).show();
                    TakePictureActivity.this.setResult(Activity.RESULT_OK);
                    TakePictureActivity.this.finish();
                } else {
                    Toast.makeText(TakePictureActivity.this, R.string.photo_upload_failed,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap imageBitmap = null;

        switch (requestCode) {
            case REQ_CODE_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
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
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(Constants.UPLOAD_SERVICE_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        foodexSurfaceView = (FoodexSurfaceView) findViewById(R.id.cameraPreview);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        int bottomToolbarHeight = height - width - 50;

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

                int size = Math.min(originalBmp.getWidth(), originalBmp.getHeight());
                Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, 0, 0, size, size);

                File file = getOutputMediaFile();
                String imagePath = file.getAbsolutePath();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (Exception e) {
                    //TODO: Handle exception
                }

                //scan the image so show up in album
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{imagePath}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                //TODO: Call Service to upload
                Intent intent = new Intent(getApplicationContext(), UploadService.class);
                intent.putExtra(Constants.FILENAME, imagePath);
                startService(intent);
            }
        });
    }

    private void showUploadButton() {
        Button takePictureButton = (Button) findViewById(R.id.take_picture_button);
        takePictureButton.setVisibility(View.GONE);

        Button selectPhotoButton = (Button) findViewById(R.id.select_photo_button);
        selectPhotoButton.setVisibility(View.GONE);

        Button uploadPhotoButton = (Button) findViewById(R.id.upload_photo_button);
        uploadPhotoButton.setVisibility(View.VISIBLE);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.ALBUM_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foodexSurfaceView.setCurrentBitmap(null);
        foodexSurfaceView = null;
    }
}

