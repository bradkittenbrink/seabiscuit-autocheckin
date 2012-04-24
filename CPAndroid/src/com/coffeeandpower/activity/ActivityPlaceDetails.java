package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyUserSmartAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.cont.VenueSmart.CheckinData;
import com.coffeeandpower.tab.activities.ActivityPeopleAndPlaces;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;

public class ActivityPlaceDetails extends RootActivity{

	private static final int HANDLE_GET_USERS_AND_VENUES = 1404;

	private ProgressDialog progress;

	private String foursquareId;

	private DataHolder result;

	private ArrayList<UserSmart> arrayUsers;
	private ArrayList<VenueSmart> arrayVenues;
	private ArrayList<CheckinData> arrayUsersInVenue;
	private ArrayList<UserSmart> arrayUsersHereNow;
	private ArrayList<UserSmart> arrayUsersWereHere;

	private VenueSmart selectedVenue;
	
	private ListView listWereHere;
	private ListView listHereNow;

	private double data[];

	{
		arrayUsersHereNow = new ArrayList<UserSmart>();
		arrayUsersWereHere = new ArrayList<UserSmart>();
	}
	
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what){

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityPlaceDetails.this, "Error", "Internet connection error").show();
				break;

			case HANDLE_GET_USERS_AND_VENUES:
				if (result.getObject() instanceof Object[]){
					Object[] obj = (Object[]) result.getObject();
					arrayVenues = (ArrayList<VenueSmart>) obj[0]; 
					arrayUsers = (ArrayList<UserSmart>) obj[1];

					for (VenueSmart v:arrayVenues){
						if (v.getFoursquareId().equals(foursquareId)){
							selectedVenue = v;
						}
					}

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

					// Fill veneu and users data
					fillData();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places_details);

		// Viewvs
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		listHereNow = (ListView)findViewById(R.id.list_here_now);
		listWereHere = (ListView)findViewById(R.id.list_were_here);
		
		// Get foursquareId from Intent
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null){
			foursquareId = bundle.getString("foursquare_id");
			data = bundle.getDoubleArray("coords");

			getUsersAndVenues();
		}
		
		
		// On item list click
		listHereNow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				Intent intent = new Intent(ActivityPlaceDetails.this, ActivityUserDetails.class);
				intent.putExtra("mapuserobject", arrayUsersHereNow.get(position));
				intent.putExtra("from_act", "list");
				startActivity(intent);
			}
		});
		
		listWereHere.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				Intent intent = new Intent(ActivityPlaceDetails.this, ActivityUserDetails.class);
				intent.putExtra("mapuserobject", arrayUsersWereHere.get(position));
				intent.putExtra("from_act", "list");
				startActivity(intent);
			}
		});
		
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


	private void fillData (){
		if (selectedVenue!=null){
			((CustomFontView)findViewById(R.id.textview_chat_name)).setText(AppCAP.cleanResponseString(selectedVenue.getName()));
			((CustomFontView)findViewById(R.id.textview_place_name)).setText(AppCAP.cleanResponseString(selectedVenue.getName()));
			((CustomFontView)findViewById(R.id.textview_place_address)).setText(AppCAP.cleanResponseString(selectedVenue.getAddress()));
			
			arrayUsersInVenue = selectedVenue.getArrayCheckins();
			for (CheckinData cd:arrayUsersInVenue){
				if (cd.getCheckedIn()==1){
					// user is here now
					arrayUsersHereNow.add(getUserById(cd.getUserId()));
				} else {
					// users were here
					arrayUsersWereHere.add(getUserById(cd.getUserId()));
				}
			}
			
			// Create adapters and populate Lists
			if (arrayUsersHereNow.isEmpty()){
				listHereNow.setVisibility(View.GONE);
				((CustomFontView)findViewById(R.id.textview_here)).setVisibility(View.GONE);
			} else {
				listHereNow.setAdapter(new MyUserSmartAdapter(ActivityPlaceDetails.this, arrayUsersHereNow));
				listHereNow.postDelayed(new Runnable() {
					@Override
					public void run() {
						Utils.setListViewHeightBasedOnChildren(listHereNow);
					}
				}, 400);
				Utils.animateListView(listHereNow);
			}
			if (arrayUsersWereHere.isEmpty()){
				listWereHere.setVisibility(View.GONE);
				((CustomFontView)findViewById(R.id.textview_worked)).setVisibility(View.GONE);
			} else {
				listWereHere.setAdapter(new MyUserSmartAdapter(ActivityPlaceDetails.this, arrayUsersWereHere));
				listWereHere.postDelayed(new Runnable() {
					@Override
					public void run() {
						Utils.setListViewHeightBasedOnChildren(listWereHere);
					}
				}, 400);
				Utils.animateListView(listWereHere);
			}
			
		}
	}

	
	private UserSmart getUserById (int userId){
		for (UserSmart us:arrayUsers){
			if (us.getUserId()==userId){
				return us;
			}
		}
		return null;
	}
	
	
	public void onClickBack (View v){
		onBackPressed();
	}

	
	public void onClickCheckIn (View v){
		//
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


}
