package com.coffeeandpower.activity;

import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;

public class ActivitySettings extends RootActivity{



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	public void onClickBack (View v){
		onBackPressed();
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	
	
}
