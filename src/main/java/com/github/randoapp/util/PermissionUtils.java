package com.github.randoapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    private static final String[] allPermissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS};

    /**
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @return true if some of requested permissions are missign
     */
    public static boolean checkAndRequestMissingPermissions(Activity activity, int requestCode, String... permissions) {

        if (permissions == null || permissions.length == 0) {
            permissions = allPermissions;
        }
        List<String> neededPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, neededPermissions.toArray(new String[neededPermissions.size()]), requestCode);
        }
        return !neededPermissions.isEmpty();
    }
}
