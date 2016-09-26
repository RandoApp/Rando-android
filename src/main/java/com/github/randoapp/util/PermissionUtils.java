package com.github.randoapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionUtils {

    private  static  final String[] allPermissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS};

    public static void requestMissingPermissions(Activity activity, String... permissions){

        if(permissions == null || permissions.length == 0){
            permissions = allPermissions;
        }

        List<String> neededPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.CAMERA)){

            } else {
                neededPermissions.add(Manifest.permission.CAMERA);
            }
        }
        neededPermissions.addAll(Arrays.asList(allPermissions));
        ActivityCompat.requestPermissions(activity, neededPermissions.toArray(new String[neededPermissions.size()]), 3);
    }
}
