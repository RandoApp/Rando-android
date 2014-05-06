package com.github.randoapp.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.github.randoapp.Constants;
import com.github.randoapp.log.Log;

import java.util.Timer;
import java.util.TimerTask;

public class LocationUpdater {
    Timer timer;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    public boolean getLocation(Context context, LocationResult result) {
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.w(LocationUpdater.class, "Failed to check gps location provider: provider is not permitted.");
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.w(LocationUpdater.class, "Failed to check network location provider: provider is not permitted.");
        }

        if (!gps_enabled && !network_enabled)
            return false;

        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if (network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer = new Timer();
        timer.schedule(new GetLastLocation(), Constants.LOCATION_PERIOD);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (timer != null) {
                timer.cancel();
            }

            if (locationResult != null) {
                locationResult.gotLocation(location);
            }

            if (lm != null) {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerNetwork);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (timer != null) {
                timer.cancel();
            }

            if (locationResult != null) {
                locationResult.gotLocation(location);
            }

            if (lm != null) {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerGps);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            if (lm != null) {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);
            }

            if (locationResult == null) {
                return;
            }

            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }

        if (lm != null) {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}