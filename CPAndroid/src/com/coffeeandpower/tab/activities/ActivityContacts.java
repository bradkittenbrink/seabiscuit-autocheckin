package com.coffeeandpower.tab.activities;

import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.utils.UserAndTabMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ActivityContacts extends Activity implements TabMenu, UserMenu{

	private UserAndTabMenu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// User and Tab Menu
		menu = new UserAndTabMenu(this);
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
	}

	@Override
	public void onClickMap(View v) {
		menu.onClickMap(v);
	}

	@Override
	public void onClickPlaces(View v) {
		menu.onClickPlaces(v);
	}

	@Override
	public void onClickCheckIn(View v) {
		menu.onClickCheckIn(v);
	}

	@Override
	public void onClickPeople(View v) {
		menu.onClickPeople(v);
	}

	@Override
	public void onClickContacts(View v) {
		menu.onClickContacts(v);
	}

}
