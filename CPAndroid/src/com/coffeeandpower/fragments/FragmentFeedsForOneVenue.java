package com.coffeeandpower.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
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
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyFeedsAdapter;
import com.coffeeandpower.adapters.MyVenueFeedsAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;

import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class FragmentFeedsForOneVenue extends Fragment {

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
    private Bundle intentExtras;
    private ProgressDialog progress;

    public FragmentFeedsForOneVenue(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }
    
    public void startUpdate() {
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = null;
        mainView = inflater.inflate(R.layout.activity_feeds_for_one_venue, null);
        progress = new ProgressDialog(this.getActivity());
        if (AppCAP.isLoggedIn()) {
            progress.setMessage("Loading...");
        } else {
            progress.setMessage("You must login to see the feeds ...");
        }
        progress.show();
        // Get data from intent
        extractExtra();
        return mainView;

    }
    
    public void extractExtra() {
        if (intentExtras != null) {
            venueName = intentExtras.getString("venue_name");
            venueId = intentExtras.getInt("venue_id");
            setCaller(intentExtras.getString("caller"));

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (AppCAP.isLoggedIn()) {
            if (getActivity().getWindow().findViewById(R.id.textview_contact_list) != null) {
                ((CustomFontView) getActivity().getWindow().findViewById(R.id.textview_contact_list)).setText(venueName);
            }
            CustomFontView textUpdateOrQuestion = (CustomFontView) getView().findViewById(R.id.textview_update);
            if (getCaller().contentEquals("question_button") == true) {
                textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsQuestionText));
                messageType = Feed.FEED_TYPE_QUESTION;
                showQuestionActionButton();
            } else if (getCaller().contentEquals("pen_button") == true ||
                    getCaller().contentEquals("postable_venues") == true) {
                textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsUpdateText));
                messageType = Feed.FEED_TYPE_UPDATE;
                showUpdateActionButton();
            } else{
                textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsUpdateText));
                messageType = Feed.FEED_TYPE_UPDATE;
                hideQuestionUpdateActionButton();
            }
            RelativeLayout chatView = (RelativeLayout) getView().findViewById(R.id.layout_places_chat);
            chatView.setVisibility(View.GONE);
            if (getCaller().contentEquals("postable_venues") == true) {
                chatView.setVisibility(View.VISIBLE);
            } 
            EditText editText = (EditText) getView().findViewById(R.id.edittext_places_chat);
            if (getCaller().contentEquals("pen_button") || 
                    getCaller().contentEquals("postable_venues") || 
                    getCaller().contentEquals("question_button")) {
                showKeyboard();
            } else {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
                if (inputManager != null && editText != null) {
                    inputManager.hideSoftInputFromWindow(editText.getWindowToken(),
                           InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {          

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
                        if (inputManager != null && v != null) {
                            inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                                   InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                }
            });
            // Executor
            exe = new Executor(getActivity());
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
                        EditText editText = (EditText) getView().findViewById(R.id.edittext_places_chat);
                        editText.setText("");
                        exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true, "");
                        break;
                    }
                }
            });

            // ListView with chat entries
            list = (ListView) getView().findViewById(R.id.listview_places_chat);
            ((EditText) getView().findViewById(R.id.edittext_places_chat)).setImeOptions(EditorInfo.IME_ACTION_SEND);
            ((EditText) getView().findViewById(R.id.edittext_places_chat))
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

    }
    
    public void changeToUpdateMode() {
        setCaller("pen_button");
        showKeyboard();
        showUpdateActionButton();
    }
    
    public void showUpdateActionButton() {
        ((Button) (getActivity())
                .findViewById(R.id.btn_top_refresh)).setVisibility(View.GONE);
        ((Button) (getActivity())
                .findViewById(R.id.btn_top_cancel)).setVisibility(View.VISIBLE);

    }
    
    public void showQuestionActionButton() {
        ((Button) (getActivity())
                .findViewById(R.id.btn_top_refresh)).setVisibility(View.GONE);
        ((Button) (getActivity())
                .findViewById(R.id.btn_top_cancel)).setVisibility(View.VISIBLE);
    }
    
    public void hideActionButton(int showHide) {
        ImageView plus = (ImageView) (getActivity())
                .findViewById(R.id.imageview_button_plus);
        ImageView minus = (ImageView) (getActivity())
                .findViewById(R.id.imageview_button_minus);

        RelativeLayout rel_map = (RelativeLayout) (getActivity()).findViewById(R.id.rel_map);
        RelativeLayout rel_feed = (RelativeLayout) (getActivity()).findViewById(R.id.rel_feed);
        RelativeLayout rel_places = (RelativeLayout) (getActivity()).findViewById(R.id.rel_places);
        RelativeLayout rel_people = (RelativeLayout) (getActivity()).findViewById(R.id.rel_people);
        RelativeLayout rel_contacts = (RelativeLayout) (getActivity()).findViewById(R.id.rel_contacts);
        

        minus.setVisibility(View.GONE);
        plus.setVisibility(showHide);
        rel_map.setVisibility(showHide);
        rel_feed.setVisibility(showHide);
        rel_places.setVisibility(showHide);
        rel_people.setVisibility(showHide);
        rel_contacts.setVisibility(showHide);
    }
    
    public void hideQuestionUpdateActionButton() {
        RelativeLayout chatView = (RelativeLayout) getView().findViewById(R.id.layout_places_chat);       
        chatView.setVisibility(View.GONE);
        ((Button) (getActivity())
                .findViewById(R.id.btn_top_refresh)).setVisibility(View.VISIBLE);
        ((Button) (getActivity())
                .findViewById(R.id.btn_top_cancel)).setVisibility(View.GONE);

    }
    
    public void showKeyboard() {
        if (getCaller().contentEquals("pen_button") || 
                getCaller().contentEquals("postable_venues") || 
                getCaller().contentEquals("question_button")) {

            RelativeLayout chatView = (RelativeLayout) getView().findViewById(R.id.layout_places_chat);       
            chatView.setVisibility(View.VISIBLE);
            final EditText editText = (EditText) getView().findViewById(R.id.edittext_places_chat);
            editText.requestFocus();
    
            editText.postDelayed(new Runnable() {
    
                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);            }
            }, 1000); 
        }
    }

    private void showDialogQuestionTo(final String input) {
        // custom dialog
        
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    hideQuestionUpdateActionButton();
                    setCaller("feeds_list");
                    ((ActivityVenueFeeds) getActivity()).changeIntentCaller("feeds_list");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((String) getResources().getText(R.string.dialog_post_a_question_title));
        builder.setMessage(mess).setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();
    }

    private void populateList(VenueNameAndFeeds venueNameAndFeeds) {
        if (AppCAP.getLocalUserPhotoURL().length() > 5) {
            this.userProfileImage = (ImageView) getView().findViewById(R.id.imageview_user_image);
            imageLoader.DisplayImage(AppCAP.getLocalUserPhotoURL(),
                    userProfileImage, R.drawable.default_avatar25, 70);
        }
        adapter = new MyFeedsAdapter(getActivity(), venueNameAndFeeds.getFeedsArray(), venueNameAndFeeds);            
        list.setAdapter(adapter);
        progress.dismiss();
        showKeyboard();
    }

    public void onClickOpenVenueFeeds(View v) {
        // nothing to do in this Activity
    }

    public void SendEntry(TextView editText) {
        String input = editText.getText().toString(); 

        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                   InputMethodManager.HIDE_NOT_ALWAYS);
        if (input != null && input.length() > 0) {
            if (messageType == Feed.FEED_TYPE_QUESTION) {
                showDialogQuestionTo(input);
            } else {
                hideQuestionUpdateActionButton();
                setCaller("feeds_list");
                ((ActivityVenueFeeds) getActivity()).changeIntentCaller("feeds_list");
                exe.venueFeeds(venueId, venueName, "0", input, true, true, messageType);
            }
        }
    }

    public void onClickSend(View v) {
        EditText editText = (EditText) getView().findViewById(R.id.edittext_places_chat);
        SendEntry(editText);
    }

    public void onClickCancel(View v) {
        hideQuestionUpdateActionButton();
        setCaller("feeds_list");
        ((ActivityVenueFeeds) getActivity()).changeIntentCaller("feeds_list");
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (((ActivityVenueFeeds) getActivity()).getFragmentId() == R.id.tab_fragment_area_feeds_for_one_venue) {
            if (getActivity().getWindow().findViewById(R.id.textview_contact_list) != null) {
                ((CustomFontView) getActivity().getWindow().findViewById(R.id.textview_contact_list)).setText(venueName);
            }
            // Get venue chat
           exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true, "");
        }
    }

    @Override
    public void onPause() {
        progress.dismiss();
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    public void refresh(Bundle intentExtras) {
        if (intentExtras != null) {
            this.intentExtras = intentExtras;
            extractExtra();
        }
        CustomFontView textUpdateOrQuestion = (CustomFontView) getView().findViewById(R.id.textview_update);
        if (getCaller().contentEquals("question_button") == true) {
            textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsQuestionText));
            messageType = Feed.FEED_TYPE_QUESTION;
            showQuestionActionButton();
        } else if (getCaller().contentEquals("pen_button") == true) {
            textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsUpdateText));
            messageType = Feed.FEED_TYPE_UPDATE;
            showUpdateActionButton();
        } else{
            textUpdateOrQuestion.setText((String) getResources().getText(R.string.activityFeedsUpdateText));
            messageType = Feed.FEED_TYPE_UPDATE;
            hideQuestionUpdateActionButton();
        }
        RelativeLayout chatView = (RelativeLayout) getView().findViewById(R.id.layout_places_chat);       
        chatView.setVisibility(View.GONE);
        if (getCaller().contentEquals("postable_venues") == true) {
            chatView.setVisibility(View.VISIBLE);
        }
        if (getCaller().contentEquals("pen_button") || 
                getCaller().contentEquals("postable_venues") || 
                getCaller().contentEquals("question_button")) {
            showKeyboard();
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            EditText editText = (EditText) getView().findViewById(R.id.edittext_places_chat);
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
            if (inputManager != null && editText != null) {
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(),
                       InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        if (getActivity().getWindow().findViewById(R.id.textview_contact_list) != null) {
            ((CustomFontView) getActivity().getWindow().findViewById(R.id.textview_contact_list)).setText(venueName);
        }
        exe.venueFeeds(venueId, venueName, lastChatIDString, "", false, true, "");
    }

    public void onClickFeeds(View v) {
        if (getCaller().contentEquals("feeds_list") == true) {
            getActivity().onBackPressed();
        } else {
            Intent intent = new Intent(getActivity(), ActivityVenueFeeds.class);
            this.startActivity(intent);
        }
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

}
