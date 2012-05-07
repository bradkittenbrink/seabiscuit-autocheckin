package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityPlaceDetails;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;

public class ActivityPeopleAndPlaces extends RootActivity implements TabMenu, UserMenu{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;
	private static final int HANDLE_GET_USERS_AND_VENUES = 1404;

	private MyUsersAdapter adapterUsers;
	private MyPlacesAdapter adapterPlaces;

	private ProgressDialog progress;

	private ListView listView;

	private HorizontalPagerModified pager;

	private DataHolder result;

	private ArrayList<UserSmart> arrayUsers;
	private ArrayList<VenueSmart> arrayVenues;

	private double userLat;
	private double userLng;
	private double data[];

	private boolean isPeopleList;

	private UserAndTabMenu menu;

	private String type;

	{
		data = new double[6];
		// default view is People List
		isPeopleList = true;
	}


	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what){

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityPeopleAndPlaces.this, "Error", "Internet connection error").show();
				break;

			case HANDLE_GET_USERS_AND_VENUES:
				if (result.getObject() instanceof Object[]){
					Object[] obj = (Object[]) result.getObject();
					arrayVenues = (ArrayList<VenueSmart>) obj[0]; 
					arrayUsers = (ArrayList<UserSmart>) obj[1];

					// Sort users list
					if (arrayUsers!=null){
						Collections.sort(arrayUsers, new Comparator<UserSmart>() {
							@Override
							public int compare(UserSmart m1, UserSmart m2) {
								if (m1.getCheckedIn()>m2.getCheckedIn()){
									return -1;
								}
								return 1;
							}
						});
					}

					if (type.equals("people")){
						setPeopleList();
					} else {
						setPlaceList();
					}

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
		} else {
			((TextView)findViewById(R.id.textview_check_in)).setText("Check In");
		}
	}

	private void setPeopleList (){
		adapterUsers = new MyUsersAdapter(ActivityPeopleAndPlaces.this, arrayUsers, userLat, userLng);
		listView.setAdapter(adapterUsers);
		Utils.animateListView(listView);
	}

	private void setPlaceList (){
		isPeopleList = false;
		((CustomFontView) findViewById(R.id.textview_location_name)).setText("Place");
		adapterPlaces = new MyPlacesAdapter(ActivityPeopleAndPlaces.this, arrayVenues, userLat, userLng);
		listView.setAdapter(adapterPlaces);
		Utils.animateListView(listView);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_activity_people_and_places);

		((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());

		// Default View
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		((CustomFontView) findViewById(R.id.textview_location_name)).setText("People");
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");

		listView = (ListView)findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				if (isPeopleList){
					if (!AppCAP.isLoggedIn()){
						showDialog(DIALOG_MUST_BE_A_MEMBER);
					} else {
						Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityUserDetails.class);
						intent.putExtra("mapuserobject", (UserSmart)adapterUsers.getItem(position));
						intent.putExtra("from_act", "list");
						startActivity(intent);
						onBackPressed();
					}
				} else {
					Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityPlaceDetails.class);
					intent.putExtra("foursquare_id", arrayVenues.get(position).getFoursquareId());
					intent.putExtra("coords", data);
					startActivity(intent);
				}

			}
		});


		// User and Tab Menu
		checkUserState();
		menu = new UserAndTabMenu(this);
		menu.setOnUserStateChanged(new OnUserStateChanged() {
			@Override
			public void onCheckOut() {
				checkUserState();
			}

			@Override
			public void onLogOut() {}
		});



		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){

			// Check is it People or Places List
			type = extras.getString("type");
			if (type.equals("people")){
				((RelativeLayout)findViewById(R.id.rel_people)).setBackgroundResource(R.drawable.bg_tabbar_selected);
				((ImageView)findViewById(R.id.imageview_people)).setImageResource(R.drawable.tab_people_pressed);
				((TextView)findViewById(R.id.text_people)).setTextColor(Color.WHITE);
			} else {
				((RelativeLayout)findViewById(R.id.rel_places)).setBackgroundResource(R.drawable.bg_tabbar_selected);
				((ImageView)findViewById(R.id.imageview_places)).setImageResource(R.drawable.tab_places_pressed);
				((TextView)findViewById(R.id.text_places)).setTextColor(Color.WHITE);
			}

			// Check is it click from Activity or Balloon
			String from = extras.getString("from");
			if (from!=null){
				if (from.equals("from_tab")){

					data[0] = extras.getDouble("sw_lat");
					data[1] = extras.getDouble("sw_lng");
					data[2] = extras.getDouble("ne_lat");
					data[3] = extras.getDouble("ne_lng");
					data[4] = extras.getDouble("user_lat");
					data[5] = extras.getDouble("user_lng");

					userLat = data[4];
					userLng = data[5];
					getUsersAndVenues();
				} else {


				}
			}
		}

	}


	@Override
	protected void onResume() {
		super.onResume();

		if (AppCAP.shouldFinishActivities()){
			onBackPressed();
		} else {
			checkUserState();
			
			// Check and Set Notification settings
			menu.setOnNotificationSettingsListener((ToggleButton)findViewById(R.id.toggle_checked_in),(Button)findViewById(R.id.btn_from));
		}
	}


	public void onClickMenu (View v){
		if (pager.getCurrentScreen()==SCREEN_USER){
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_USER, true);
		}
	}


	private void getUsersAndVenues (){
		progress.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				result = AppCAP.getConnection().getVenuesAndUsersWithCheckinsInBoundsDuringInterval(data, 7);
				if (result.getResponseCode()==AppCAP.HTTP_ERROR){
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				} else {
					handler.sendEmptyMessage(HANDLE_GET_USERS_AND_VENUES);
				}
			}
		}).start();
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
	public void onClickEnterInviteCode(View v) {
		menu.onClickEnterInviteCode(v);		
	}

	@Override
	public void onClickWallet(View v) {
		menu.onClickWallet(v);

	}

	@Override
	public void onClickSettings(View v) {
		menu.onClickSettings(v);

	}

	@Override
	public void onClickLogout(View v) {
		menu.onClickLogout(v);
		onBackPressed();
	}

	@Override
	public void onClickMap(View v) {
		menu.onClickMap(v);
		finish();
	}

	@Override
	public void onClickContacts(View v) {
		menu.onClickContacts(v);
		finish();
	}

	public void onClickPlaces (View v){
		menu.onClickPlaces(v);
		finish();
	}


	public void onClickPeople (View v){
		menu.onClickPeople(v);
		finish();
	}

	@Override
	public void onClickCheckIn(View v) {
		if (AppCAP.isLoggedIn()){
			menu.onClickCheckIn(v);
		} else {
			showDialog(DIALOG_MUST_BE_A_MEMBER);
		}
	}

}
