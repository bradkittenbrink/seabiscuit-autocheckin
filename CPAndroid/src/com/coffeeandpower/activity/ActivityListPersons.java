package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;

public class ActivityListPersons extends ListActivity {

	private static final int HANDLE_GET_USERS_AND_VENUES = 1404;

	private MyUsersAdapter adapterUsers;
	private MyPlacesAdapter adapterPlaces;

	private ProgressDialog progress;

	private DataHolder result;

	private ArrayList<UserSmart> arrayUsers;
	private ArrayList<VenueSmart> arrayVenues;

	private double userLat;
	private double userLng;
	private double data[];

	private boolean isPeopleList;

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
				new CustomDialog(ActivityListPersons.this, "Error", "Internet connection error").show();
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

					// Set default People view
					adapterUsers = new MyUsersAdapter(ActivityListPersons.this, arrayUsers, userLat, userLng);
					setListAdapter(adapterUsers);
					Utils.animateListView(getListView());
				}
				break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_show_persons);

		// Default View
		((CustomFontView) findViewById(R.id.textview_location_name)).setText("People");
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");

		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){

			// Check is it click from Activity or Balloon
			String type = extras.getString("type");
			if (type!=null){
				if (type.equals("form_activity")){

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
			Intent intent = new Intent(ActivityListPersons.this, ActivityUserDetails.class);
			intent.putExtra("mapuserobject", (UserSmart)adapterUsers.getItem(position));
			intent.putExtra("from_act", "list");
			startActivity(intent);
			onBackPressed();
		} else {
			
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
	}


	public void onClickCheckIn (View v){
		if (userLat!=0 && userLng!=0){
			Intent intent = new Intent(ActivityListPersons.this, ActivityCheckInList.class);
			intent.putExtra("lat", (int)(userLat * 1E6));
			intent.putExtra("lng", (int)(userLng * 1E6));
			startActivity(intent);
		}
	}


	public void onClickPlaces (View v){
		isPeopleList = false;
		((CustomFontView) findViewById(R.id.textview_location_name)).setText("Place");
		adapterPlaces = new MyPlacesAdapter(ActivityListPersons.this, arrayVenues, userLat, userLng);
		setListAdapter(adapterPlaces);
		Utils.animateListView(getListView());
	}


	public void onClickPeople (View v){
		isPeopleList = true;
		((CustomFontView) findViewById(R.id.textview_location_name)).setText("People");
		adapterUsers = new MyUsersAdapter(ActivityListPersons.this, arrayUsers, userLat, userLng);
		setListAdapter(adapterUsers);
		Utils.animateListView(getListView());
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
}
