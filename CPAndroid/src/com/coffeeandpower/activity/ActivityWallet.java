package com.coffeeandpower.activity;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;

import android.os.Bundle;
import android.view.View;

public class ActivityWallet extends RootActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
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
