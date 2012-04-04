package com.coffeeandpower.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.cont.User;
import com.coffeeandpower.maps.MyItemizedOverlay;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
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
	private static final int GET_MAP_DATA_SET = 3;

	// Views
	private CustomFontView textNickName;
	private ProgressDialog progress;
	private HorizontalPager pager;


	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay myLocationOverlay;
	private MyItemizedOverlay itemizedoverlay;
	private LocationManager locationManager;

	private User loggedUser;

	private DataHolder result;
	private DataHolder resultMapDataSet;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what){

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityMap.this, "Error", "Internet connection error").show();
				break;

			case AppCAP.HTTP_REQUEST_SUCCEEDED:
				if (result.getObject()!=null){
					loggedUser = (User) result.getObject();
					useUserData();
				}
				break;

			case GET_MAP_DATA_SET:
				if (resultMapDataSet.getObject()!=null){

					HashMap<String,MapUserData> mapUsersArray = (HashMap<String,MapUserData>) resultMapDataSet.getObject();
					RootActivity.log("ActivityMap_mapUsersArray.size()=" + mapUsersArray.size());

					for (Map.Entry<String, MapUserData> mud: mapUsersArray.entrySet()){

						GeoPoint gp = new GeoPoint((int)(mud.getValue().getLat()*1E6), (int)(mud.getValue().getLng()*1E6));
						createMarker(gp);
					}
					mapView.invalidate();
				}
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
		setContentView(R.layout.activity_map);

		// Views
		pager = (HorizontalPager) findViewById(R.id.pager);
		mapView = (MapView) findViewById(R.id.mapview);
		textNickName = (CustomFontView) findViewById(R.id.text_nick_name);
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		progress = new ProgressDialog(ActivityMap.this);
		Drawable drawable = this.getResources().getDrawable(R.drawable.people_marker_turquoise_circle);
		itemizedoverlay = new MyItemizedOverlay(drawable);


		// Views states
		pager.setCurrentScreen(SCREEN_MAP, false);
		progress.setMessage("Getting user data...");


		// Set others
		mapView.getOverlays().add(myLocationOverlay);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		try {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new GeoUpdateHandler());
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			new CustomDialog(ActivityMap.this, "Info", "Location Manager error").show();
		}
		mapController = mapView.getController();
		mapController.setZoom(12);


		// User is logged in, get user data
		progress.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				result = AppCAP.getConnection().getUserData();
				if (result!=null){
					handler.sendEmptyMessage(result.getResponseCode());
				} else {
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				}
			}
		}).start();

	}


	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		mapController.setZoom(17);
	}


	private void createMarker(GeoPoint point) {

		OverlayItem overlayitem = new OverlayItem(point, "", "");
		itemizedoverlay.addOverlay(overlayitem);
		if (itemizedoverlay.size() > 0) {
			mapView.getOverlays().add(itemizedoverlay);
		}
	}


	// We have user data from logged user, use it now...
	public void useUserData(){

		textNickName.setText(loggedUser.getNickName());
	}

	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			//int lat = (int) (location.getLatitude() * 1E6);
			//int lng = (int) (location.getLongitude() * 1E6);
			//GeoPoint point = new GeoPoint(lat, lng);

			//RootActivity.log("ActivityMap locationChanged: " + lat+":"+lng);
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


	public void onClickSettings (View v){

		if (pager.getCurrentScreen()==SCREEN_MAP){
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_MAP, true);
		}

	}


	public void onClickAccountSettings (View v){

		Intent intent = new Intent(ActivityMap.this, ActivitySettings.class);
		startActivity(intent);
	}


	public void onClickWallet (View v){

		Toast.makeText(this, "onClickWallet", Toast.LENGTH_SHORT).show();
	}


	public void onClickLogout (View v){

		//HttpUtil.logout();
		AppCAP.setUserEmail("");
		onBackPressed();
		Toast.makeText(this, "onClickLogout", Toast.LENGTH_SHORT).show();
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
		if (myLocationOverlay!=null){
			if (myLocationOverlay.getMyLocation()!=null){
				mapController.animateTo(myLocationOverlay.getMyLocation());
				mapController.setZoom(17);
			}
		}
	}


	public void onClickRefresh (View v) {

		for (int i=mapView.getOverlays().size(); i>1; i--){
			mapView.getOverlays().remove(i-1);
		}
		mapView.postInvalidate();
		
		RootActivity.log("ActivityMap_mapView.getOverlays().size=" + mapView.getOverlays().size());
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultMapDataSet = AppCAP.getConnection().getCheckedInBoundsOverTime(mapView);
				if (resultMapDataSet!=null){
					handler.sendEmptyMessage(GET_MAP_DATA_SET);
				} else {
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				}
			}
		}).start();

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
