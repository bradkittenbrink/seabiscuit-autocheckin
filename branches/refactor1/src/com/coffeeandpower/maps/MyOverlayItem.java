package com.coffeeandpower.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	private String foursquareIdKey;

	private boolean isPin;

	private int lat;
	private int lng;

	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);

	}

	public void setMapUserData(String foursquareIdKey) {
		this.foursquareIdKey = foursquareIdKey;
	}

	public String getFoursquareIdKey() {
		return foursquareIdKey;
	}

	public void setMyLocationCoords(int lat, int lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public boolean isPin() {
		return isPin;
	}

	public void setPin(boolean isPin) {
		this.isPin = isPin;
	}

	public int getMyLatitude() {
		return lat;
	}

	public int getMyLongitude() {
		return lng;
	}
}
