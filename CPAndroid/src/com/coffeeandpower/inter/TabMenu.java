package com.coffeeandpower.inter;

import android.app.Activity;
import android.view.View;

public interface TabMenu {

    public void onClickMap(View v);

    public boolean onClickVenueFeeds(View v);

    public void onClickPlaces(View v);

    public void onClickCheckIn(View v);

    public void onClickCheckOut(View v, Activity finishActivity);

    public void onClickPeople(View v);

    public void onClickContacts(View v);

    public void onClickMinus(View v);

    public void onClickPlus(View v);

    public void onClickMapFromTab(View v);

    public void onClickQuestion(View v);

}
