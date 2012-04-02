package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.adapters.MyVenuesAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Venue;
import com.google.android.maps.GeoPoint;

public class ActivityCheckInList extends ListActivity{

	private static final int RESPONSE_OK = 200;
	
	private ProgressDialog progress;
	
	private DataHolder dh;
	private MyVenuesAdapter adapter;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			progress.dismiss();
			
			switch (msg.what){
			
			case RESPONSE_OK:
				
				adapter = new MyVenuesAdapter(ActivityCheckInList.this, (ArrayList<Venue>) dh.getObject());
				setListAdapter(adapter);
				animateListView(getListView());
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_in_list);
		
		// Views
		progress = new ProgressDialog(ActivityCheckInList.this);
		
		
		// Views state
		progress.setMessage("Searching nearest locations...");
		
		
		// Getdata from Intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){
			
			int lng = extras.getInt("lng");
			int lat = extras.getInt("lat");
			
			final GeoPoint gp = new GeoPoint(lat, lng);
			progress.show();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					dh = AppCAP.getConnection().getVenuesCloseToLocation(gp, 20);
					handler.sendEmptyMessage(dh.getResponseCode());
				}
			}).start();
		}

	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent (ActivityCheckInList.this, ActivityCheckIn.class);
		intent.putExtra("venue", (Venue)adapter.getItem(position));
		startActivity(intent);
	}



	@Override
	protected void onResume() {
		super.onResume();
	}

	
	public void onClickCancel (View v){
		onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void animateListView(ListView lv){
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(150);
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
				);
		animation.setDuration(300);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);       
		lv.setLayoutAnimation(controller);
	}

}
