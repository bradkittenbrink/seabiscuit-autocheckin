package com.coffeeandpower.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.activity.ActivityMap.GeoUpdateHandler;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.maps.MyOverlays;
import com.coffeeandpower.views.CustomFontView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ActivityCheckIn extends MapActivity{

	private CustomFontView textTitle;
	
	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyOverlays itemizedoverlay;
	private MyLocationOverlay myLocationOverlay;
	
	private Venue venue;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_check_in);
		
		// Get Data from Intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){
			venue = (Venue) extras.getSerializable("venue");
		} else {
			venue = new Venue();
		}
		
		// Views
		textTitle = (CustomFontView) findViewById(R.id.text_title);
		mapView = (MapView) findViewById(R.id.imageview_mapview);
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		Drawable drawable = this.getResources().getDrawable(R.drawable.loc_point);
		itemizedoverlay = new MyOverlays(this, drawable);
		
		
		// Views states
		textTitle.setText(venue.getName());
		
		
		// Set others
		mapView.getOverlays().add(myLocationOverlay);
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);
		
		
		// Navigate map to location from intent data
		GeoPoint point = new GeoPoint((int)(venue.getLat()*1E6), (int)(venue.getLng()*1E6));
		mapController.animateTo(point);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void onClickCheckIn (View v){
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	
	
	

}
