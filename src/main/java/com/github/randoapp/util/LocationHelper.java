package com.github.randoapp.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.github.randoapp.Constants;
import com.github.randoapp.preferences.Preferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Location Helper Class.
 * Handles creation of the Location Manager and Location Listener.
 *
 * @author Scott Helme
 */
public class LocationHelper {

    //my location manager and listener
    private LocationManager locationManager;
    private MyLocationListener locationListener;

    private Context context;

    /**
     * Constructor.
     *
     * @param context - The context of the calling activity.
     */
    public LocationHelper(Context context) {
        this.context = context;
    }

    public void updateLocationAsync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //setup the location manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //create the location listener
        locationListener = new MyLocationListener();

        //setup a callback for when the GRPS/WiFi gets a lock and we receive data
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, locationListener);
        }
        //setup a callback for when the GPS gets a lock and we receive data
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, locationListener);
        }

        //Set timer to kill location services after timeout
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                killLocationServices();
            }
        }, Constants.LOCATION_DETECT_TIMEOUT);
    }

    /**
     * Used to receive notifications from the Location Manager when they are sent. These methods are called when
     * the Location Manager is registered with the Location Service and a state changes.
     *
     * @author Scott Helme
     */
    public class MyLocationListener implements LocationListener {

        //called when the location service reports a change in location
        public void onLocationChanged(Location location) {
            Preferences.setLocation(context, location);
            //now we have our location we can stop the service from sending updates
            //comment out this line if you want the service to continue updating the users location
            killLocationServices();
        }

        //called when the provider is disabled
        public void onProviderDisabled(String provider) {
        }

        //called when the provider is enabled
        public void onProviderEnabled(String provider) {
        }

        //called when the provider changes state
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /**
     * Stop updates from the Location Service.
     */
    public void killLocationServices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public static boolean isGpsEnabled(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String providers = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (TextUtils.isEmpty(providers)) {
                return false;
            }
            return (providers.contains(LocationManager.GPS_PROVIDER) || providers.contains(LocationManager.NETWORK_PROVIDER));
        } else {
            final int locationMode;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            switch (locationMode) {
                case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:
                case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
                case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:
                    return true;
                case Settings.Secure.LOCATION_MODE_OFF:
                default:
                    return false;
            }
        }
    }
}