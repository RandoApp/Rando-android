package com.github.randoapp.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.randoapp.CameraActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.service.SyncService;
import com.github.randoapp.task.CropImageTask;
import com.github.randoapp.task.UploadTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.BitmapUtil;
import com.makeramen.RoundedImageView;

import java.util.Map;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class CameraUploadFragment extends SherlockFragment {

    private String picFileName;
    private RoundedImageView preview;
    private ImageView uploadPictureButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera_upload, container, false);

        preview = (RoundedImageView) rootView.findViewById(R.id.preview);

        Bundle bundle = getArguments();
        String fileToCrop = bundle.getString(Constants.FILEPATH);

        prepareForUpload(fileToCrop);

        uploadPictureButton = (ImageView) rootView.findViewById(R.id.upload_button);
        uploadPictureButton.setOnClickListener(new UploadButtonListner());
        return rootView;
    }


    private void prepareForUpload(String fileToCrop) {
        new CropImageTask(fileToCrop)
                .onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        picFileName = (String) data.get(Constants.FILEPATH);
                        preview.setImageBitmap(BitmapUtil.decodeSampledBitmap(picFileName, preview.getWidth(), preview.getWidth()));
                    }
                })
                .onError(new OnError() {
                    @Override
                    public void onError(Map<String, Object> data) {
                    }
                })
                .onDone(new OnDone() {
                    @Override
                    public void onDone(Map<String, Object> data) {

                    }
                })
                .execute();
    }

    private class UploadButtonListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (picFileName != null) {
                uploadPictureButton.setEnabled(false);
                new UploadTask(picFileName).onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        picFileName = null;
                        SyncService.run();
                    }
                }).onError(new OnError() {
                    @Override
                    public void onError(Map<String, Object> data) {
                        String error = (String) data.get("error");
                        if (error != null) {
                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.photo_upload_failed,
                                    Toast.LENGTH_LONG).show();
                        }

                        if (uploadPictureButton != null) {
                            uploadPictureButton.setEnabled(true);
                        }
                    }
                }).onDone(new OnDone() {
                    @Override
                    public void onDone(Map<String, Object> data) {
                        Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                }).execute();
            }
        }
    }
}
