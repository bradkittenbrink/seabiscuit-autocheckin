package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coffeandpower.db.CAPDao;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.adapters.MyFavouritePlacesAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Education;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.cont.Review;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.maps.MyItemizedOverlay2;
import com.coffeeandpower.utils.HttpUtil;
import com.coffeeandpower.utils.Utils;
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
	private static final int HANDLE_SENDING_LOVE = 1224;

	private static final int DIALOG_SEND_LOVE = 0;

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

	private DataHolder resultGetUserResume;
	private DataHolder resultGetProfilePhoto;
	private DataHolder resultSendReview;

	// User Resume
	private UserResume userResumeData;

	private ArrayList<Venue> favouriteVenues;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();
			progressPhoto.setVisibility(View.GONE);

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
				if (resultGetProfilePhoto!=null){
					if (resultGetProfilePhoto.getObject()!=null){
						if (resultGetProfilePhoto.getObject() instanceof Bitmap){

							imageProfile.setImageBitmap((Bitmap) resultGetProfilePhoto.getObject());
						}
					}
				}
				break;


			case HANDLE_SENDING_LOVE:
				if (resultSendReview!=null){

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
					if (resultGetUserResume.getResponseCode()==AppCAP.HTTP_ERROR){
						handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
					} else {
						handler.sendEmptyMessage(HANDLE_GET_USER_RESUME);
					}
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


			if(isUserHereNow(userResumeData)){
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


				// Check if we have love review
				if (!userResumeData.getReviewsLoveReceived().equals("0")){
					((LinearLayout)findViewById(R.id.love_inflate)).setVisibility(View.VISIBLE);
				}


				for (Review review:userResumeData.getReviews()){

					// Find all love reviews
					if (review.getIsLove().equals("1")){

						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.item_love_review, null);

						((TextView)v.findViewById(R.id.textview_review_love)).setText(" from " + review.getAuthor() + ": \"" + AppCAP.cleanResponseString(review.getReview()) + "\"");
						((LinearLayout)findViewById(R.id.love_inflate)).addView(v);
					}
				}
			}


			// Check if user have Education Data
			if (!userResumeData.getEducation().isEmpty()){

				((LinearLayout)findViewById(R.id.layout_edu_review)).setVisibility(View.VISIBLE);

				for (Education edu:userResumeData.getEducation()){

					LayoutInflater inflater = getLayoutInflater();
					View view = inflater.inflate(R.layout.item_education_review, null);

					((TextView)view.findViewById(R.id.textview_review_edu)).setText(edu.getSchool() + " " + edu.getStartDate() + "-" + edu.getEndDate());
					((LinearLayout)findViewById(R.id.edu_inflate)).addView(view);
				}
			}

		}

		if (favouriteVenues!=null){

			MyFavouritePlacesAdapter adapter = new MyFavouritePlacesAdapter(this, favouriteVenues);
			favPlacesList.setAdapter(adapter);
			favPlacesList.postDelayed(new Runnable() {
				@Override
				public void run() {
					Utils.setListViewHeightBasedOnChildren(favPlacesList);
				}
			}, 400);

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
				if (resultGetProfilePhoto.getResponseCode()==AppCAP.HTTP_ERROR){
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				} else {
					handler.sendEmptyMessage(HANDLE_LOAD_PROFILE_PICTURE);
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

	private boolean animFlag = true;

	public void onClickPlus (View v) {

		startButtonsAnim(v, animFlag);
		animFlag = !animFlag;
	}


	private void animateView(RelativeLayout v){
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(150);
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
				);
		animation.setDuration(300);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);       
		v.setLayoutAnimation(controller);
	}


	private void startButtonsAnim (View v, boolean isPlus) {

		if (isPlus){

			((RelativeLayout)findViewById(R.id.rel_buttons)).setVisibility(View.VISIBLE);
			animateView((RelativeLayout)findViewById(R.id.rel_buttons));

			// Plus 
			Animation anim = new RotateAnimation(360.0f, 0.0f, v.getWidth()/2, v.getHeight()/2);
			anim.setDuration(700);
			anim.setRepeatCount(0);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setFillAfter(true);
			v.setAnimation(anim);
			v.setBackgroundResource(R.drawable.go_menu_button_minus);

		} else {

			((RelativeLayout)findViewById(R.id.rel_buttons)).setVisibility(View.GONE);

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
	public static boolean isUserHereNow(UserResume ur){
		Calendar checkoutCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long checkout = Long.parseLong(ur.getCheckOut());

		return checkout > checkoutCal.getTimeInMillis()/1000;
	}

	public void onClickChat (View v){
		startActivity(new Intent(ActivityUserDetails.this, ActivityChat.class)
		.putExtra("user_id", userResumeData.getUserId())
		.putExtra("nick_name", userResumeData.getNickName()));
	}


	public void onClickPaid (View v){

	}


	public void onClickF2F (View v){

	}

	public void onClickSendLove (View v){
		showDialog(DIALOG_SEND_LOVE);
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


	@Override
	protected Dialog onCreateDialog(int id) {

		final Dialog dialog = new Dialog(ActivityUserDetails.this);

		switch(id) {

		case DIALOG_SEND_LOVE:

			dialog.setContentView(R.layout.diloag_send_love);
			//dialog.setTitle("Custom Dialog");

			((Button)dialog.findViewById(R.id.btn_send)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (((EditText)dialog.findViewById(R.id.edit_review)).getText().toString().length()>0){
						progress.setMessage("Sending love");
						progress.show();
						dialog.dismiss();
						new Thread(new Runnable() {
							@Override
							public void run() {
								resultSendReview = AppCAP.getConnection().sendReview(userResumeData, ((EditText)dialog.findViewById(R.id.edit_review)).getText().toString());
								if (resultSendReview.getResponseCode()==AppCAP.HTTP_ERROR){
									handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
								} else {
									handler.sendEmptyMessage(HANDLE_SENDING_LOVE);
								}
							}
						}).start();
					} else {
						dialog.dismiss();
						Toast.makeText(ActivityUserDetails.this, "Review can't be empty!", Toast.LENGTH_SHORT).show();
					}
				}
			});

			((Button)dialog.findViewById(R.id.btn_cancel)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		}

		return dialog;
	} 

}
