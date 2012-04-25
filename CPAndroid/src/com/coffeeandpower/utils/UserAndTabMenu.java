package com.coffeeandpower.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.activity.ActivitySettings;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.tab.activities.ActivityCheckInList;
import com.coffeeandpower.tab.activities.ActivityContacts;
import com.coffeeandpower.tab.activities.ActivityPeopleAndPlaces;
import com.coffeeandpower.views.CustomDialog;

public class UserAndTabMenu implements UserMenu, TabMenu{
	
	public static final int HANDLE_CHECK_OUT = 1800;
	public static final int HANDLE_LOG_OUT = 1801;

	private Context context;

	private ProgressDialog progress;

	private DataHolder result;

	public interface OnUserStateChanged{
		public void onCheckOut();
		public void onLogOut();
	}
	
	OnUserStateChanged userState = new OnUserStateChanged() {
		@Override
		public void onCheckOut() {}
		public void onLogOut() {}
	};
	
	public void setOnUserStateChanged(OnUserStateChanged userState){
		this.userState = userState;
	}
	
	public UserAndTabMenu (Context context){
		this.context = context;
		this.progress = new ProgressDialog(context);
	}

	private Handler handler =  new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what){
			case AppCAP.HTTP_ERROR:
				new CustomDialog(context, "Error", result.getResponseMessage()).show();
				break;
				
			case HANDLE_CHECK_OUT:
				AppCAP.setUserCheckedIn(false);
				userState.onCheckOut();
				break;
			}
		}

	};

	@Override
	public void onClickMap(View v) {

	}

	@Override
	public void onClickPlaces(View v) {
		double[] data = new double[6];
		data = AppCAP.getUserCoordinates();

		Intent intent = new Intent(context, ActivityPeopleAndPlaces.class);
		intent.putExtra("sw_lat", data[0]);
		intent.putExtra("sw_lng", data[1]);
		intent.putExtra("ne_lat", data[2]);
		intent.putExtra("ne_lng", data[3]);
		intent.putExtra("user_lat", data[4]);
		intent.putExtra("user_lng", data[5]);
		intent.putExtra("from", "from_tab");
		intent.putExtra("type", "place");
		context.startActivity(intent);

	}

	@Override
	public void onClickCheckIn(View v) {

		if (AppCAP.isUserCheckedIn()){

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Check Out");
			builder.setMessage("Are you sure you want to be checked out?")
			.setCancelable(false)
			.setPositiveButton("Check Out", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					progress.setMessage("Checking out...");
					progress.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							result = AppCAP.getConnection().checkOut();
							handler.sendEmptyMessage(result.getResponseCode());
						}
					}).start();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

		} else {
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
		intent.putExtra("from", "from_tab");
		intent.putExtra("type", "people");
		context.startActivity(intent);
	}

	@Override
	public void onClickContacts(View v) {
		Intent intent = new Intent(context, ActivityContacts.class);
		context.startActivity(intent);
	}

	@Override
	public void onClickEnterInviteCode(View v) {

	}

	@Override
	public void onClickWallet(View v) {

	}

	@Override
	public void onClickSettings(View v) {
		Intent intent = new Intent(context, ActivitySettings.class);
		context.startActivity(intent);
	}

	@Override
	public void onClickLogout(View v) {
		AppCAP.setLoggedInUserId(0);
		AppCAP.setLocalUserPhotoLargeURL("");
		AppCAP.setLocalUserPhotoURL("");
		AppCAP.setLoggedInUserNickname("");
		AppCAP.setUserLinkedInDetails("", "", "");
		userState.onLogOut();
	}

}
