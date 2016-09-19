package com.github.randoapp.util;

import android.content.pm.PackageManager;

import com.google.android.gms.common.GoogleApiAvailability;


public class GPSUtil {

    public static boolean isGPSUpateRequired(PackageManager packageManager){

        boolean isUpdateRequired = true;
        int versionCode = 0;
        try {
             versionCode = packageManager.getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (versionCode>5000000){
            isUpdateRequired = false;
        }
        return isUpdateRequired;
    }
}
