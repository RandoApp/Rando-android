package com.github.randoapp.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.util.PermissionUtils;

public class MissingStoragePermissionFragment extends Fragment {

    private boolean requestStorageOnStart = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.home_storage_permission_missing, container, false);

        Button enableStorageAccess = (Button) rootView.findViewById(R.id.enable_storage);
        enableStorageAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtils.checkAndRequestMissingPermissions(getActivity(), Constants.STORAGE_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestStorageOnStart){
            PermissionUtils.checkAndRequestMissingPermissions(getActivity(), Constants.STORAGE_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requestStorageOnStart = false;
        }
    }
}