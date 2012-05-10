package com.coffeeandpower.utils;

import java.io.IOException;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class MockLocationProvider extends Thread {


    private LocationManager locationManager;

    private String mocLocationProvider;

    private String LOG_TAG = "LOG";

    public MockLocationProvider(LocationManager locationManager, String mocLocationProvider) throws IOException {

	this.locationManager = locationManager;
	this.mocLocationProvider = mocLocationProvider;
    }

    @Override
    public void run() {

	Double latitude = 37.771069d;
	Double longitude = -122.424060d;
	Location location = new Location(mocLocationProvider);
	location.setLatitude(latitude);
	location.setLongitude(longitude);

	Log.e(LOG_TAG, location.toString());

	location.setTime(System.currentTimeMillis());

	locationManager.setTestProviderLocation(mocLocationProvider, location);
    }
}
