package com.github.randoapp.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.CameraActivity16;
import com.github.randoapp.CameraActivity10;
import com.github.randoapp.R;
import com.github.randoapp.adapter.EmptyHomePagerAdapter;
import com.github.randoapp.api.API;

import static com.github.randoapp.Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE;

public class EmptyHomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.empty_home, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.colums_pager);
        viewPager.setAdapter(new EmptyHomePagerAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(1);

        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                     intent = new Intent(rootView.getContext(), CameraActivity10.class);
                } else {
                    intent = new Intent(rootView.getContext(), CameraActivity16.class);
                }
                startActivityForResult(intent, CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        API.syncUserAsync(null, null);
    }
}
