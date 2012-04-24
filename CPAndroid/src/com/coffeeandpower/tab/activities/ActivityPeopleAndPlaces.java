package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
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
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;

public class ActivityPeopleAndPlaces extends ListActivity implements TabMenu, UserMenu{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;
	private static final int HANDLE_GET_USERS_AND_VENUES = 1404;

	private MyUsersAdapter adapterUsers;
	private MyPlacesAdapter adapterPlaces;

	private ProgressDialog progress;

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

	private void setPeopleList (){
		adapterUsers = new MyUsersAdapter(ActivityPeopleAndPlaces.this, arrayUsers, userLat, userLng);
		setListAdapter(adapterUsers);
		Utils.animateListView(getListView());
	}

	private void setPlaceList (){
		isPeopleList = false;
		((CustomFontView) findViewById(R.id.textview_location_name)).setText("Place");
		adapterPlaces = new MyPlacesAdapter(ActivityPeopleAndPlaces.this, arrayVenues, userLat, userLng);
		setListAdapter(adapterPlaces);
		Utils.animateListView(getListView());
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_activity_people_and_places);

		// User and Tab Menu
		menu = new UserAndTabMenu(this);

		// Default View
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		((CustomFontView) findViewById(R.id.textview_location_name)).setText("People");
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");

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
					data[4] = 0;
					data[5] = 0;

					int ulat = extras.getInt("user_lat");
					int ulng = extras.getInt("user_lng");

					if (ulat!=0 && ulng!=0){
						data[4] = ulat / 1E6;
						data[5] = ulng / 1E6;
					}
					userLat = data[4];
					userLng = data[5];
					getUsersAndVenues();
				} else {


				}
			}
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (isPeopleList){
			Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityUserDetails.class);
			intent.putExtra("mapuserobject", (UserSmart)adapterUsers.getItem(position));
			intent.putExtra("from_act", "list");
			startActivity(intent);
			onBackPressed();
		} else {
			Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityPlaceDetails.class);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
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

	}

	@Override
	public void onClickMap(View v) {
		//menu.onClickMap(v);
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
		menu.onClickCheckIn(v);
	}
}
