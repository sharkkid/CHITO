package com.example.chito.Util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*---------- Listener class to get coordinates ------------- */
public class GPSListner implements LocationListener {

    private static final String TAG = "GPSListner";
    private Context context;

    public GPSListner(Context context){
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.d("loc", String.valueOf(loc));
        Toast.makeText(context,"Location changed: Lat: " + loc.getLatitude() + " Lng: "+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.d(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.d(TAG, latitude);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
