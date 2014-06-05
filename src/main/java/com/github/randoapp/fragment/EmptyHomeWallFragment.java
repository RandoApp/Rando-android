package com.github.randoapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.CameraActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.util.RandoUtil;

import static com.github.randoapp.Constants.EMPTY_HOME_BROADCAST_EVENT;

public class EmptyHomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.empty_home, container, false);
        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), CameraActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        RandoUtil.initMenuButton(rootView, getActivity());
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE) {
            Intent intent = new Intent(EMPTY_HOME_BROADCAST_EVENT);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }
}
