package com.coffeeandpower.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.app.R;
import com.coffeeandpower.fragments.FragmentPlaceDetails;

public class ActivityPlaceDetails extends RootActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details_dialog);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment newFragment= new FragmentPlaceDetails(getIntent().getExtras());
        transaction.add(R.id.tab_fragment_area_place_details, newFragment);
        transaction.commit();
    }

    public void onClickBack(View v) {
        onBackPressed();
    }

}
