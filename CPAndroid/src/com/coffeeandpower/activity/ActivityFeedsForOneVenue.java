package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyFeedsAdapter;
import com.coffeeandpower.adapters.MyPlaceChatAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueChatEntry;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.views.CustomFontView;

public class ActivityFeedsForOneVenue extends RootActivity {

    private ListView list;
    private DataHolder result;
    private Executor exe;
    public ImageLoader imageLoader;
    public ImageView userProfileImage;

    private int venueId = 0;
    private String venueName = "";
    private String lastChatIDString = "0";
    private VenueNameAndFeeds venueNameAndFeeds;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.imageLoader = new ImageLoader(this);
        
        setContentView(R.layout.activity_feeds_for_one_venue); 

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            venueName = extras.getString("venue_name");
            venueId = extras.getInt("venue_id");

            ((CustomFontView) findViewById(R.id.textview_places_chat_name))
                    .setText(venueName);
        }
        RelativeLayout view = (RelativeLayout) findViewById(R.id.layout_places_chat);
        if (!AppCAP.isUserCheckedIn() ||  AppCAP.getUserLastCheckinVenueId() != venueId) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        
        // Executor
        exe = new Executor(ActivityFeedsForOneVenue.this);
        exe.setExecutorListener(new ExecutorInterface() {
            @Override
            public void onErrorReceived() {
            }

            @Override
            public void onActionFinished(int action) {
                result = exe.getResult();

                switch (action) {
                case Executor.HANDLE_VENUE_FEED: 
                    if (result != null && result.getObject() != null
                            && (result.getObject() instanceof VenueNameAndFeeds)) {
                        VenueNameAndFeeds venueNameAndFeeds = (VenueNameAndFeeds) result
                                .getObject();

                        populateList(venueNameAndFeeds);
                    }
                    break;
                case Executor.HANDLE_SEND_VENUE_FEED:  
                    // refresh the list
                    EditText editText = (EditText) findViewById(R.id.edittext_places_chat);
                    editText.setText("");
                    exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true);
                    break;
                }
            }
        });

        // ListView with chat entries
        list = (ListView) findViewById(R.id.listview_places_chat);
        list.setStackFromBottom(true);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                    long arg3) {

            }
        });
    }

    private void populateList(VenueNameAndFeeds venueNameAndFeeds) {
        if (AppCAP.getLocalUserPhotoURL().length() > 5) {
            this.userProfileImage = (ImageView) findViewById(R.id.imageview_user_image);
            imageLoader.DisplayImage(AppCAP.getLocalUserPhotoURL(),
                    userProfileImage, R.drawable.default_avatar25, 70);
        }
        MyFeedsAdapter adapter = new MyFeedsAdapter(this, venueNameAndFeeds.getFeedsArray(), venueNameAndFeeds);            
        list.setAdapter(adapter);
    }

    public void onClickOpenVenueFeeds(View v) {
        // nothing to do in this Activity
    }

    public void onClickSend(View v) {
        EditText editText = (EditText) findViewById(R.id.edittext_places_chat);
        String input = editText.getText().toString();

        if (input != null && input.length() > 0) {
            exe.venueFeeds(venueId, venueName, "0", input, true, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get venue chat
        exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true);
    }

    public void onClickBack(View v) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
