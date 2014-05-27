package com.github.randoapp.camera;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.randoapp.R;

import java.io.File;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_PHOTO_PATH;

public class CameraUploadFragment extends SherlockFragment {

    private File photo;

    public CameraUploadFragment(File photo) {
        this.photo = photo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera_upload, container, false);

        ImageView preview = (ImageView) rootView.findViewById(R.id.preview);
//        preview.setImageBitmap(BitmapFactory.decodeFile(photo.getPath()));

        ImageView uploadButton = (ImageView) rootView.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new UploadButtonListner());
        return rootView;
    }

    private class UploadButtonListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }
}
