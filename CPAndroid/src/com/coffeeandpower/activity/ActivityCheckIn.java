package com.coffeeandpower.activity;

import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.views.CustomFontView;
import com.google.android.maps.MapActivity;

public class ActivityCheckIn extends MapActivity{

	private CustomFontView textTitle;
	
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
		
		// Views states
		textTitle.setText(venue.getName());
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public void onClickCheckIn (View v){
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	
	
	

}
