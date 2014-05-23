package com.github.randoapp.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.randoapp.R;
import com.github.randoapp.util.RandoUtil;

public class CameraUploadFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera_upload, container, false);
        ImageView uploadButton = (ImageView) rootView.findViewById(R.id.camera_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Uploading...", Toast.LENGTH_LONG).show();
            }
        });
        RandoUtil.initMenuButton(rootView, getActivity());
        return rootView;
    }
}
