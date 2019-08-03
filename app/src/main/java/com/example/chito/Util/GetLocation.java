package com.example.chito.Util;

import android.location.Location;
import android.os.Bundle;

public interface GetLocation {
    public void onLocationChanged(Location location);
    public void onStatusChanged(String s, int i, Bundle bundle);
    public void onProviderEnabled(String s);
    public void onProviderDisabled(String s);
}
