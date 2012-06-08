package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyFavouritePlacesAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Education;
import com.coffeeandpower.cont.Listing;
import com.coffeeandpower.cont.Review;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.cont.Work;
import com.coffeeandpower.cont.VenueSmart.CheckinData;
import com.coffeeandpower.datatiming.CounterData;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.maps.MyItemizedOverlay2;
import com.coffeeandpower.maps.PinBlackDrawable;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.urbanairship.UAirship;

public class ActivityUserDetails extends RootActivity implements Observer{

	private static final int DIALOG_SEND_PROP = 0;
	private static final int DIALOG_SEND_F2F_INVITE = 1;

	private UserSmart mud;

	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyItemizedOverlay2 itemizedoverlay;

	private ImageView imageProfile;

	private ListView favPlacesList;

	private DataHolder result;
	private ImageLoader imageLoader;

	private Executor exe;

	private UserResume userResumeData;

	private ArrayList<Venue> favoritePlaces;
	private ArrayList<VenueSmart> arraySmartVenues;
	
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler taskHandler = new Handler() {

		// handleMessage - on the main thread
		@Override
		public void handleMessage(Message msg) {
				// pass message data along to venue update method
			arraySmartVenues = msg.getData().getParcelableArrayList("venues");
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_user_details);

		// Executor
		exe = new Executor(ActivityUserDetails.this);
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

		// Image Loader
		imageLoader = new ImageLoader(ActivityUserDetails.this);

		// Views
		favPlacesList = (ListView) findViewById(R.id.listview_favorite_places);
		favPlacesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				Intent intent = new Intent(ActivityUserDetails.this, ActivityPlaceDetails.class);
				Venue currVenue = favoritePlaces.get(position);
				//intent.putExtra("foursquare_id", currVenue);
				//FIXME
				//We need to eliminate the Venue class eventually
				ArrayList<CheckinData> arrayCheckins = new ArrayList<VenueSmart.CheckinData>();
				VenueSmart currSmartVenue = new VenueSmart(currVenue.getVenueId(), currVenue.getName(), currVenue.getAddress(), currVenue.getCity(), currVenue.getCity(), currVenue.getDistance(), currVenue.getFoursquareId(), currVenue.getCheckinsCount(),
						0, 0, currVenue.getPhotoUrl(), currVenue.getPhone(), currVenue.getPhone(), currVenue.getLat(), currVenue.getLng(),
						arrayCheckins);
				intent.putExtra("venueSmart", currSmartVenue);
				if(arraySmartVenues!=null)
				{
					boolean venueFound = false;
					for(VenueSmart testSmartVenue:arraySmartVenues)
					{
						if(testSmartVenue.getVenueId() == currVenue.getVenueId())
						{
							intent.putExtra("venueSmart", testSmartVenue);
							venueFound = true;
							break;
						}
					}
					//FIXME
					//If we don't find the venue we need to pull it from http
				}
				intent.putExtra("coords", AppCAP.getUserCoordinates());
				startActivity(intent);
			}
		});

		mapView = (MapView) findViewById(R.id.mapview_user_details);
		imageProfile = (ImageView) findViewById(R.id.imagebutton_user_face);

		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			//Not used so being removed
			String foursquareId = extras.getString("mapuserdata");
			String fromAct = extras.getString("from_act");

			if (fromAct != null) {
				if (fromAct.equals("list")) {
					// From list
					mud = (UserSmart) extras.getParcelable("mapuserobject");
				}
			}
		}

		// Set MapView
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);

		// Navigate map to location from intent data
		if (mud != null) {
			GeoPoint point = new GeoPoint((int) (mud.getLat() * 1E6), (int) (mud.getLng() * 1E6));
			GeoPoint pointForCenter = new GeoPoint(point.getLatitudeE6()
					+ Utils.getScreenDependentItemSize(Utils.MAP_VER_OFFSET_FROM_CENTER), point.getLongitudeE6()
					- Utils.getScreenDependentItemSize(Utils.MAP_HOR_OFFSET_FROM_CENTER));
			mapController.animateTo(pointForCenter);

			itemizedoverlay = new MyItemizedOverlay2(getPinDrawable(
					RootActivity.getDistanceBetween(AppCAP.getUserCoordinates()[4], AppCAP.getUserCoordinates()[5], mud.getLat(),
							mud.getLng()) + " away", point));
			createMarker(point);
		}

		// Set Views states
		if (mud != null) {
			((CustomFontView) findViewById(R.id.textview_user_name)).setText(mud.getNickName());
			((TextView) findViewById(R.id.textview_user_status))
					.setText(AppCAP.cleanResponseString(mud.getStatusText()) != null ? (AppCAP.cleanResponseString(
							mud.getStatusText()).equals("") ? "" : "\"" + AppCAP.cleanResponseString(mud.getStatusText())
							+ "\"") : "");
			((CustomFontView) findViewById(R.id.textview_nick_name)).setText(mud.getNickName());

			// If current user looking at own page, hide "plus"
			// button
			if (mud.getUserId() == AppCAP.getLoggedInUserId()) {
				((ImageButton) findViewById(R.id.imagebutton_plus)).setVisibility(View.GONE);
				((RelativeLayout) findViewById(R.id.rel_buttons)).setVisibility(View.GONE);
			}

		}

		// Load user resume data
		if (mud != null) {
			exe.getResumeForUserId(mud.getUserId());
		}

	}  //end onCreate()
	
	@Override
	protected void onStart() {
		Log.d("UserDetails","ActivityUserDetails.onStart()");
		super.onStart();
		//initialLoad = true;
		UAirship.shared().getAnalytics().activityStarted(this);
		
		AppCAP.getCounter().getCachedDataForAPICall("venuesWithCheckins",this);	
	}

	@Override
	public void onStop() {
		Log.d("UserDetails","ActivityUserDetails.onStop()");
		super.onStop();
		UAirship.shared().getAnalytics().activityStopped(this);

		AppCAP.getCounter().stoppedObservingAPICall("venuesWithCheckins",this);
	}
	

	private Drawable getPinDrawable(String text, GeoPoint gp) {
		PinBlackDrawable icon = new PinBlackDrawable(this, text);
		icon.setBounds(0, -icon.getIntrinsicHeight(), icon.getIntrinsicWidth(), 0);
		return icon;
	}

	/**
	 * Update users data in UI, from favoritePlaces and userResumeData
	 */
	private void updateUserDataInUI() {
		if (userResumeData != null) {
			// Load profile picture
			imageLoader.DisplayImage(userResumeData.getUrlPhoto(), imageProfile, R.drawable.default_avatar50, 70);

			((TextView) findViewById(R.id.textview_user_job_title)).setText(AppCAP.cleanResponseString(userResumeData.getJobTitle()));
			((TextView) findViewById(R.id.textview_date)).setText(userResumeData.getJoined());

			// Was EARNED now it's HOURS
			((TextView) findViewById(R.id.textview_earned)).setText(userResumeData.getTotalHours()+"");
			((TextView) findViewById(R.id.textview_love)).setText(userResumeData.getReviewsLoveReceived());
			((TextView) findViewById(R.id.textview_rate)).setText(userResumeData.getHourlyBillingRate().matches("null") ? "N/A"
					: userResumeData.getHourlyBillingRate());

			((TextView) findViewById(R.id.textview_place)).setText(AppCAP.cleanResponseString(userResumeData.getCheckInData_Name()));
			((TextView) findViewById(R.id.textview_street)).setText(AppCAP.cleanResponseString(userResumeData.getCheckInData_Address()));

			if (isUserHereNow(userResumeData)) {
				((CustomFontView) findViewById(R.id.box_title)).setText("Checked in ...");
				((LinearLayout) findViewById(R.id.layout_available)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.textview_minutes)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.textview_minutes)).setText(getAvailableMins(userResumeData));

				if (userResumeData.getCheckInData_usersHere() > 1) {
					((LinearLayout) findViewById(R.id.layout_others_at_venue)).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.textview_others_here_now))
							.setText(userResumeData.getCheckInData_usersHere() == 2 ? "1 other here now"
									: (userResumeData.getCheckInData_usersHere() - 1) + " others here now");
				}

			} else {
				((CustomFontView) findViewById(R.id.box_title)).setText("Was checked in ...");

				if (userResumeData.getCheckInData_usersHere() > 0) {
					((LinearLayout) findViewById(R.id.layout_others_at_venue)).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.textview_others_here_now))
							.setText(userResumeData.getCheckInData_usersHere() == 1 ? "1 other here now" : userResumeData
									.getCheckInData_usersHere() + " others here now");
				}
			}

			// Check for Summary info
			if (userResumeData.getBio() != null && !userResumeData.getBio().contains("null") && !userResumeData.getBio().equals("")) {
				((TextView) findViewById(R.id.text_summary)).setText(AppCAP.cleanResponseString(userResumeData.getBio()));
				((TextView) findViewById(R.id.text_summary_title)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.text_summary)).setVisibility(View.VISIBLE);
			}

			// Chech if user is verified for LinkedIn and Facebook
			if (userResumeData.getVerifiedLinkedIn().matches("1")) {
				((LinearLayout) findViewById(R.id.layout_verified_linked_in)).setVisibility(View.VISIBLE);
			}
			if (userResumeData.getVerifiedFacebook().matches("1")) {
				((LinearLayout) findViewById(R.id.layout_verified_facebook)).setVisibility(View.VISIBLE);
			}

			// Check if user have Education Data
			if (!userResumeData.getEducation().isEmpty()) {
				((LinearLayout) findViewById(R.id.layout_edu_review)).setVisibility(View.VISIBLE);

				for (Education edu : userResumeData.getEducation()) {
					LayoutInflater inflater = getLayoutInflater();
					View view = inflater.inflate(R.layout.review_education, null);

					String school = edu.getSchool().contains("null") ? "" : edu.getSchool();
					String startDate = (edu.getStartDate() + "").contains("null") ? "" : edu.getStartDate() + "";
					String endDate = (edu.getEndDate() + "").contains("null") ? "" : edu.getEndDate() + "";
					String degree = edu.getDegree().contains("null") ? "" : edu.getDegree();
					String concentration = edu.getConcentrations().contains("null") ? "" : edu.getConcentrations();

					((TextView) view.findViewById(R.id.textview_review_edu)).setText(school + " " + startDate + "-" + endDate);
					((TextView) view.findViewById(R.id.textview_review_degree)).setText(degree);
					((TextView) view.findViewById(R.id.textview_review_concentrations)).setText(concentration);
					((LinearLayout) findViewById(R.id.edu_inflate)).addView(view);
				}
			}

			// Check if user have Work Data
			if (!userResumeData.getWork().isEmpty()) {
				((LinearLayout) findViewById(R.id.layout_work_review)).setVisibility(View.VISIBLE);

				for (Work work : userResumeData.getWork()) {
					LayoutInflater inflater = getLayoutInflater();
					View view = inflater.inflate(R.layout.review_work, null);

					String title = work.getTitle().contains("null") ? "" : work.getTitle();
					String startDate = work.getStartDate().contains("null") ? "" : work.getStartDate() + "";
					String endDate = work.getEndDate().contains("null") ? "" : work.getEndDate() + "";
					String company = work.getCompany().contains("null") ? "" : work.getCompany();

					if (!title.equals(""))
						((TextView) view.findViewById(R.id.textview_job_title)).setText(title + " at " + company);

					((TextView) view.findViewById(R.id.textview_job_date)).setText("(" + startDate + " - " + endDate + ")");
					((LinearLayout) findViewById(R.id.work_inflate)).addView(view);
				}
			}

			// Check if user has Reviews
			if (userResumeData.getReviewsTotal() > 0) {
				((LinearLayout) findViewById(R.id.layout_reviews)).setVisibility(View.VISIBLE);

				// Check if we have love review
				if (!userResumeData.getReviewsLoveReceived().equals("0")) {
					((LinearLayout) findViewById(R.id.love_inflate)).setVisibility(View.VISIBLE);
				}

				// Find all love reviews
				for (Review review : userResumeData.getReviews()) {
					if (review.getIsLove().equals("1")) {
						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.review_props, null);

						((TextView) v.findViewById(R.id.textview_review_love)).setText("from " + review.getAuthor() + ": \""
								+ AppCAP.cleanResponseString(review.getReview()) + "\"");
						((LinearLayout) findViewById(R.id.love_inflate)).addView(v);
					}
				}

				// Find other reviews
				for (Review review : userResumeData.getReviews()) {
					if (!review.getIsLove().equals("1")) {
						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.review_props, null);

						((TextView) v.findViewById(R.id.textview_review_love)).setText(AppCAP.cleanResponseString(review
								.getReview()) + (review.getSkill().contains("null") ? "" : "\n" + review.getSkill()));
						((LinearLayout) findViewById(R.id.love_inflate)).addView(v);
					}
				}
			}

			// Check Listings as Agent
			if (!userResumeData.getAgentListings().isEmpty()) {
				((LinearLayout) findViewById(R.id.layout_listings_agent)).setVisibility(View.VISIBLE);

				for (Listing l : userResumeData.getAgentListings()) {
					LayoutInflater inflater = getLayoutInflater();
					View v = inflater.inflate(R.layout.review_listing, null);
					((TextView) v.findViewById(R.id.textview_listing)).setText(AppCAP.cleanResponseString(l.getListing()));
					((TextView) v.findViewById(R.id.text_price)).setText("$" + l.getPrice());

					((LinearLayout) findViewById(R.id.agent_inflate)).addView(v);
				}
			}

			// Check Listings as Client
			if (!userResumeData.getClienListings().isEmpty()) {
				((LinearLayout) findViewById(R.id.layout_listings_client)).setVisibility(View.VISIBLE);

				for (Listing l : userResumeData.getClienListings()) {
					LayoutInflater inflater = getLayoutInflater();
					View v = inflater.inflate(R.layout.review_listing, null);
					((TextView) v.findViewById(R.id.textview_listing)).setText(AppCAP.cleanResponseString(l.getListing()));
					((TextView) v.findViewById(R.id.text_price)).setText("$" + l.getPrice());

					((LinearLayout) findViewById(R.id.client_inflate)).addView(v);
				}
			}

		}

		// List view with venues
		if (favoritePlaces != null) {
			MyFavouritePlacesAdapter adapter = new MyFavouritePlacesAdapter(this, favoritePlaces);
			favPlacesList.setAdapter(adapter);
			favPlacesList.postDelayed(new Runnable() {
				@Override
				public void run() {
					Utils.setListViewHeightBasedOnChildren(favPlacesList);
				}
			}, 400);

		}

		// Scroll to the top of the page
		((ScrollView) findViewById(R.id.scroll)).post(new Runnable() {
			@Override
			public void run() {
				((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_UP);
			}
		});
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

	private boolean animFlag = true;

	public void onClickPlus(View v) {
		startButtonsAnim(v, animFlag);
		animFlag = !animFlag;
	}

	private void animateView(RelativeLayout v) {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(400);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				3.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(300);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.0f);
		v.setLayoutAnimation(controller);
	}

	private void startButtonsAnim(View v, boolean isPlus) {
		if (isPlus) {
			((RelativeLayout) findViewById(R.id.rel_buttons)).setVisibility(View.VISIBLE);
			animateView((RelativeLayout) findViewById(R.id.rel_buttons));

			// Plus
			Animation anim = new RotateAnimation(360.0f, 0.0f, v.getWidth() / 2, v.getHeight() / 2);
			anim.setDuration(700);
			anim.setRepeatCount(0);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setFillAfter(true);
			v.setAnimation(anim);
			v.setBackgroundResource(R.drawable.go_menu_button_minus);

		} else {
			((RelativeLayout) findViewById(R.id.rel_buttons)).setVisibility(View.GONE);

			// Plus
			Animation anim = new RotateAnimation(0.0f, 360.0f, v.getWidth() / 2, v.getHeight() / 2);
			anim.setDuration(700);
			anim.setRepeatCount(0);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setFillAfter(true);
			v.setAnimation(anim);
			v.setBackgroundResource(R.drawable.go_button_iphone);

		}
	}

	/**
	 * Get String with number of available minutes od checked in user
	 * 
	 * @param ur
	 * @return
	 */
	private String getAvailableMins(UserResume ur) {
		Calendar checkoutCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long checkout = Long.parseLong(ur.getCheckInData_checkOut());
		int mins = (int) ((checkout - (checkoutCal.getTimeInMillis() / 1000)) / 60);

		if (mins <= 60)
			return mins == 1 ? mins + " min" : mins + " mins";
		else
			return ((mins / 60) == 1 ? "1 hour " : (mins / 60) + " hours ") + ((mins % 60) == 1 ? "1 min" : (mins % 60) + " mins");

	}

	/**
	 * Check if I am checked in here
	 */
	public static boolean isUserHereNow(UserResume ur) {
		Calendar checkoutCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long checkout = Long.parseLong(ur.getCheckInData_checkOut());

		return checkout > checkoutCal.getTimeInMillis() / 1000;
	}

	public void onClickChat(View v) {
		startActivity(new Intent(ActivityUserDetails.this, ActivityChat.class).putExtra("user_id", userResumeData.getCheckInData_userId())
				.putExtra("nick_name", userResumeData.getNickName()));
	}

	public void onClickPaid(View v) {

	}

	public void onClickSendContact(View v) {
		showDialog(DIALOG_SEND_F2F_INVITE);
	}

	public void onClickSendProp(View v) {
		showDialog(DIALOG_SEND_PROP);
	}

	public void onClickBack(View v) {
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

	@Override
	protected Dialog onCreateDialog(int id) {

		final Dialog dialog = new Dialog(ActivityUserDetails.this);

		switch (id) {

		case DIALOG_SEND_PROP:
			dialog.setContentView(R.layout.diloag_send_love);
			((Button) dialog.findViewById(R.id.btn_send)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((EditText) dialog.findViewById(R.id.edit_review)).getText().toString().length() > 0) {
						dialog.dismiss();
						exe.sendReview(userResumeData, ((EditText) dialog.findViewById(R.id.edit_review)).getText()
								.toString());
					} else {
						dialog.dismiss();
						Toast.makeText(ActivityUserDetails.this, "Review can't be empty!", Toast.LENGTH_SHORT).show();
					}
				}
			});

			((Button) dialog.findViewById(R.id.btn_cancel)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;

		case DIALOG_SEND_F2F_INVITE:
			AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUserDetails.this);
			builder.setMessage("Request to exchange contact info?").setCancelable(false)
					.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							exe.sendFriendRequest(mud.getUserId());
							dialog.cancel();
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			return builder.create();
		}

		return dialog;
	}

	public void actionFinished(int action) {
		result = exe.getResult();

		switch (action) {

		case Executor.HANDLE_GET_USER_RESUME:
			if (result != null && result.getObject() != null) {
				if (result.getObject() instanceof UserResume) {
					userResumeData = (UserResume) result.getObject();
					favoritePlaces = userResumeData.getFavoritePlaces();
					updateUserDataInUI();
				}
			}

			break;

		case Executor.HANDLE_SEND_FRIEND_REQUEST:
			String message = result.getResponseCode() == 0 ? "Contact Request Sent."
					: (result.getResponseCode() == 4 ? "We've resent your request.\nThe password is: "
							+ result.getResponseMessage() : (result.getResponseCode() == 6 ? "Request already sent"
							: result.getResponseMessage()));

			Toast.makeText(ActivityUserDetails.this, message, Toast.LENGTH_LONG).show();
			break;
		}
	}

	public void errorReceived() {
	}
	
	
	@Override
	public void update(Observable observable, Object data) {
		/*
		 * verify that the data is really of type CounterData, and log the
		 * details
		 */
		if (data instanceof CounterData) {
			CounterData counterdata = (CounterData) data;
			DataHolder venuesWithCheckins = counterdata.getData();
						
			Object[] obj = (Object[]) venuesWithCheckins.getObject();
			@SuppressWarnings("unchecked")
			ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
			
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("venues", arrayVenues);
			message.setData(bundle);
			
			Log.d("UserDetails","ActivityUserDetails.update: Sending handler message...");
			taskHandler.sendMessage(message);
			
			
		}
		else
			Log.d("UserDetails","Error: Received unexpected data type: " + data.getClass().toString());
	}

}
