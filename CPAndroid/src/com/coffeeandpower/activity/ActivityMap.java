package com.coffeeandpower.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.maps.MyOverlays;
import com.coffeeandpower.views.HorizontalPager;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class ActivityMap extends MapActivity{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_MAP = 1;

	private HorizontalPager pager;

	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyOverlays itemizedoverlay;
	private MyLocationOverlay myLocationOverlay;
	private LocationManager locationManager;



	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_map);

		// Views
		pager = (HorizontalPager) findViewById(R.id.pager);
		mapView = (MapView) findViewById(R.id.mapview);
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		Drawable drawable = this.getResources().getDrawable(R.drawable.loc_point);
		itemizedoverlay = new MyOverlays(this, drawable);


		// Views states
		pager.setCurrentScreen(SCREEN_MAP, false);


		// Set others
		mapView.getOverlays().add(myLocationOverlay);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new GeoUpdateHandler());
		mapController = mapView.getController();
		mapController.setZoom(12);

	}


	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}


	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);


			RootActivity.log("ActivityMap locationChanged: " + lat+":"+lng);
		}

		@Override
		public void onProviderDisabled(String provider) {
			RootActivity.log("ActivityMap provider: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			RootActivity.log("ActivityMap providerEnabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			RootActivity.log("ActivityMap statusChanged");
		}
	}


	private void createMarker() {
		GeoPoint p = mapView.getMapCenter();
		OverlayItem overlayitem = new OverlayItem(p, "", "");
		itemizedoverlay.addOverlay(overlayitem);
		if (itemizedoverlay.size() > 0) {
			mapView.getOverlays().add(itemizedoverlay);
		}
	}


	public void onClickSettings (View v){

		if (pager.getCurrentScreen()==SCREEN_MAP){
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_MAP, true);
		}

	}


	public void onClickCheckIn (View v){
		
		if (myLocationOverlay.getMyLocation()!=null){
			
			Intent intent = new Intent(ActivityMap.this, ActivityCheckInList.class);
			intent.putExtra("lat", myLocationOverlay.getMyLocation().getLatitudeE6());
			intent.putExtra("lng", myLocationOverlay.getMyLocation().getLongitudeE6());
			startActivity(intent);
		}
	}


	public void onClickLocateMe (View v) {
		mapController.animateTo(myLocationOverlay.getMyLocation());
		mapController.setZoom(17);
	}


	public void onClickRefresh (View v) {

	}


	public void onClickPeopleList (View v){

	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		myLocationOverlay.disableMyLocation();
	}


}
