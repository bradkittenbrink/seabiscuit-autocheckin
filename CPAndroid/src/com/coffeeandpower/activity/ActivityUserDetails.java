package com.coffeeandpower.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.maps.MyItemizedOverlay2;
import com.coffeeandpower.views.CustomFontView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ActivityUserDetails extends MapActivity{

	private MapUserData mud;

	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyItemizedOverlay2 itemizedoverlay;

	// Views
	private Button buttonTitle;

	private CustomFontView textTitleNickName;
	private CustomFontView textNickName;

	private TextView textStatus;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_user_details);


		// Views
		buttonTitle = (Button) findViewById(R.id.button_location_name);
		textTitleNickName = (CustomFontView) findViewById(R.id.textview_user_name);
		textStatus = (TextView) findViewById(R.id.textview_user_place);
		textNickName = (CustomFontView) findViewById(R.id.textview_nick_name);
		mapView = (MapView) findViewById(R.id.mapview_user_details);
		Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker_iphone);
		itemizedoverlay = new MyItemizedOverlay2(drawable);

		// Get data from intent 
		Bundle extras = getIntent().getExtras();
		if (extras!=null){

			// INTENT FROM MAP !!!!
			mud = (MapUserData) extras.getSerializable("mapuserdata");

			// INTENT FORM LIST !!!!
			if (mud==null){

				//buttonTitle.setText(mud.getVenueName());
			}
		}


		// Set MapView
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);


		// Navigate map to location from intent data
		GeoPoint point = new GeoPoint((int)(mud.getLat()*1E6), (int)(mud.getLng()*1E6));
		mapController.animateTo(point);
		createMarker(point);


		// Set Views states
		if (mud!=null){

			textTitleNickName.setText(mud.getNickName());
			textStatus.setText(mud.getStatusText());
			textNickName.setText(mud.getNickName());

		}
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


	public void onClickBack (View v){

		onBackPressed();
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}


	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}


}
