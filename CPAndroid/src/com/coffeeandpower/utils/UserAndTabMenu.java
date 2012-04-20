package com.coffeeandpower.utils;

import android.content.Context;
import android.view.View;

import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickCheckIn(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickPeople(View v) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickLogout(View v) {
		// TODO Auto-generated method stub

	}

}
