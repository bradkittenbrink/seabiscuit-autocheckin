package com.coffeeandpower.activity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.maps.MyItemizedOverlay;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.CustomSeek;
import com.coffeeandpower.views.CustomSeek.HoursChangeListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ActivityCheckIn extends MapActivity{

	private static final int GET_CHECKED_USERS = 8;
	
	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyItemizedOverlay itemizedoverlay;

	private Venue venue;

	// Views
	private CustomFontView textHours;
	private CustomFontView textTitle;
	private CustomFontView textName;
	private CustomFontView textStreet;
	private CustomSeek hoursSeek;
	private EditText statusEditText;
	private ProgressDialog progress;
	
	private int checkInDuration;
	
	private DataHolder resultCheckIn;
	private DataHolder resultGetUsersCheckedIn;
	
	
	{
		checkInDuration = 1; 	// default 1 hour checkin duration, slider sets other values
		
		
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what){

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityCheckIn.this, "Error", "Internet connection error").show();
				break;

			case AppCAP.HTTP_REQUEST_SUCCEEDED:
				setResult(AppCAP.ACT_QUIT);
				ActivityCheckIn.this.finish();
				break;
				
			case GET_CHECKED_USERS:
				break;

			}
		}

	};
	
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
		textName = (CustomFontView) findViewById(R.id.textview_name);
		textStreet = (CustomFontView) findViewById(R.id.textview_street);
		hoursSeek = (CustomSeek) findViewById(R.id.seekbar_hours);
		statusEditText = (EditText) findViewById(R.id.edittext_optional);
		mapView = (MapView) findViewById(R.id.imageview_mapview);
		progress = new ProgressDialog(this);
		Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker_iphone);
		itemizedoverlay = new MyItemizedOverlay(drawable);


		// Views states
		textTitle.setText(venue.getName());
		textStreet.setText(venue.getAddress());
		textName.setText(venue.getName());
		progress.setMessage("Checking in...");

		
		// Set others
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);


		// Navigate map to location from intent data
		GeoPoint point = new GeoPoint((int)(venue.getLat()*1E6), (int)(venue.getLng()*1E6));
		mapController.animateTo(point);
		createMarker(point);
		
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
		
		// Get users checked in venue
		getUsersCheckedIn(venue);
		
	}

	public void getUsersCheckedIn (Venue v){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultGetUsersCheckedIn = AppCAP.getConnection().getUsersCheckedInAtFoursquareID(venue);
				if (resultCheckIn!=null){
					handler.sendEmptyMessage(GET_CHECKED_USERS);
				}
			}
		}).start();
	}
	
	private void createMarker(GeoPoint point) {
		OverlayItem overlayitem = new OverlayItem(point, "", "");
		itemizedoverlay.addOverlay(overlayitem);
		if (itemizedoverlay.size() > 0) {
			mapView.getOverlays().add(itemizedoverlay);
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	public void onClickCheckIn (View v){
		final int checkInTime = (int) (System.currentTimeMillis() / 1000);
		final int checkOutTime = checkInTime + checkInDuration * 3600;
		
		progress.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultCheckIn = AppCAP.getConnection().checkIn(venue, checkInTime, checkOutTime, statusEditText.getText().toString());
				if (resultCheckIn!=null){
					handler.sendEmptyMessage(resultCheckIn.getResponseCode());
				} else {
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				}
			}
		}).start();
		
		
		
	}

	
	public void onClickBack (View v) {
	
		onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}






}
