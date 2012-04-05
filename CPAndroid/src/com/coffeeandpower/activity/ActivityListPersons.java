package com.coffeeandpower.activity;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.MapUserData;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ActivityListPersons extends ListActivity {

	private MapUserData mud;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_show_persons);

		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){
			
			mud = (MapUserData) extras.getSerializable("mapuserdata");
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
