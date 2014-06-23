package com.github.randoapp.camera;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.randoapp.CameraActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.UploadService;
import com.github.randoapp.task.CropImageTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.BitmapUtil;
import com.makeramen.RoundedImageView;

import java.util.Date;
import java.util.Map;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class CameraUploadFragment extends SherlockFragment {

    private String picFileName;
    private RoundedImageView preview;
    private ImageView uploadButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera_upload, container, false);

        preview = (RoundedImageView) rootView.findViewById(R.id.preview);

        Bundle bundle = getArguments();
        String fileToCrop = bundle.getString(Constants.FILEPATH);

        prepareForUpload(fileToCrop);

        uploadButton = (ImageView) rootView.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new UploadButtonListner());
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
                        Log.e(CropImageTask.class, "Can not crop image");
                    }
                })
                .execute();
    }

    private class UploadButtonListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (picFileName == null) {
                return;
            }

            Location location = Preferences.getLocation();
            RandoDAO.addToUpload(new RandoUpload(picFileName, location.getLatitude(), location.getLongitude(), new Date()));

            Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }
}
