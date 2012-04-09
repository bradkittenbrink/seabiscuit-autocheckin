package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coffeandpower.db.CAPDao;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.MapUserData;
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
	private Button buttonTitle;
	
	private CustomFontView textTitleNickName;
	private CustomFontView textNickName;
	
	private ProgressDialog progress;
	private ProgressBar progressPhoto;
	
	private TextView textStatus;
	private TextView textJoinedDate;
	
	private ImageView imageProfile;
	
	private CAPDao capDao;

	// User Data
	private DataHolder resultGetUserResume;
	private DataHolder resultGetProfilePhoto;
	
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
		buttonTitle = (Button) findViewById(R.id.button_location_name);
		textTitleNickName = (CustomFontView) findViewById(R.id.textview_user_name);
		textStatus = (TextView) findViewById(R.id.textview_user_place);
		textNickName = (CustomFontView) findViewById(R.id.textview_nick_name);
		textJoinedDate = (TextView) findViewById(R.id.textview_date);
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

			textTitleNickName.setText(mud.getNickName());
			textStatus.setText(AppCAP.cleanResponseString(mud.getStatusText()));
			textNickName.setText(mud.getNickName());
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
			textJoinedDate.setText(userResumeData.getJoined());
		}
		
		if (favouriteVenues!=null){
			
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
