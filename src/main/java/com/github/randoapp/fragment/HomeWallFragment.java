package com.github.randoapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.CameraActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.adapter.HomePagerAdapter;

public class HomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.home, container, false);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.colums_pager);
        viewPager.setAdapter(new HomePagerAdapter(getActivity().getSupportFragmentManager()));

        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);
        takePictureButton.setEnabled(true);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), CameraActivity.class);
                startActivityForResult(intent, Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE);
            }
        });
        return  rootView;
    }
}
