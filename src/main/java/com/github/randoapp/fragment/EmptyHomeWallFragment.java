package com.github.randoapp.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.CameraActivity;
import com.github.randoapp.R;
import com.github.randoapp.adapter.EmptyHomePagerAdapter;
import com.github.randoapp.util.PermissionUtils;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE;
import static com.github.randoapp.Constants.CAMERA_PERMISSION_REQUEST_CODE;

public class EmptyHomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.empty_home, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.colums_pager);
        viewPager.setAdapter(new EmptyHomePagerAdapter(getActivity().getSupportFragmentManager()));
        viewPager.setCurrentItem(1);

        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionUtils.checkAndRequestMissingPermissions(getActivity(), CAMERA_PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA)) {
                    Intent intent = new Intent(rootView.getContext(), CameraActivity.class);
                    startActivityForResult(intent, CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE);
                }
            }
        });
        return rootView;
    }

}
