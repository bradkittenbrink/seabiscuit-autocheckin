package com.coffeeandpower.tab.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;

public class ActivityContacts extends RootActivity implements TabMenu, UserMenu {

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;

	private HorizontalPagerModified pager;


	private UserAndTabMenu menu;

	private Executor exe;

	private DataHolder result;

	/**
	 * Check if user is checked in or not
	 */
	private void checkUserState() {

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_activity_contacts);

		// Executor
		exe = new Executor(ActivityContacts.this);
		exe.setExecutorListener(new ExecutorInterface() {
			@Override
			public void onErrorReceived() {
				errorReceived();
			}

			@Override
			public void onActionFinished(int action) {
				actionFinished(action);
			}
		});

		((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());

		// Horizontal Pager
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		// User and Tab Menu
		menu = new UserAndTabMenu(this);
		menu.setOnUserStateChanged(new OnUserStateChanged() {

			@Override
			public void onLogOut() {
			}

			@Override
			public void onCheckOut() {
				checkUserState();
			}
		});

		if (AppCAP.isLoggedIn()) {
			((RelativeLayout) findViewById(R.id.rel_contacts)).setBackgroundResource(R.drawable.bg_tabbar_selected);
			((ImageView) findViewById(R.id.imageview_contacts)).setImageResource(R.drawable.tab_contacts_pressed);
			((TextView) findViewById(R.id.text_contacts)).setTextColor(Color.WHITE);

			// Get Notification settings from shared prefs
			((ToggleButton) findViewById(R.id.toggle_checked_in)).setChecked(AppCAP.getNotificationToggle());
			((Button) findViewById(R.id.btn_from)).setText(AppCAP.getNotificationFrom());

			// Check and Set Notification settings
			menu.setOnNotificationSettingsListener((ToggleButton) findViewById(R.id.toggle_checked_in),
					(Button) findViewById(R.id.btn_from), false);

			// Get contacts list
			exe.getContactsList();

			if (AppCAP.isUserCheckedIn()) {
				((TextView) findViewById(R.id.textview_check_in)).setText("Check Out");
				((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).setAnimation(AnimationUtils.loadAnimation(ActivityContacts.this,
						R.anim.rotate_indefinitely));
			} else {
				((TextView) findViewById(R.id.textview_check_in)).setText("Check In");
				((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).clearAnimation();
			}


		} else {
			setContentView(R.layout.tab_activity_login);
			((RelativeLayout) findViewById(R.id.rel_log_in)).setBackgroundResource(R.drawable.bg_tabbar_selected);
			((ImageView) findViewById(R.id.imageview_log_in)).setImageResource(R.drawable.tab_login_pressed);
			((TextView) findViewById(R.id.text_log_in)).setTextColor(Color.WHITE);

			RelativeLayout r = (RelativeLayout) findViewById(R.id.rel_log_in);
			RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rel_contacts);

			if (r != null) {
				r.setVisibility(View.VISIBLE);
			}
			if (r1 != null) {
				r1.setVisibility(View.GONE);
			}
			
			if (AppCAP.isUserCheckedIn()) {
				((TextView) findViewById(R.id.textview_check_in)).setText("Check Out");
				((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).setAnimation(AnimationUtils.loadAnimation(ActivityContacts.this,
						R.anim.rotate_indefinitely));
			} else {
				((TextView) findViewById(R.id.textview_check_in)).setText("Check In");
				((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).clearAnimation();
			}
		}



	}

	public void onClickLinkedIn(View v) {
		AppCAP.setShouldFinishActivities(true);
		AppCAP.setStartLoginPageFromContacts(true);
		onBackPressed();
	}


	public void onClickMenu(View v) {
		if (pager.getCurrentScreen() == SCREEN_USER) {
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_USER, true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*if (AppCAP.shouldFinishActivities()) {
			onBackPressed();
		} else {
			// Get Notification settings from shared prefs
			((ToggleButton) findViewById(R.id.toggle_checked_in)).setChecked(AppCAP.getNotificationToggle());
			((Button) findViewById(R.id.btn_from)).setText(AppCAP.getNotificationFrom());

			// Check and Set Notification settings
			menu.setOnNotificationSettingsListener((ToggleButton) findViewById(R.id.toggle_checked_in),
					(Button) findViewById(R.id.btn_from), false);

			// Get contacts list
			exe.getContactsList();
		}*/
	}

	private void errorReceived() {

	}

	private void actionFinished(int action) {
		result = exe.getResult();

		switch (action) {

		case 0:

			break;
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
		if (AppCAP.isLoggedIn()) {
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
		// menu.onClickContacts(v);
	}

}