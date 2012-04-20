package com.coffeeandpower.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.activity.ActivityCheckInList;
import com.coffeeandpower.activity.ActivitySettings;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.tab.activities.ActivityMap;
import com.coffeeandpower.tab.activities.ActivityPeopleAndPlaces;

public class UserAndTabMenu implements UserMenu, TabMenu{

	private Context context;

	public UserAndTabMenu (Context context){
		this.context = context;
	}


	@Override
	public void onClickMap(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickPlaces(View v) {


	}

	@Override
	public void onClickCheckIn(View v) {
		double[] data = new double[6];
		data = AppCAP.getUserCoordinates();
		
		double userLat = data[4];
		double userLng = data[5];
		
		if (userLat!=0 && userLng!=0){
			Intent intent = new Intent(context, ActivityCheckInList.class);
			intent.putExtra("lat", (int)(userLat * 1E6));
			intent.putExtra("lng", (int)(userLng * 1E6));
			context.startActivity(intent);
		}
	}

	@Override
	public void onClickPeople(View v) {
		double[] data = new double[6];
		data = AppCAP.getUserCoordinates();

		Intent intent = new Intent(context, ActivityPeopleAndPlaces.class);
		intent.putExtra("sw_lat", data[0]);
		intent.putExtra("sw_lng", data[1]);
		intent.putExtra("ne_lat", data[2]);
		intent.putExtra("ne_lng", data[3]);
		intent.putExtra("user_lat", data[4]);
		intent.putExtra("user_lng", data[5]);
		intent.putExtra("type", "from_tab");
		context.startActivity(intent);
	}

	@Override
	public void onClickContacts(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEnterInviteCode(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickWallet(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickSettings(View v) {
		//Intent intent = new Intent(context, ActivitySettings.class);
		//intent.putExtra("user_obj", loggedUser);
		//startActivityForResult(intent, ACTIVITY_ACCOUNT_SETTINGS);
	}

	@Override
	public void onClickLogout(View v) {
		// TODO Auto-generated method stub

	}

}
