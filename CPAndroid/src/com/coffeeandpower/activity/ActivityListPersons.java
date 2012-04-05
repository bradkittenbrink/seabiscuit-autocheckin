package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.coffeandpower.db.CAPDao;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.MapUserData;

public class ActivityListPersons extends ListActivity {


	private ArrayList<MapUserData> arrayMapUserData;
	
	private CAPDao capDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_show_persons);

		// Configure database
		capDao = new CAPDao(this);
		capDao.open();
		
		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){
			
			String foursquareId = extras.getString("mapuserdata");

			if (foursquareId!=null){
				
				arrayMapUserData = capDao.getMapsUsersData(foursquareId);
				capDao.close();
				
				Log.d("LOG", "arrayFormDb: " + arrayMapUserData.size());
			}
		}
		
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
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
}
