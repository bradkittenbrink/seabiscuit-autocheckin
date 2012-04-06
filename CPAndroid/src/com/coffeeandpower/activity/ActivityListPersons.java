package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.HashMap;

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

import com.coffeandpower.db.CAPDao;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.views.CustomFontView;

public class ActivityListPersons extends ListActivity {

	private static final int LIST_CONVERT_FINISHED = 1404;

	private ArrayList<MapUserData> arrayMapUserData;

	private MyUsersAdapter adapter;

	private CAPDao capDao;

	private CustomFontView textTitle;
	
	private ProgressDialog progress;

	private int myLat;
	private int myLng;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what){

			case LIST_CONVERT_FINISHED:

				progress.dismiss();
				capDao.close();

				if (arrayMapUserData!=null){
					if (!arrayMapUserData.isEmpty()){
						
						textTitle.setText(AppCAP.cleanResponseString(arrayMapUserData.get(0).getVenueName()));
						adapter = new MyUsersAdapter(ActivityListPersons.this, arrayMapUserData, myLat, myLng);
						setListAdapter(adapter);
						animateListView(getListView());
					}
				}

				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_show_persons);


		// Views
		textTitle = (CustomFontView) findViewById(R.id.textview_location_name);
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");

		// Configure database
		capDao = new CAPDao(this);
		capDao.open();

		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras!=null){

			final String foursquareId = extras.getString("mapuserdata");
			myLat = extras.getInt("lat");
			myLng = extras.getInt("lng");
			
			if (foursquareId!=null){
				
				progress.show();
				
				// It may take time...
				new Thread(new Runnable() {
					@Override
					public void run() {

						ArrayList<MapUserData> arrayMapUserDataFromIntent = capDao.getMapsUsersData(foursquareId);

						if (arrayMapUserDataFromIntent!=null){
							HashMap<Integer, MapUserData> setMapUserData = new HashMap<Integer, MapUserData>();

							for (MapUserData mud: arrayMapUserDataFromIntent){
								setMapUserData.put(mud.getUserId(), mud);
							}

							arrayMapUserData = new ArrayList<MapUserData>(setMapUserData.values());
						}
						handler.sendEmptyMessage(LIST_CONVERT_FINISHED);
					}
				}).start();

			}
		}


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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(ActivityListPersons.this, ActivityUserDetails.class);
		intent.putExtra("mapuserobject", (MapUserData)adapter.getItem(position));
		intent.putExtra("from_act", "list");
		startActivity(intent);
		onBackPressed();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}


	public void onClickCheckIn (View v){
		
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
