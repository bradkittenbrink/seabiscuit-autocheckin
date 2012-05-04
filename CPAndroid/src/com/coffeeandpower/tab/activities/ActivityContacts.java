package com.coffeeandpower.tab.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityLoginPage;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;

public class ActivityContacts extends RootActivity implements TabMenu, UserMenu{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;

	private HorizontalPagerModified pager;

	private UserAndTabMenu menu;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_activity_contacts);

		((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());

		// Horizontal Pager
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		// User and Tab Menu
		menu = new UserAndTabMenu(this);
		menu.setOnUserStateChanged(new OnUserStateChanged() {

			@Override
			public void onLogOut() {}

			@Override
			public void onCheckOut() {
				checkUserState();
			}
		});

		if (AppCAP.isLoggedIn()){
			((RelativeLayout)findViewById(R.id.rel_contacts)).setBackgroundResource(R.drawable.bg_tabbar_selected);
			((ImageView)findViewById(R.id.imageview_contacts)).setImageResource(R.drawable.tab_contacts_pressed);

		} else {
			setContentView(R.layout.tab_activity_login);
			((RelativeLayout)findViewById(R.id.rel_log_in)).setBackgroundResource(R.drawable.bg_tabbar_selected);
			((ImageView)findViewById(R.id.imageview_contacts)).setImageResource(R.drawable.tab_login_pressed);

			RelativeLayout r = (RelativeLayout)findViewById(R.id.rel_log_in);
			RelativeLayout r1 = (RelativeLayout)findViewById(R.id.rel_contacts);

			if (r!=null){ r.setVisibility(View.VISIBLE);}
			if (r1!=null){ r1.setVisibility(View.GONE);}
		}

		((TextView)findViewById(R.id.text_contacts)).setTextColor(Color.WHITE);
	}


	public void onClickLinkedIn(View v) {
		AppCAP.setShouldFinishActivities(true);
		AppCAP.setStartLoginPageFromContacts(true);
		onBackPressed();
	}


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

	public void onClickMenu (View v){
		if (pager.getCurrentScreen()==SCREEN_USER){
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_USER, true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (AppCAP.shouldFinishActivities()){
			onBackPressed();
		}
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
	public void onClickPlaces(View v) {
		menu.onClickPlaces(v);
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

	@Override
	public void onClickPeople(View v) {
		menu.onClickPeople(v);
		finish();
	}

	@Override
	public void onClickContacts(View v) {
		//menu.onClickContacts(v);
	}

}
