package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeandpower.db.CAPDao;
import com.coffeandpower.db.CASPSQLiteDatabase;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityLoginPage;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.User;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.maps.MyItemizedOverlay;
import com.coffeeandpower.maps.MyOverlayItem;
import com.coffeeandpower.maps.MyOverlayPin;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.urbanairship.UAirship;

public class ActivityMap extends MapActivity implements TabMenu, UserMenu{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_MAP = 1;
	private static final int GET_MAP_DATA_SET = 3;

	private static final int ACTIVITY_ACCOUNT_SETTINGS = 1888;
	public static final int ACCOUNT_CHANGED = 1900;

	private UserAndTabMenu menu;

	// Views
	private CustomFontView textNickName;
	private ProgressDialog progress;
	private HorizontalPagerModified pager;


	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay myLocationOverlay;
	private MyItemizedOverlay itemizedoverlay;
	private LocationManager locationManager;

	// Current user
	private User loggedUser;

	private DataHolder result;
	private DataHolder resultMapDataSet;

	private CAPDao capDao;

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

					@SuppressWarnings("unchecked")
					ArrayList<UserSmart> mapUsersArray = (ArrayList<UserSmart>) resultMapDataSet.getObject();

					RootActivity.log("ActivityMap_mapUsersArray.size()=" + mapUsersArray.size());

					// < Key, Value >  = < foursquareId, >
					HashMap<String, ArrayList<UserSmart>> mapKeyIsFoursquareId = new HashMap<String, ArrayList<UserSmart>>();
					HashSet<String> setFoursquareIds = new HashSet<String>();

					// Find all uniq foursquareIds
					for (UserSmart mud:mapUsersArray){
						setFoursquareIds.add(mud.getFoursquareId());

						//Log.d("LOG", "array: " + mud.getFoursquareId()+ " : " + mud.getNickName() + " chIn: " + mud.getCheckedIn());
					}

					// Find all MapUserData objects for every foursquareId
					for (String foursquareId:setFoursquareIds){

						// Array list without duplicates
						ArrayList<UserSmart> tmpArray = new ArrayList<UserSmart>();

						for (UserSmart mud:mapUsersArray){

							if (mud.getFoursquareId().equals(foursquareId)){
								tmpArray.add(mud);
							}
						}

						mapKeyIsFoursquareId.put(foursquareId, tmpArray);
					}


					// Reset table for new data
					capDao.open();
					capDao.deleteAllFromTable(CASPSQLiteDatabase.TABLE_MAP_USER_DATA); // reset table for new data

					// Use to determinate, am i checked in
					boolean yesIam = false;

					// Loop for creating markers on map, in this loop iterate thru all uniq foursquaresIds
					for (Entry<String, ArrayList<UserSmart>> itemWithKeyFoursquareId : mapKeyIsFoursquareId.entrySet()){

						int checkinsSum = 0;
						int hereNowCount = 0;
						String venueName = "";

						// init for coordinates of created point
						double lat = 0.0d;
						double lng = 0.0d;

						// we need to sum checkins for every user who checked in foursquareId
						for (UserSmart mud : itemWithKeyFoursquareId.getValue()){

							checkinsSum++;
							hereNowCount += mud.getCheckedIn();

							// no matter for loop, all items in loop have the same coordinates
							lat = mud.getLat();
							lng = mud.getLng();

							venueName = mud.getVenueName();

							// write data to database
							capDao.putUserSmartData(mud, itemWithKeyFoursquareId.getKey());

							// Check if current user is checked in
							if (AppCAP.getLoggedInUserId()==mud.getUserId()){
								if (mud.getCheckedIn()==1){
									yesIam = true;
								}
							} 
						}

						if (yesIam){
							AppCAP.setUserCheckedIn(true);
						} else {
							AppCAP.setUserCheckedIn(false);
						}
						checkUserState();


						// Create Pins, if we have checkedin user for foursquareId
						GeoPoint gp = new GeoPoint((int)(lat*1E6), (int)(lng*1E6));

						if (hereNowCount > 0){
							createPin(gp, itemWithKeyFoursquareId.getKey(), hereNowCount);
						} else {
							createMarker(gp, itemWithKeyFoursquareId.getKey(), checkinsSum, venueName);
						}
					}

					capDao.close();
					mapView.invalidate();
				}
				break;
			}
		}

	};

	/**
	 * Check if user is checked in or not
	 */
	private void checkUserState(){
		if (AppCAP.isUserCheckedIn()){
			((TextView)findViewById(R.id.textview_check_in)).setText("Check Out");
			((ImageView)findViewById(R.id.imageview_check_in_clock_hand)).setAnimation(AnimationUtils.loadAnimation(ActivityMap.this, R.anim.rotate_indefinitely));
		} else {
			((TextView)findViewById(R.id.textview_check_in)).setText("Check In");
		}
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.tab_activity_map);

		// User and Tab Menu
		menu = new UserAndTabMenu(this);
		menu.setOnUserStateChanged(new OnUserStateChanged() {

			@Override
			public void onCheckOut() {
				checkUserState();

				// Refresh Data
				refreshMapDataSet(findViewById(R.id.imagebutton_map_refresh_progress));
			}

			@Override
			public void onLogOut() {
				onBackPressed();
				startActivity(new Intent(ActivityMap.this, ActivityLoginPage.class));
			}
		});

		((RelativeLayout)findViewById(R.id.rel_map)).setBackgroundResource(R.drawable.bg_tabbar_selected);
		((ImageView)findViewById(R.id.imageview_map)).setImageResource(R.drawable.tab_map_pressed);
		((TextView)findViewById(R.id.text_map)).setTextColor(Color.WHITE);

		// Views
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		mapView = (MapView) findViewById(R.id.mapview);
		textNickName = (CustomFontView) findViewById(R.id.text_nick_name);
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		progress = new ProgressDialog(ActivityMap.this);
		Drawable drawable = this.getResources().getDrawable(R.drawable.people_marker_turquoise_circle);
		itemizedoverlay = new MyItemizedOverlay(drawable, mapView);


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
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(myLocationOverlay.getMyLocation());
				AppCAP.setUserCoordinates(getSWAndNECoordinatesBounds(mapView));
			}
		});

		mapController = mapView.getController();
		mapController.setZoom(12);


		// Database control
		capDao = new CAPDao(this);


		// User is logged in, get user data
		getUserData();

	}


	/**
	 * Get user data from server
	 */
	private void getUserData(){

		progress.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				result = AppCAP.getConnection().getUserData();
				if (result.getResponseCode()==AppCAP.HTTP_ERROR){
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				} else {
					handler.sendEmptyMessage(result.getResponseCode());
				}
			}
		}).start();
	}


	@Override
	protected void onResume() {
		super.onResume();

		if (AppCAP.shouldFinishActMap()){
			onBackPressed();
			AppCAP.setShouldFinishActMap(false);
		} else {

			myLocationOverlay.enableMyLocation();
			mapController.setZoom(17);

			// Temp solution for black space below mapView
			if (myLocationOverlay!=null){
				if (myLocationOverlay.getMyLocation()!=null){
					mapController.animateTo(myLocationOverlay.getMyLocation());
					mapController.setZoom(17);
				}
			}

			// Refresh Data
			refreshMapDataSet(findViewById(R.id.imagebutton_map_refresh_progress));
		}
	}


	/**
	 * Create point on Map with data from MapUserdata
	 * @param point
	 * @param foursquareIdKey
	 * @param checkinsSum
	 * @param venueName
	 * @param isList
	 */
	private void createMarker(GeoPoint point, String foursquareIdKey, int checkinsSum, String venueName) {

		if (foursquareIdKey!=null){
			//Log.d("LOG", "create marker");

			String checkStr = checkinsSum == 1 ? " checkin in the last week" : " checkins in the last week";
			venueName = AppCAP.cleanResponseString(venueName);

			MyOverlayItem overlayitem = new MyOverlayItem(point, venueName, checkinsSum + checkStr);
			overlayitem.setMapUserData(foursquareIdKey);

			if (myLocationOverlay.getMyLocation()!=null){
				overlayitem.setMyLocationCoords(myLocationOverlay.getMyLocation().getLatitudeE6(), myLocationOverlay.getMyLocation().getLongitudeE6());
			}

			itemizedoverlay.addOverlay(overlayitem);
			if (itemizedoverlay.size() > 0) {
				mapView.getOverlays().add(itemizedoverlay);
			}
		}
	}


	/**
	 * Create pin on map, with number of checked in/out users
	 * @param point
	 * @param foursquareIdKey
	 * @param hereNowCount
	 */
	private void createPin(GeoPoint point, String foursquareIdKey, int hereNowCount) {
		Log.d("LOG", "createPin");

		if (foursquareIdKey!=null){

			MyOverlayPin overlayitem = new MyOverlayPin(ActivityMap.this, point, MyOverlayPin.TYPE_RED_PIN, hereNowCount);

			mapView.getOverlays().add(overlayitem);
		}
	}

	// We have user data from logged user, use it now...
	public void useUserData(){
		AppCAP.setLoggedInUserId(loggedUser.getUserId());
		AppCAP.setLoggedInUserNickname(loggedUser.getNickName());
		textNickName.setText(loggedUser.getNickName());
	}


	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			//int lat = (int) (location.getLatitude() * 1E6);
			//int lng = (int) (location.getLongitude() * 1E6);
			//GeoPoint point = new GeoPoint(lat, lng);

			//Log.d("LOG", "ActivityMap locationChanged: " + location.getLatitude()+":"+location.getLongitude());
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


	public void onClickMenu (View v){

		if (pager.getCurrentScreen()==SCREEN_MAP){
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_MAP, true);
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

		refreshMapDataSet(v);
	}


	private void refreshMapDataSet(View v){

		Animation anim = new RotateAnimation(360.0f, 0.0f, v.getWidth()/2, v.getHeight()/2);
		anim.setDuration(1000);
		anim.setRepeatCount(0);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setFillAfter(true);

		((ImageView) findViewById(R.id.imagebutton_map_refresh_progress)).setAnimation(anim);

		// Remove all markers from MapView
		itemizedoverlay.clear();

		for (int i=mapView.getOverlays().size(); i>1; i--){
			mapView.getOverlays().remove(i-1);
		}
		mapView.invalidate();

		RootActivity.log("ActivityMap_mapView.getOverlays().size=" + mapView.getOverlays().size());

		new Thread(new Runnable() {
			@Override
			public void run() {
				resultMapDataSet = AppCAP.getConnection().getCheckedInBoundsOverTime(mapView);
				if (resultMapDataSet.getResponseCode()==AppCAP.HTTP_ERROR){
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				} else {
					handler.sendEmptyMessage(GET_MAP_DATA_SET);
				}
			}
		}).start();

		// For every refresh save Map coordinates
		AppCAP.setUserCoordinates(getSWAndNECoordinatesBounds(mapView));
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode){

		case ACTIVITY_ACCOUNT_SETTINGS:
			if (resultCode==ACCOUNT_CHANGED){
				getUserData();
			}
			break;
		}
	}


	/**
	 * [0]sw_lat; [1]sw_lng; [2]ne_lat; [3]ne_lng;
	 * @param map
	 * @return
	 */
	private double[] getSWAndNECoordinatesBounds (MapView map){
		double[] data = new double[6];

		GeoPoint pointCenterMap = mapView.getMapCenter();
		int lngSpan = mapView.getLongitudeSpan();
		int latSpan = mapView.getLatitudeSpan();

		GeoPoint sw = new GeoPoint(pointCenterMap.getLatitudeE6() - latSpan/2, pointCenterMap.getLongitudeE6() - lngSpan/2);
		GeoPoint ne = new GeoPoint(pointCenterMap.getLatitudeE6() + latSpan/2, pointCenterMap.getLongitudeE6() + lngSpan/2);

		data[0] = sw.getLatitudeE6() / 1E6; // sw_lat
		data[1] = sw.getLongitudeE6()/ 1E6; // sw_lng
		data[2] = ne.getLatitudeE6() / 1E6; // ne_lat
		data[3] = ne.getLongitudeE6()/ 1E6; // ne_lng
		data[4] = 0;
		data[5] = 0;

		if (myLocationOverlay.getMyLocation()!=null){
			data[4] = myLocationOverlay.getMyLocation().getLatitudeE6() / 1E6;
			data[5] = myLocationOverlay.getMyLocation().getLongitudeE6() / 1E6;
		}
		return data;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	@Override
	protected void onPause() {
		super.onPause();
		capDao.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myLocationOverlay.disableMyLocation();
	}

	@Override
	public void onClickEnterInviteCode(View v) {
		menu.onClickEnterInviteCode(v);
	}

	@Override
	public void onClickMap(View v) {
		//menu.onClickMap(v);
	}

	@Override
	public void onClickPlaces(View v) {
		menu.onClickPlaces(v);
	}

	@Override
	public void onClickPeople(View v) {
		menu.onClickPeople(v);
	}

	@Override
	public void onClickContacts(View v) {
		menu.onClickContacts(v);
	}

	@Override
	public void onClickSettings(View v) {
		menu.onClickSettings(v);		
	}

	@Override
	public void onClickCheckIn(View v) {
		menu.onClickCheckIn(v);
	}

	public void onClickWallet (View v){
		menu.onClickWallet(v);
	}


	public void onClickLogout (View v){
		menu.onClickLogout(v);
	}


	@Override
	protected void onStart() {
		super.onStart();
		UAirship.shared().getAnalytics().activityStarted(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		UAirship.shared().getAnalytics().activityStopped(this);
	}



}