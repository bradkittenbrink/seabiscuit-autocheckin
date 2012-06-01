package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserShort;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CounterData;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.maps.MyItemizedOverlay2;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.CustomSeek;
import com.coffeeandpower.views.CustomSeek.HoursChangeListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.urbanairship.UAirship;

public class ActivityCheckIn extends RootActivity implements Observer {

	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyItemizedOverlay2 itemizedoverlay;

	private Venue venue;

	// Views
	private CustomFontView textHours;
	private CustomFontView textTitle;
	private CustomFontView textName;
	private CustomFontView textStreet;
	private CustomSeek hoursSeek;

	private RelativeLayout layoutCheckedInUsers;
	private LinearLayout layoutForInflate;
	private LinearLayout layoutPopUp;

	private EditText statusEditText;

	private int checkInDuration;

	private DataHolder result;

	private Executor exe;
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler taskHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			/*
			if (type.equals("people")) {
				// pass message data along to venue update method
				ArrayList<UserSmart> usersArray = msg.getData().getParcelableArrayList("users");
				updateUsersAndCheckinsFromApiResult(usersArray);
			}
			else
			{
				// pass message data along to venue update method
				ArrayList<VenueSmart> venueArray = msg.getData().getParcelableArrayList("venues");
				updateVenuesAndCheckinsFromApiResult(venueArray);
			}
			*/
			super.handleMessage(msg);
		}
	};

	private ArrayList<UserShort> checkedInUsers;

	{
		checkInDuration = 1; // default 1 hour checkin duration, slider
				     // sets
				     // other values
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_check_in);

		// Executor
		exe = new Executor(ActivityCheckIn.this);
		exe.setExecutorListener(new ExecutorInterface() {
			@Override
			public void onErrorReceived() {
				errorReceived();
			}

			@Override
			public void onActionFinished(int action) {
				actionFinished(action);
			}
		});

		// Get Data from Intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
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
		layoutCheckedInUsers = (RelativeLayout) findViewById(R.id.layout_name);
		layoutForInflate = (LinearLayout) findViewById(R.id.inflate_users);
		layoutPopUp = (LinearLayout) findViewById(R.id.layout_popup_info);
		mapView = (MapView) findViewById(R.id.imageview_mapview);
		Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker_iphone);
		itemizedoverlay = new MyItemizedOverlay2(drawable);

		// Views states
		textTitle.setText(venue.getName());
		textStreet.setText(venue.getAddress());
		textName.setText(venue.getName());
		layoutCheckedInUsers.setVisibility(View.GONE);
		layoutPopUp.setVisibility(View.GONE);

		// Set others
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);

		// Navigate map to location from intent data
		GeoPoint point = new GeoPoint((int) (venue.getLat() * 1E6), (int) (venue.getLng() * 1E6));
		GeoPoint pointForCenter = new GeoPoint(point.getLatitudeE6() + Utils.getScreenDependentItemSize(Utils.MAP_VER_OFFSET_FROM_CENTER),
				point.getLongitudeE6() - Utils.getScreenDependentItemSize(Utils.MAP_HOR_OFFSET_FROM_CENTER));
		mapController.animateTo(pointForCenter);
		createMarker(point);

		// Listener for Hours change on SeekBar
		hoursSeek.setOnHoursChangeListener(new HoursChangeListener() {
			@Override
			public void onHoursChange(int hours) {
				switch (hours) {
				case 1:
					textHours.setText(hours + " hour");
					checkInDuration = 1;
					break;

				default:
					textHours.setText(hours + " hours");
					checkInDuration = hours;
					break;
				}
			}
		});

		// Get users checked in venue
		getUsersCheckedIn(venue);
	}
	
	@Override
	protected void onStart() {
		Log.d("CheckIn","ActivityCheckIn.onStart()");
		super.onStart();

		//UAirship.shared().getAnalytics().activityStarted(this);
		AppCAP.getCounter().addObserver(this); // add this object as a Counter observer
		AppCAP.getCounter().getLastResponseReset();
	}

	@Override
	public void onStop() {
		Log.d("CheckIn","ActivityCheckIn.onStop()");
		super.onStop();

		//UAirship.shared().getAnalytics().activityStopped(this);
		AppCAP.getCounter().deleteObserver(this);
	}

	/**
	 * Get checkedin users in venue
	 * 
	 * @param v
	 */
	public void getUsersCheckedIn(Venue v) {
		exe.getUsersCheckedInAtFoursquareID(v.getFoursquareId());
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

	/**
	 * Checkin me in venue
	 * 
	 * @param v
	 */
	public void onClickCheckIn(View v) {
		final int checkInTime = (int) (System.currentTimeMillis() / 1000);
		final int checkOutTime = checkInTime + checkInDuration * 3600;

		exe.checkIn(venue, checkInTime, checkOutTime, statusEditText.getText().toString());
	}

	private void populateUsersIfExist() {
		if (checkedInUsers != null) {
			if (checkedInUsers.size() > 0) {

				layoutCheckedInUsers.setVisibility(View.VISIBLE);
				layoutPopUp.setVisibility(View.VISIBLE);
				ImageLoader imageLoader = new ImageLoader(this);

				// Set text on first view
				((TextView) layoutPopUp.getChildAt(0)).setText(checkedInUsers.get(0).getNickName());
				String status = checkedInUsers.get(0).getStatusText();
				status = status.length() < 1 ? "No status set..." : status;
				((TextView) layoutPopUp.getChildAt(1)).setText(AppCAP.cleanResponseString(status));

				for (int i = 0; i < checkedInUsers.size(); i++) {

					ImageView image = new ImageView(this);
					image.setPadding(10, 10, 10, 10);
					image.setTag(i);
					image.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((TextView) layoutPopUp.getChildAt(0)).setText(checkedInUsers.get((Integer) v.getTag())
									.getNickName());

							String status = checkedInUsers.get((Integer) v.getTag()).getStatusText();
							status = status.length() < 1 ? "No status set..." : status;
							((TextView) layoutPopUp.getChildAt(1)).setText(AppCAP.cleanResponseString(status));
						}
					});

					imageLoader.DisplayImage(checkedInUsers.get(i).getImageURL(), image, R.drawable.default_avatar50, 70);
					layoutForInflate.addView(image);

				}
			}
		}
	}

	private void errorReceived() {

	}

	private void actionFinished(int action) {
		result = exe.getResult();

		switch (action) {

		case Executor.HANDLE_CHECK_IN:
			setResult(AppCAP.ACT_QUIT);
			AppCAP.setUserCheckedIn(true);
			ActivityCheckIn.this.finish();
			break;

		case Executor.HANDLE_GET_CHECHED_USERS_IN_FOURSQUARE:
			if (result.getObject() != null) {
				if (result.getObject() instanceof ArrayList<?>) {
					checkedInUsers = (ArrayList<UserShort>) result.getObject();
					populateUsersIfExist();
				}
			}
			break;
		}
	}

	public void onClickBack(View v) {
		onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		/*
		 * verify that the data is really of type CounterData, and log the
		 * details
		 */
		if (data instanceof CounterData) {
			CounterData counterdata = (CounterData) data;
			DataHolder result = counterdata.value;
						
			Object[] obj = (Object[]) result.getObject();
			@SuppressWarnings("unchecked")
			ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
			Iterator<VenueSmart> itr = arrayVenues.iterator();
			while(itr.hasNext())
			{
				if(this.venue.getVenueId() == itr.next().getVenueId())       
				{
					//Grab the network data that matches the venueId
					VenueSmart currentVenue = itr.next();
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putCharSequence("type", counterdata.type);
					bundle.putParcelable("venue", currentVenue);
					message.setData(bundle);
					
					Log.d("CheckIn","ActivityCheckIn.update: Sending handler message...");
					taskHandler.sendMessage(message);
					break;
				}
				
			}
			
			
		}
		else
			Log.d("CheckIn","Error: Received unexpected data type: " + data.getClass().toString());
	}
	
	

}
