package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.coffeeandpower.cont.Review;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.Venue;
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

public class ActivityUserDetails extends RootActivity {
    private static final int DIALOG_SEND_PROP = 0;

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

    // User Resume
    private UserResume userResumeData;

    private ArrayList<Venue> favouriteVenues;

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
		intent.putExtra("foursquare_id", favouriteVenues.get(position).getId());
		intent.putExtra("coords", AppCAP.getUserCoordinates());
		startActivity(intent);
	    }
	});

	mapView = (MapView) findViewById(R.id.mapview_user_details);
	imageProfile = (ImageView) findViewById(R.id.imagebutton_user_face);

	// Get data from intent
	Bundle extras = getIntent().getExtras();
	if (extras != null) {

	    String foursquareId = extras.getString("mapuserdata");
	    String fromAct = extras.getString("from_act");

	    if (fromAct != null) {
		if (fromAct.equals("list")) {
		    // From list
		    mud = (UserSmart) extras.getSerializable("mapuserobject");
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
	    ((TextView) findViewById(R.id.textview_user_status)).setText(AppCAP.cleanResponseString(mud.getStatusText()));
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
	    exe.getUserResume(mud.getUserId());
	}

    }

    private Drawable getPinDrawable(String text, GeoPoint gp) {
	PinBlackDrawable icon = new PinBlackDrawable(this, text);
	icon.setBounds(0, -icon.getIntrinsicHeight(), icon.getIntrinsicWidth(), 0);
	return icon;
    }

    /**
     * Update users data in UI, from favouriteVenues and userResumeData
     */
    private void updateUserDataInUI() {
	if (userResumeData != null) {
	    // Load profile picture
	    imageLoader.DisplayImage(userResumeData.getUrlPhoto(), imageProfile, R.drawable.default_avatar50);

	    ((TextView) findViewById(R.id.textview_date)).setText(userResumeData.getJoined());
	    ((TextView) findViewById(R.id.textview_earned)).setText("$" + userResumeData.getTotalEarned());
	    ((TextView) findViewById(R.id.textview_love)).setText(userResumeData.getReviewsLoveReceived());
	    ((TextView) findViewById(R.id.textview_rate)).setText(userResumeData.getHourlyBillingRate().matches("") ? "N/A"
		    : userResumeData.getHourlyBillingRate());

	    ((TextView) findViewById(R.id.textview_place)).setText(AppCAP.cleanResponseString(userResumeData.getVenueName()));
	    ((TextView) findViewById(R.id.textview_street)).setText(AppCAP.cleanResponseString(userResumeData.getVenueAddress()));

	    if (isUserHereNow(userResumeData)) {
		((CustomFontView) findViewById(R.id.box_title)).setText("Checked in ...");
		((LinearLayout) findViewById(R.id.layout_available)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textview_minutes)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textview_minutes)).setText(getAvailableMins(userResumeData));

		if (userResumeData.getUsersHere() > 1) {
		    ((LinearLayout) findViewById(R.id.layout_others_at_venue)).setVisibility(View.VISIBLE);
		    ((TextView) findViewById(R.id.textview_others_here_now))
			    .setText(userResumeData.getUsersHere() == 2 ? "1 other here now"
				    : (userResumeData.getUsersHere() - 1) + " others here now");
		}

	    } else {
		((CustomFontView) findViewById(R.id.box_title)).setText("Was checked in ...");

		if (userResumeData.getUsersHere() > 0) {
		    ((LinearLayout) findViewById(R.id.layout_others_at_venue)).setVisibility(View.VISIBLE);
		    ((TextView) findViewById(R.id.textview_others_here_now))
			    .setText(userResumeData.getUsersHere() == 1 ? "1 other here now" : userResumeData.getUsersHere()
				    + " others here now");
		}
	    }

	    // Check if user has Reviews
	    if (userResumeData.getReviewsTotal() > 0) {

		((LinearLayout) findViewById(R.id.layout_reviews)).setVisibility(View.VISIBLE);

		// Check if we have love review
		if (!userResumeData.getReviewsLoveReceived().equals("0")) {
		    ((LinearLayout) findViewById(R.id.love_inflate)).setVisibility(View.VISIBLE);
		}

		for (Review review : userResumeData.getReviews()) {

		    // Find all love reviews
		    if (review.getIsLove().equals("1")) {

			LayoutInflater inflater = getLayoutInflater();
			View v = inflater.inflate(R.layout.item_love_review, null);

			((TextView) v.findViewById(R.id.textview_review_love)).setText(" from " + review.getAuthor() + ": \""
				+ AppCAP.cleanResponseString(review.getReview()) + "\"");
			((LinearLayout) findViewById(R.id.love_inflate)).addView(v);
		    }
		}
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
		    View view = inflater.inflate(R.layout.item_education_review, null);

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

	}

	// List view with venues
	if (favouriteVenues != null) {
	    MyFavouritePlacesAdapter adapter = new MyFavouritePlacesAdapter(this, favouriteVenues);
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
	animation.setDuration(150);
	set.addAnimation(animation);

	animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		Animation.RELATIVE_TO_SELF, 3.0f, Animation.RELATIVE_TO_SELF, 0.0f);
	animation.setDuration(300);
	set.addAnimation(animation);

	LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
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
	long checkout = Long.parseLong(ur.getCheckOut());
	int mins = (int) ((checkout - (checkoutCal.getTimeInMillis() / 1000)) / 60);
	return mins == 1 ? mins + " min" : mins + " mins";
    }

    /**
     * Check if I am checked in here
     */
    public static boolean isUserHereNow(UserResume ur) {
	Calendar checkoutCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	long checkout = Long.parseLong(ur.getCheckOut());

	return checkout > checkoutCal.getTimeInMillis() / 1000;
    }

    public void onClickChat(View v) {
	startActivity(new Intent(ActivityUserDetails.this, ActivityChat.class).putExtra("user_id", userResumeData.getUserId())
		.putExtra("nick_name", userResumeData.getNickName()));
    }

    public void onClickPaid(View v) {

    }

    public void onClickSendContact(View v) {
	AppCAP.getConnection().sendF2FInvite(mud.getUserId());
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
			exe.sendReviewProp(userResumeData, ((EditText) dialog.findViewById(R.id.edit_review)).getText()
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
	}

	return dialog;
    }

    public void actionFinished(int action) {
	result = exe.getResult();
	switch (action) {
	case Executor.HANDLE_GET_USER_RESUME:
	    if (result != null && result.getObject() != null) {
		if (result.getObject() instanceof ArrayList<?>) {
		    ArrayList<Object> tempArray = (ArrayList<Object>) result.getObject();

		    if (tempArray != null) {
			if (!tempArray.isEmpty()) {

			    if (tempArray.get(0) instanceof UserResume) {
				userResumeData = (UserResume) tempArray.get(0);
			    }

			    if (tempArray.size() > 1) {
				if (tempArray.get(1) instanceof ArrayList<?>) {
				    if (tempArray.get(1) != null) {
					favouriteVenues = (ArrayList<Venue>) tempArray.get(1);
				    }
				}
			    }
			    updateUserDataInUI();
			}
		    }
		}
	    }
	    break;

	}
    }

    public void errorReceived() {
    }

}
