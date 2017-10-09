package com.github.randoapp;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.upload.UploadJobScheduler;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.BitmapUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Date;

import io.fabric.sdk.android.Fabric;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE;
import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class ImageReviewUploadActivity extends FragmentActivity {

    private String originalPicFileName;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ImageView uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_upload);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Fabric.with(this, new Crashlytics());

        RoundedImageView preview = (RoundedImageView) findViewById(R.id.preview);

        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int previewSize = Math.min(display.getWidth(), display.getHeight());

        originalPicFileName = getIntent().getStringExtra(Constants.FILEPATH);
        preview.setImageBitmap(BitmapUtil.decodeSampledBitmap(originalPicFileName, previewSize, previewSize));

        uploadButton = (ImageView) findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new ImageReviewUploadActivity.UploadButtonListner());

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CameraActivity16.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    private class UploadButtonListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (originalPicFileName == null) {
                return;
            }

            if (Preferences.getEnableVibrate(getBaseContext())
                    && ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
            }

            uploadButton.setEnabled(false);
            Analytics.logUploadRando(mFirebaseAnalytics);
            Location location = Preferences.getLocation(getBaseContext());
            RandoUpload randoUpload = new RandoUpload(originalPicFileName, location.getLatitude(), location.getLongitude(), new Date());
            RandoDAO.addToUpload(getBaseContext(), randoUpload);

            UploadJobScheduler.scheduleUpload(getApplicationContext());

            Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            ImageReviewUploadActivity.this.setResult(CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE);
            ImageReviewUploadActivity.this.finish();
        }
    }
}