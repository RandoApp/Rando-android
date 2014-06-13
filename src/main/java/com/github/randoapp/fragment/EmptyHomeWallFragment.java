package com.github.randoapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.CameraActivity;
import com.github.randoapp.R;
import com.github.randoapp.util.RandoUtil;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE;

public class EmptyHomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.empty_home, container, false);
        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), CameraActivity.class);
                startActivityForResult(intent, CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE);
            }
        });

        RandoUtil.initMenuButton(rootView, getActivity());
        return rootView;
    }

}
