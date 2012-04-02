package com.coffeeandpower.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.maps.MyOverlays;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.CustomSeek;
import com.coffeeandpower.views.CustomSeek.HoursChangeListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ActivityCheckIn extends MapActivity{

	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyOverlays itemizedoverlay;
	private MyLocationOverlay myLocationOverlay;

	private Venue venue;

	// Views
	private CustomFontView textHours;
	private CustomFontView textTitle;
	private CustomSeek hoursSeek;

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
		textHours = (CustomFontView) findViewById(R.id.textview_hours);
		hoursSeek = (CustomSeek) findViewById(R.id.seekbar_hours);
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

		// Listener for Hours change on SeekBar
		hoursSeek.setOnHoursChangeListener(new HoursChangeListener() {
			@Override
			public void onHoursChange(int hours) {
				switch (hours){
				case 1:
					textHours.setText(hours + " hour");
					break;
				
				default:
					textHours.setText(hours + " hours");
				}
			}
		});
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
