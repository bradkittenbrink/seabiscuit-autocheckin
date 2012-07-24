package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.LinkedInUsersAdapter;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.google.code.linkedinapi.schema.Person;

public class ActivityInviteContacts extends RootActivity  {

    private static final int SCREEN_USER = 1;

    private HorizontalPagerModified pager;

    private LinkedInUsersAdapter adapterUsers; 
    
    private String inviteCodeText;

    private ListView listView;
    
    private ProgressDialog progress;

    private ArrayList<Person> arrayUsers;

    private ArrayList<Person> arraySelectedUsers;

    // Scheduler - create a custom message handler for use in passing venue data
    // from background API call to main thread
    protected Handler taskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            progress.dismiss();

            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts);
        arraySelectedUsers = new ArrayList<Person>();

        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP
                .getLoggedInUserNickname());

        // Horizontal Pager
        pager = (HorizontalPagerModified) findViewById(R.id.pager);
        pager.setCurrentScreen(SCREEN_USER, false);

        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();


        if (AppCAP.isLoggedIn()) {

            // Get data from intent
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                inviteCodeText = extras.getString("inviteCodeText");
            }
            
            this.arrayUsers = AppCAP.getUsersConnections();
            if (this.arraySelectedUsers != null) {
                this.arraySelectedUsers.clear();
            }
            if (Constants.debugLog)
                Log.d("ActivityInviteContacts", "Contacts List Initial Load"); 
            adapterUsers = new LinkedInUsersAdapter(ActivityInviteContacts.this,
                    this.arrayUsers, this.arraySelectedUsers);

            // Display the list of users if the user is logged in
            listView = (ListView) findViewById(R.id.linkedin_users_listview);
            listView.setAdapter(adapterUsers);
            Utils.animateListView(listView);
            // TODO Need to add listview listener here
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long arg3) {
                    if (!AppCAP.isLoggedIn()) {
                        showDialog(DIALOG_MUST_BE_A_MEMBER);
                    } else {
                        if (arraySelectedUsers.contains((Person) adapterUsers.getItem(position))) {
                            arraySelectedUsers.remove((Person) adapterUsers.getItem(position));
                        } else {
                            arraySelectedUsers.add((Person) adapterUsers.getItem(position));
                        }
                        adapterUsers.notifyDataSetChanged();
                        if (arraySelectedUsers.isEmpty()) {
                            ((Button) findViewById(R.id.btn_next)).setClickable(false);
                            ((Button) findViewById(R.id.btn_next)).setBackgroundResource(0);
                        } else {
                            ((Button) findViewById(R.id.btn_next)).setClickable(true);
                            ((Button) findViewById(R.id.btn_next)).setBackgroundResource(R.drawable.button_turquoise_a);
                        }
                    }
                }
            });

        } else {
            setContentView(R.layout.tab_activity_login);
            ((RelativeLayout) findViewById(R.id.rel_log_in))
                    .setBackgroundResource(R.drawable.bg_tabbar_selected);
            ((ImageView) findViewById(R.id.imageview_log_in))
                    .setImageResource(R.drawable.tab_login_pressed);

            RelativeLayout r = (RelativeLayout) findViewById(R.id.rel_log_in);
            RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rel_contacts);

            if (r != null) {
                r.setVisibility(View.VISIBLE);
            }
            if (r1 != null) {
                r1.setVisibility(View.GONE);
            }

        }
        progress.dismiss();

    }

    public void onClickNext(View v) {
        ArrayList<String> arraySelectedUsersIds;
        arraySelectedUsersIds = new ArrayList <String> ();
        
        for( Person person: arraySelectedUsers) {    
            arraySelectedUsersIds.add(person.getId())  ;
        }
        Intent intent = new Intent(this, ActivityInviteContactsConfirm.class);
        intent.putExtra("inviteCodeText", inviteCodeText);
        intent.putStringArrayListExtra("arraySelectedUsersIds", arraySelectedUsersIds);
        startActivity(intent);
    }

    public void onClickBack(View v) {
        onBackPressed();
    }
    @Override
    protected void onStart() {
        if (Constants.debugLog)
            Log.d("Contacts", "ActivityInviteContacts.onStart()");
        super.onStart();

    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("Contacts", "ActivityInviteContacts.onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppCAP.shouldFinishActivities()) {
            onBackPressed();
        } else {
            // Get Notification settings from shared prefs
            ((ToggleButton) findViewById(R.id.toggle_checked_in))
                    .setChecked(AppCAP.getNotificationToggle());
            ((Button) findViewById(R.id.btn_from)).setText(AppCAP
                    .getNotificationFrom());
        }
    }

}
