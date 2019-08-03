package com.example.chito.Util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


public class CurrentLocation implements LocationListener {

    Context context;
    LocationManager locationManager;
    String provider;

    GetLocation getLocation;

    public CurrentLocation(Context context) {

        this.context = context;
        getLocation = (GetLocation) context;
        location();
    }

    public void location() {
        // Getting LocationManager object
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // anruag getting last location
        //  Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals(" ")) {

            // Get the location from the given provider
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, (android.location.LocationListener) this);

            if (location != null)
                onLocationChanged(location);
            else {

            }
            // Toast.makeText(context, "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // Log.e("Location", location.getProvider() + "==" + location.getAccuracy() + "==" + location.getAltitude() + "==" + location.getLatitude() + "==" + location.getLongitude());
        getLocation.onLocationChanged(location);
        String message = String.format(
                "New Location \n Longitude: %1$s \n Latitude: %2$s",
                location.getLongitude(), location.getLatitude());

        GlobalValue.Latitude = Double.parseDouble(String.valueOf(location.getLatitude()));
        GlobalValue.Longtitude = Double.parseDouble(String.valueOf(location.getLongitude()));
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        Log.d("GPS", message);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e("onStatusChanged", "==" + s);
        getLocation.onStatusChanged(s, i, bundle);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e("onProviderEnabled", "==" + s);
        getLocation.onProviderEnabled(s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e("onProviderDisabled", "==" + s);
        getLocation.onProviderDisabled(s);
    }

}