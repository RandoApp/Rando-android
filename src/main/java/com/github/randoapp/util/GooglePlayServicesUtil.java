package com.github.randoapp.util;

import android.content.pm.PackageManager;

import com.github.randoapp.log.Log;
import com.google.android.gms.common.GoogleApiAvailability;


public class GooglePlayServicesUtil {

    private  static int MIN_GPS_VERISON = 5000000;

    public static boolean isGPSVersionLowerThanRequired(PackageManager packageManager){

        int versionCode = 0;
        try {
             versionCode = packageManager.getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(GooglePlayServicesUtil.class, "Google Play Services not installed at all");
        }
        return versionCode <= MIN_GPS_VERISON;
    }
}
