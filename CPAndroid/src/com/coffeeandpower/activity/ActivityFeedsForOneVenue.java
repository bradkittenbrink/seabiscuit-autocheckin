package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyFeedsAdapter;
import com.coffeeandpower.adapters.MyPlaceChatAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueChatEntry;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
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
    private MyFeedsAdapter adapter;
    private String caller;
    private String messageType;

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
            caller = extras.getString("caller");

            ((CustomFontView) findViewById(R.id.textview_places_chat_name))
                    .setText(venueName);
        }
        CustomFontView textUpdateOrQuestion = (CustomFontView) findViewById(R.id.textview_update);
        if (caller.contentEquals("question_button") == true) {
            textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsQuestionText));
            messageType = Feed.FEED_TYPE_QUESTION;
        } else {
            messageType = Feed.FEED_TYPE_UPDATE;
        }
        RelativeLayout view = (RelativeLayout) findViewById(R.id.layout_places_chat);
        
        if (caller.contentEquals("postable_venues") == true ||
                (AppCAP.isUserCheckedIn() &&  AppCAP.getUserLastCheckinVenueId() == venueId)) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        if (caller.contentEquals("pen_button") || 
                caller.contentEquals("postable_venues") || 
                caller.contentEquals("question_button")) {
            EditText editText = (EditText) findViewById(R.id.edittext_places_chat);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE); 
            if (inputManager != null && editText != null) {
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(),
                       InputMethodManager.SHOW_FORCED);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            EditText editText = (EditText) findViewById(R.id.edittext_places_chat);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE); 
            if (inputManager != null && editText != null) {
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(),
                       InputMethodManager.HIDE_NOT_ALWAYS);
            }
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
                    exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true, "");
                    break;
                }
            }
        });

        // ListView with chat entries
        list = (ListView) findViewById(R.id.listview_places_chat);
        ((EditText) findViewById(R.id.edittext_places_chat)).setImeOptions(EditorInfo.IME_ACTION_SEND);
        ((EditText) findViewById(R.id.edittext_places_chat))
        .setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
 
                if (actionId == EditorInfo.IME_NULL && 
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER && 
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    SendEntry(v);
                }
                return false;
            }
        });


    }

    private void showDialogQuestionTo(final String input) {
        // custom dialog
        
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    exe.venueFeeds(venueId, venueName, "0", input, true, true, messageType);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
                }
            }
        };
        int numberOfUsersHere = CacheMgrService.getNumberOfCheckedInInVenue(venueId);
        String mess;
        if (numberOfUsersHere == 2) {
            mess = (String) getResources().getText(R.string.dialog_post_a_question_content_1);
        } else if (numberOfUsersHere > 2) {
            mess = String.format((String) getResources().getText(R.string.dialog_post_a_question_content_2), numberOfUsersHere - 1);
        } else {
            mess = (String) getResources().getText(R.string.dialog_post_a_question_content_0);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((String) getResources().getText(R.string.dialog_post_a_question_title));
        builder.setMessage(mess).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();
    }

    private void populateList(VenueNameAndFeeds venueNameAndFeeds) {
        if (AppCAP.getLocalUserPhotoURL().length() > 5) {
            this.userProfileImage = (ImageView) findViewById(R.id.imageview_user_image);
            imageLoader.DisplayImage(AppCAP.getLocalUserPhotoURL(),
                    userProfileImage, R.drawable.default_avatar25, 70);
        }
        adapter = new MyFeedsAdapter(this, venueNameAndFeeds.getFeedsArray(), venueNameAndFeeds);            
        list.setAdapter(adapter);
    }

    public void onClickOpenVenueFeeds(View v) {
        // nothing to do in this Activity
    }

    public void SendEntry(TextView editText) {
        String input = editText.getText().toString(); 

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE); 

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                   InputMethodManager.HIDE_NOT_ALWAYS);
        if (input != null && input.length() > 0) {
            if (messageType == Feed.FEED_TYPE_QUESTION) {
                showDialogQuestionTo(input);
            } else {
                exe.venueFeeds(venueId, venueName, "0", input, true, true, messageType);
            }
        }
    }

    public void onClickSend(View v) {
        EditText editText = (EditText) findViewById(R.id.edittext_places_chat);
        SendEntry(editText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get venue chat
        exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true, "");
    }

    public void onClickFeeds(View v) {
        if (caller.contentEquals("feeds_list") == true) {
            onBackPressed();
        } else {
            Intent intent = new Intent(this, ActivityVenueFeeds.class);
            this.startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
