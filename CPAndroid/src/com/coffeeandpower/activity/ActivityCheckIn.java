package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.maps.MyItemizedOverlay2;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.CustomSeek;
import com.coffeeandpower.views.CustomSeek.HoursChangeListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ActivityCheckIn extends RootActivity
{

	private static final int GET_CHECKED_USERS = 8;

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
	private ProgressDialog progress;

	private int checkInDuration;

	private DataHolder resultCheckIn;
	private DataHolder resultGetUsersCheckedIn;

	private ArrayList<UserShort> checkedInUsers;

	{
		checkInDuration = 1; // default 1 hour checkin duration, slider sets
								// other values
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what)
			{

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityCheckIn.this, "Error", "Internet connection error").show();
				break;

			case AppCAP.HTTP_REQUEST_SUCCEEDED:
				setResult(AppCAP.ACT_QUIT);
				AppCAP.setUserCheckedIn(true);
				ActivityCheckIn.this.finish();
				break;

			case GET_CHECKED_USERS:
				if (resultGetUsersCheckedIn.getObject() != null)
				{
					if (resultGetUsersCheckedIn.getObject() instanceof ArrayList<?>)
					{

						checkedInUsers = (ArrayList<UserShort>) resultGetUsersCheckedIn.getObject();
						populateUsersIfExist();
					}
				}
				break;

			}
		}

	};

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_check_in);

		// Get Data from Intent
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			venue = (Venue) extras.getSerializable("venue");
		}
		else
		{
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
		progress = new ProgressDialog(this);
		Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker_iphone);
		itemizedoverlay = new MyItemizedOverlay2(drawable);

		// Views states
		textTitle.setText(venue.getName());
		textStreet.setText(venue.getAddress());
		textName.setText(venue.getName());
		layoutCheckedInUsers.setVisibility(View.GONE);
		layoutPopUp.setVisibility(View.GONE);
		progress.setMessage("Checking in...");

		// Set others
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);

		// Navigate map to location from intent data
		GeoPoint point = new GeoPoint((int) (venue.getLat() * 1E6), (int) (venue.getLng() * 1E6));
		mapController.animateTo(point);
		mapView.scrollBy(-getDisplayMetrics().widthPixels / 4, 0);
		createMarker(point);

		// Listener for Hours change on SeekBar
		hoursSeek.setOnHoursChangeListener(new HoursChangeListener()
		{
			@Override
			public void onHoursChange(int hours)
			{
				switch (hours)
				{
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

	/**
	 * Get checkedin users in venue
	 * 
	 * @param v
	 */
	public void getUsersCheckedIn(Venue v)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				resultGetUsersCheckedIn = AppCAP.getConnection().getUsersCheckedInAtFoursquareID(venue.getId());
				if (resultGetUsersCheckedIn.getResponseCode() == AppCAP.HTTP_ERROR)
				{
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				}
				else
				{
					handler.sendEmptyMessage(GET_CHECKED_USERS);
				}
			}
		}).start();
	}

	private void createMarker(GeoPoint point)
	{
		OverlayItem overlayitem = new OverlayItem(point, "", "");
		itemizedoverlay.addOverlay(overlayitem);
		if (itemizedoverlay.size() > 0)
		{
			mapView.getOverlays().add(itemizedoverlay);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	/**
	 * Checkin me in venue
	 * 
	 * @param v
	 */
	public void onClickCheckIn(View v)
	{
		final int checkInTime = (int) (System.currentTimeMillis() / 1000);
		final int checkOutTime = checkInTime + checkInDuration * 3600;

		progress.show();

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				resultCheckIn = AppCAP.getConnection().checkIn(venue, checkInTime, checkOutTime,
						statusEditText.getText().toString());
				if (resultCheckIn.getResponseCode() == AppCAP.HTTP_ERROR)
				{
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				}
				else
				{
					handler.sendEmptyMessage(resultCheckIn.getResponseCode());
				}
			}
		}).start();
	}

	private void populateUsersIfExist()
	{
		if (checkedInUsers != null)
		{
			if (checkedInUsers.size() > 0)
			{

				layoutCheckedInUsers.setVisibility(View.VISIBLE);
				layoutPopUp.setVisibility(View.VISIBLE);
				ImageLoader imageLoader = new ImageLoader(this);

				// Set text on first view
				((TextView) layoutPopUp.getChildAt(0)).setText(checkedInUsers.get(0).getNickName());
				String status = checkedInUsers.get(0).getStatusText();
				status = status.length() < 1 ? "No status set..." : status;
				((TextView) layoutPopUp.getChildAt(1)).setText(AppCAP.cleanResponseString(status));

				for (int i = 0; i < checkedInUsers.size(); i++)
				{

					ImageView image = new ImageView(this);
					image.setPadding(10, 10, 10, 10);
					image.setTag(i);
					image.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							((TextView) layoutPopUp.getChildAt(0)).setText(checkedInUsers.get((Integer) v.getTag())
									.getNickName());

							String status = checkedInUsers.get((Integer) v.getTag()).getStatusText();
							status = status.length() < 1 ? "No status set..." : status;
							((TextView) layoutPopUp.getChildAt(1)).setText(AppCAP.cleanResponseString(status));
						}
					});

					imageLoader.DisplayImage(checkedInUsers.get(i).getImageURL(), image, R.drawable.default_avatar50);
					layoutForInflate.addView(image);

				}
			}
		}
	}

	public void onClickBack(View v)
	{
		onBackPressed();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

}
