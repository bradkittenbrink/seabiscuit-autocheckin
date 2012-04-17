package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coffeandpower.db.CAPDao;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.adapters.MyFavouritePlacesAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.cont.Review;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.maps.MyItemizedOverlay2;
import com.coffeeandpower.utils.HttpUtil;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ActivityUserDetails extends MapActivity{

	private static final int HANDLE_GET_USER_RESUME = 1222; 
	private static final int HANDLE_LOAD_PROFILE_PICTURE = 1223; 

	private MapUserData mud;

	// Map items
	private MapView mapView;
	private MapController mapController;
	private MyItemizedOverlay2 itemizedoverlay;

	// Views
	private ProgressDialog progress;
	private ProgressBar progressPhoto;

	private ImageView imageProfile;

	private ListView favPlacesList;

	private CAPDao capDao;

	// User Data
	private DataHolder resultGetUserResume;
	private DataHolder resultGetProfilePhoto;

	// User Resume
	private UserResume userResumeData;

	private ArrayList<Venue> favouriteVenues;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what){

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityUserDetails.this, "Error", "Internet connection error").show();
				break;

			case HANDLE_GET_USER_RESUME:
				if (resultGetUserResume!=null){
					if (resultGetUserResume.getObject()!=null){
						if (resultGetUserResume.getObject() instanceof ArrayList<?>){

							ArrayList<Object> tempArray =  (ArrayList<Object>) resultGetUserResume.getObject();

							if (tempArray!=null){
								if (!tempArray.isEmpty()){

									if (tempArray.get(0) instanceof UserResume){
										userResumeData = (UserResume) tempArray.get(0);
									}

									if (tempArray.size()>1){
										if (tempArray.get(1) instanceof ArrayList<?>){
											if (tempArray.get(1)!=null){
												favouriteVenues = (ArrayList<Venue>) tempArray.get(1);
											}
										}
									}

									updateUserDataInUI();
								}
							}
						}
					}
				}
				break;

			case HANDLE_LOAD_PROFILE_PICTURE:
				progressPhoto.setVisibility(View.GONE);
				if (resultGetProfilePhoto!=null){
					if (resultGetProfilePhoto.getObject()!=null){
						if (resultGetProfilePhoto.getObject() instanceof Bitmap){

							imageProfile.setImageBitmap((Bitmap) resultGetProfilePhoto.getObject());
						}
					}
				}
				break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_user_details);

		// Views
		favPlacesList = (ListView) findViewById(R.id.listview_favorite_places);

		mapView = (MapView) findViewById(R.id.mapview_user_details);
		progressPhoto = (ProgressBar) findViewById(R.id.progressbar_photo);
		imageProfile = (ImageView) findViewById(R.id.imagebutton_user_face);
		Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker_iphone);
		itemizedoverlay = new MyItemizedOverlay2(drawable);
		progress = new ProgressDialog(this);
		progress.setMessage("Loading");


		// Views state
		progressPhoto.setVisibility(View.GONE);

		// Database controler
		capDao = new CAPDao(this);
		capDao.open();


		// Get data from intent 
		Bundle extras = getIntent().getExtras();
		if (extras!=null){

			String foursquareId = extras.getString("mapuserdata");
			String fromAct = extras.getString("from_act");

			if (fromAct!=null){

				if (fromAct.equals("map")){
					// From Map

					ArrayList<MapUserData> tempArray = capDao.getMapsUsersData(foursquareId);
					capDao.close();

					if (!tempArray.isEmpty()){
						mud = tempArray.get(0);
					}
				} else {

					// From list
					mud = (MapUserData) extras.getSerializable("mapuserobject");
				}
			}
		}


		// Set MapView
		mapView.setClickable(false);
		mapView.setEnabled(false);
		mapController = mapView.getController();
		mapController.setZoom(18);


		// Navigate map to location from intent data
		if (mud!=null){
			GeoPoint point = new GeoPoint((int)(mud.getLat()*1E6), (int)(mud.getLng()*1E6));
			mapController.animateTo(point);
			createMarker(point);
		}


		// Set Views states
		if (mud!=null){

			((CustomFontView) findViewById(R.id.textview_user_name)).setText(mud.getNickName());
			((TextView)findViewById(R.id.textview_user_status)).setText(AppCAP.cleanResponseString(mud.getStatusText()));
			((CustomFontView) findViewById(R.id.textview_nick_name)).setText(mud.getNickName());
		}


		// Load user resume data
		if (mud!=null){

			// Maybe add some progress
			new Thread(new Runnable() {
				@Override
				public void run() {
					resultGetUserResume = AppCAP.getConnection().getResumeForUserId(mud.getUserId());
					handler.sendEmptyMessage(HANDLE_GET_USER_RESUME);
				}
			}).start();
		}
	}

	/**
	 * Update users data in UI, from favouriteVenues and userResumeData
	 */
	private void updateUserDataInUI(){

		if (userResumeData!=null){
			loadProfilePicture();

			((TextView)findViewById(R.id.textview_date)).setText(userResumeData.getJoined());
			((TextView)findViewById(R.id.textview_earned)).setText("$" + userResumeData.getTotalEarned());
			((TextView)findViewById(R.id.textview_love)).setText(userResumeData.getReviewsLoveReceived());

			((TextView)findViewById(R.id.textview_place)).setText(AppCAP.cleanResponseString(userResumeData.getVenueName()));
			((TextView)findViewById(R.id.textview_street)).setText(AppCAP.cleanResponseString(userResumeData.getVenueAddress()));

			
			if(amIHereNow(userResumeData)){
				((CustomFontView)findViewById(R.id.box_title)).setText("Checked in ...");
				((LinearLayout)findViewById(R.id.layout_available)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.textview_minutes)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.textview_minutes)).setText(getAvailableMins(userResumeData));
				
				if (userResumeData.getUsersHere()>1){
					((LinearLayout)findViewById(R.id.layout_others_at_venue)).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.textview_others_here_now)).setText( 
							userResumeData.getUsersHere() == 2 ? "1 other here now" : (userResumeData.getUsersHere()-1) +  " others here now");
				}
				
			} else {
				((CustomFontView)findViewById(R.id.box_title)).setText("Was checked in ...");

				if (userResumeData.getUsersHere()>0){
					((LinearLayout)findViewById(R.id.layout_others_at_venue)).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.textview_others_here_now)).setText( 
							userResumeData.getUsersHere() == 1 ? "1 other here now" : userResumeData.getUsersHere() +  " others here now");
				}
			}
			
			// Check if user has Reviews
			if (userResumeData.getReviewsTotal()>0){
				
				((LinearLayout)findViewById(R.id.layout_reviews)).setVisibility(View.VISIBLE);
				
				for (Review review:userResumeData.getReviews()){
					
					// Check if is it love review
					if (review.getIsLove().equals("1")){
						((LinearLayout)findViewById(R.id.layout_love_review)).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.textview_review_love)).setText(" from " + review.getAuthor() + ": \"" + review.getReview() + "\"");
					}
				}
			}

		}

		if (favouriteVenues!=null){

			MyFavouritePlacesAdapter adapter = new MyFavouritePlacesAdapter(this, favouriteVenues);
			favPlacesList.setAdapter(adapter);
		}
	}

	/**
	 * Load profile picture for selected user
	 */
	private void loadProfilePicture(){

		progressPhoto.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultGetProfilePhoto = HttpUtil.getBitmapFromURL(userResumeData.getUrlPhoto());
				handler.sendEmptyMessage(HANDLE_LOAD_PROFILE_PICTURE);
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

	private boolean animFlag = true;

	public void onClickPlus (View v) {

		startButtonsAnim(v, animFlag);
		animFlag = !animFlag;
	}


	private void startButtonsAnim (View v, boolean isPlus) {

		if (isPlus){

			((ImageButton)findViewById(R.id.imagebutton_paid)).setVisibility(View.VISIBLE);
			((ImageButton)findViewById(R.id.imagebutton_chat)).setVisibility(View.VISIBLE);
			((ImageButton)findViewById(R.id.imagebutton_f2f)).setVisibility(View.VISIBLE);

			// Plus 
			Animation anim = new RotateAnimation(360.0f, 0.0f, v.getWidth()/2, v.getHeight()/2);
			anim.setDuration(700);
			anim.setRepeatCount(0);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setFillAfter(true);
			v.setAnimation(anim);
			v.setBackgroundResource(R.drawable.go_menu_button_minus);


			// Paid
			Animation animT = new TranslateAnimation(0, 0, 0, -80);
			animT.setDuration(500);
			animT.setFillAfter(true);
			((ImageButton)findViewById(R.id.imagebutton_paid)).startAnimation(animT);

			// Chat
			Animation animT1 = new TranslateAnimation(0, 0, 0, -160);
			animT1.setDuration(500);
			animT1.setFillAfter(true);
			((ImageButton)findViewById(R.id.imagebutton_chat)).startAnimation(animT1);

			// f2f
			Animation animT2 = new TranslateAnimation(0, 0, 0, -240);
			animT2.setDuration(500);
			animT2.setFillAfter(true);
			((ImageButton)findViewById(R.id.imagebutton_f2f)).startAnimation(animT2);

		} else {

			((ImageButton)findViewById(R.id.imagebutton_paid)).setVisibility(View.GONE);
			((ImageButton)findViewById(R.id.imagebutton_chat)).setVisibility(View.GONE);
			((ImageButton)findViewById(R.id.imagebutton_f2f)).setVisibility(View.GONE);

			// Plus 
			Animation anim = new RotateAnimation(0.0f, 360.0f, v.getWidth()/2, v.getHeight()/2);
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
	 * @param ur
	 * @return
	 */
	private String getAvailableMins (UserResume ur){
		Calendar checkoutCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long checkout = Long.parseLong(ur.getCheckOut());
		int mins = (int) ((checkout - (checkoutCal.getTimeInMillis()/1000))/60);
		return mins == 1 ? mins + " min" : mins + " mins";
	}


	/**
	 * Check if I am checked in here
	 */
	private boolean amIHereNow(UserResume ur){
		Calendar checkoutCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long checkout = Long.parseLong(ur.getCheckOut());

		return checkout > checkoutCal.getTimeInMillis()/1000;
	}

	public void onClickChat (View v){

	}


	public void onClickPaid (View v){

	}


	public void onClickF2F (View v){

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
