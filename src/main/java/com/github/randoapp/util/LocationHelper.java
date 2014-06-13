package com.github.randoapp.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/***
 * Location Helper Class.
 * Handles creation of the Location Manager and Location Listener.
 *
 * @author Scott Helme
 */
public class LocationHelper{

    //variables to store lat and long
    private float latitude  = 0.0f;
    private float longitude = 0.0f;
    private Location location;

    //flag for when we have co-ords
    private boolean gotLocation = false;

    //my location manager and listener
    private LocationManager    locationManager;
    private MyLocationListener locationListener;

    /**
     * Constructor.
     *
     * @param context - The context of the calling activity.
     */
    public LocationHelper(Context context){

        //setup the location manager
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        //create the location listener
        locationListener = new MyLocationListener();

        //setup a callback for when the GRPS/WiFi gets a lock and we receive data
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //setup a callback for when the GPS gets a lock and we receive data
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /***
     * Used to receive notifications from the Location Manager when they are sent. These methods are called when
     * the Location Manager is registered with the Location Service and a state changes.
     *
     * @author Scott Helme
     */
    public class MyLocationListener implements LocationListener {

        //called when the location service reports a change in location
        public void onLocationChanged(Location location) {

            //store lat and long and location itself
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
            LocationHelper.this.location = location;

            //now we have our location we can stop the service from sending updates
            //comment out this line if you want the service to continue updating the users location
            locationManager.removeUpdates(locationListener);

            //change the flag to indicate we now have a location
            gotLocation = true;
        }

        //called when the provider is disabled
        public void onProviderDisabled(String provider) {}
        //called when the provider is enabled
        public void onProviderEnabled(String provider) {}
        //called when the provider changes state
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    /***
     * Stop updates from the Location Service.
     */
    public void killLocationServices(){
        locationManager.removeUpdates(locationListener);
    }

    /***
     * Get Latitude from GPS Helper.
     * Should check gotLocation() first.
     * @return - The current Latitude.
     */
    public float getLat(){
        return latitude;
    }

    /***
     * Get Longitude from GPS Helper.
     * Should check gotLocation() first.
     * @return - The current Longitude.
     */
    public float getLong(){
        return longitude;
    }

    /***
     * Get Location from GPS Helper.
     * Should check gotLocation() first.
     * @return - The current Longitude.
     */
    public Location getLocation(){
        return location;
    }

    /***
     * Check if a location has been found yet.
     * @return - True if a location has been acquired. False otherwise.
     */
    public Boolean gotLocation(){
        return gotLocation;
    }
}