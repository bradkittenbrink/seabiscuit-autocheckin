package com.coffeeandpower.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.tab.activities.ActivityContacts;
import com.coffeeandpower.tab.activities.ActivityMap;
import com.coffeeandpower.tab.activities.ActivityPeopleAndPlaces;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.UserAndTabMenu;

public class ActivityMain extends TabActivity implements UserMenu, TabMenu,
        OnTabChangeListener {

    private static final String TAB_CHECKIN = "Checkin";
    private static final String TAB_FEED = "Feed";
    private static final String TAB_MAP = "Map";
    private static final String TAB_LIST = "List";
    private static final String TAB_PEOPLE = "People";
    private static final String TAB_CONTACT = "Contact";
    private static final String TAB_LOGIN = "Login";
    private static final int TAB_CHECKIN_WIDTH = 60;
    private static final int DIALOG_MUST_BE_A_MEMBER = 30;
    private static final int TAB_MAP_INDEX = 2;
    private static final int TAB_LIST_INDEX = 3;

    private int lastSelectButton = -1;
    private TabHost tabHost;
    private Resources resources;
    private UserAndTabMenu menu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = new UserAndTabMenu(this);

        resources = getResources();

        tabHost = getTabHost();

        tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_separator);

        // Checkin tab
        Intent intentCheckin = new Intent().setClass(this,
                ActivityCheckIn.class);
        TabSpec tabSpecCheckin = tabHost.newTabSpec(TAB_CHECKIN)
                .setIndicator("").setContent(intentCheckin);

        tabHost.addTab(tabSpecCheckin);

        // Feed tab
        Intent intentFeed = new Intent().setClass(this,
                ActivityVenueFeeds.class);
        TabSpec tabSpecFeed = tabHost.newTabSpec(TAB_FEED)
                .setIndicator("", resources.getDrawable(R.drawable.tab_feed_a))
                .setContent(intentFeed);

        tabHost.addTab(tabSpecFeed);

        // Map tab
        Intent intentMap = new Intent().setClass(this, ActivityMap.class);
        TabSpec tabSpecMap = tabHost
                .newTabSpec(TAB_MAP)
                .setIndicator("",
                        resources.getDrawable(R.drawable.tab_places_a))
                .setContent(intentMap);
        tabHost.addTab(tabSpecMap);

        // Place tab
        double[] dataPlace = new double[6];
        dataPlace = AppCAP.getUserCoordinates();
        Intent intentPlaces = new Intent().setClass(this,
                ActivityPeopleAndPlaces.class);
        intentPlaces.putExtra("sw_lat", dataPlace[0]);
        intentPlaces.putExtra("sw_lng", dataPlace[1]);
        intentPlaces.putExtra("ne_lat", dataPlace[2]);
        intentPlaces.putExtra("ne_lng", dataPlace[3]);
        intentPlaces.putExtra("user_lat", dataPlace[4]);
        intentPlaces.putExtra("user_lng", dataPlace[5]);
        intentPlaces.putExtra("from", "from_tab");
        intentPlaces.putExtra("type", "place");
        TabSpec tabSpecPlaces = tabHost.newTabSpec(TAB_LIST).setIndicator("")
                .setContent(intentPlaces);

        tabHost.addTab(tabSpecPlaces);
        // People tab
        double[] dataPeople = new double[6];
        dataPeople = AppCAP.getUserCoordinates();
        Intent intentPeople = new Intent().setClass(this,
                ActivityPeopleAndPlaces.class);
        intentPeople.putExtra("sw_lat", dataPeople[0]);
        intentPeople.putExtra("sw_lng", dataPeople[1]);
        intentPeople.putExtra("ne_lat", dataPeople[2]);
        intentPeople.putExtra("ne_lng", dataPeople[3]);
        intentPeople.putExtra("user_lat", dataPeople[4]);
        intentPeople.putExtra("user_lng", dataPeople[5]);
        intentPeople.putExtra("from", "from_tab");
        intentPeople.putExtra("type", "people");
        TabSpec tabSpecPeople = tabHost
                .newTabSpec(TAB_PEOPLE)
                .setIndicator("",
                        resources.getDrawable(R.drawable.tab_people_a))
                .setContent(intentPeople);

        tabHost.addTab(tabSpecPeople);

        if (AppCAP.isLoggedIn()) {

            // Contacts tab
            Intent intentContacts = new Intent().setClass(this,
                    ActivityContacts.class);
            TabSpec tabSpecContacts = tabHost
                    .newTabSpec(TAB_CONTACT)
                    .setIndicator("",
                            resources.getDrawable(R.drawable.tab_contacts_a))
                    .setContent(intentContacts);

            tabHost.addTab(tabSpecContacts);
        } else {
            // Login tab
            Intent intentLogin = new Intent().setClass(this,
                    ActivityLoginPage.class);
            TabSpec tabSpecLogin = tabHost
                    .newTabSpec(TAB_LOGIN)
                    .setIndicator("",
                            resources.getDrawable(R.drawable.tab_login_a))
                    .setContent(intentLogin);

            tabHost.addTab(tabSpecLogin);
        }
        // Set Default tab
        tabHost.setCurrentTabByTag(TAB_MAP);

        // Set width of the Check in tab
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().width = TAB_CHECKIN_WIDTH;

        // Disable the click of the Check in tab.
        tabHost.getTabWidget().getChildAt(0).setClickable(isRestricted());

        tabHost.getTabWidget().getChildTabViewAt(TAB_LIST_INDEX)
                .setVisibility(View.GONE);

        changeMenuBarState();

        tabHost.setOnTabChangedListener(this);

    }

    public void changeMenuBarState() {

        if (TAB_LOGIN.equalsIgnoreCase(tabHost.getCurrentTabTag())
                && !AppCAP.isLoggedIn()) {
            tabHost.getTabWidget().setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.imageview_check_in))
                    .setVisibility(View.GONE);
        } else {

            if ((lastSelectButton > -1 && tabHost.getTabWidget()
                    .getChildTabViewAt(lastSelectButton) != null)) {
                tabHost.getTabWidget().getChildTabViewAt(lastSelectButton)
                        .setBackgroundResource(0);
            }
            tabHost.getTabWidget().getChildTabViewAt(TAB_MAP_INDEX)
                    .setBackgroundResource(0);

            lastSelectButton = tabHost.getCurrentTab();
            tabHost.getTabWidget().getChildTabViewAt(tabHost.getCurrentTab())
                    .setBackgroundResource(R.drawable.tab_pressed);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        changeMenuBarState();
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
    public void onClickSettings(View v) {
        menu.onClickSettings(v);
    }

    @Override
    public void onClickSupport(View v) {
        menu.onClickSupport(v);
    }

    @Override
    public void onClickLogout(View v) {
        menu.onClickLogout(v);
        onBackPressed();
    }

    @Override
    public void onClickMap(View v) {
        menu.onClickMap(v);
    }

    @Override
    public void onClickNotifications(View v) {
        menu.onClickNotifications(v);

    }

    @Override
    public void onClickPlaces(View v) {
        menu.onClickPlaces(v);
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
    }

    @Override
    public void onClickContacts(View v) {
        menu.onClickContacts(v);
    }

    @Override
    public void onClickVenueFeeds(View v) {
        menu.onClickVenueFeeds(v);
    }
}
