package com.coffeeandpower.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyFeedsAdapter;
import com.coffeeandpower.adapters.MyVenueFeedsAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.cont.VenueSmart;
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
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class FragmentFeedsForOneVenue extends Fragment {

    public static int TAG_FEED_ID = 1;
    public static int TAG_FEED_TYPE = 2;
    private ListView list;
    private DataHolder result;
    private Executor exe;
    public ImageLoader imageLoader;
    public ImageView userProfileImage;

    private int venueId = 0;
    private String venueName = "";
    private String lastChatIDString = "0";
    private VenueNameAndFeeds venueNameAndFeeds = null;
    private MyFeedsAdapter adapter = null;
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
        mainView = inflater
                .inflate(R.layout.activity_feeds_for_one_venue, null);
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
            if (getActivity().getWindow().findViewById(
                    R.id.textview_contact_list) != null) {
                ((CustomFontView) getActivity().getWindow().findViewById(
                        R.id.textview_contact_list)).setText(venueName);
            }
            CustomFontView textUpdateOrQuestion = (CustomFontView) getView()
                    .findViewById(R.id.textview_update);
            if (getCaller().contentEquals("question_button") == true) {
                textUpdateOrQuestion.setText((String) getResources().getText(
                        R.string.activityFeedsQuestionText));
                messageType = Feed.FEED_TYPE_QUESTION;
                hideRefreshButton();
            } else if (getCaller().contentEquals("pen_button") == true
                    || getCaller().contentEquals("postable_venues") == true) {
                textUpdateOrQuestion.setText((String) getResources().getText(
                        R.string.activityFeedsUpdateText));
                messageType = Feed.FEED_TYPE_UPDATE;
                showUpdateActionButton();
            } else {
                textUpdateOrQuestion.setText((String) getResources().getText(
                        R.string.activityFeedsUpdateText));
                messageType = Feed.FEED_TYPE_UPDATE;
                hideQuestionUpdateActionButton();
            }
            RelativeLayout chatView = (RelativeLayout) getView().findViewById(
                    R.id.layout_places_chat);
            chatView.setVisibility(View.GONE);
            if (getCaller().contentEquals("postable_venues") == true) {
                chatView.setVisibility(View.VISIBLE);
            }
            EditText editText = (EditText) getView().findViewById(
                    R.id.edittext_places_chat);
            if (getCaller().contentEquals("pen_button")
                    || getCaller().contentEquals("postable_venues")
                    || getCaller().contentEquals("question_button")) {
                showKeyboard();
            } else {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager inputManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null && editText != null) {
                    inputManager.hideSoftInputFromWindow(
                            editText.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null && v != null) {
                            inputManager.hideSoftInputFromWindow(
                                    v.getWindowToken(),
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
                    case Executor.HTTP_ERROR_IN_MORE_FEED:
                        onErrorReceived();
                        new CustomDialog(getActivity(), "Error", result.getResponseMessage())
                                .show();
                        if (adapter != null) {
                            adapter.setLastViewed(0);
                        }
                        break;
                    case Executor.HANDLE_VENUE_FEED:
                    case Executor.HANDLE_VENUE_MORE_FEED:                       
                        if (result != null
                                && result.getObject() != null
                                && (result.getObject() instanceof VenueNameAndFeeds)) {
                            venueNameAndFeeds = (VenueNameAndFeeds) result
                                    .getObject();

                            populateList();
                        }
                        break;
                    case Executor.HANDLE_GET_QUESTIONS_RECEIVERS:
                        if (result != null
                                && result.getObject() != null) {
                            Object[] obj = (Object[]) result.getObject();
                            @SuppressWarnings("unchecked")
                            String input = (String) obj[1];
                            int count = ((Integer)obj[0]).intValue();
                            showDialogQuestionTo(input, count);
                        }
                        break;
                    case Executor.HANDLE_SEND_VENUE_FEED:
                        // refresh the list
                        EditText editText = (EditText) getView().findViewById(
                                R.id.edittext_places_chat);
                        editText.setText("");
                        exe.venueFeeds(venueId, venueName, lastChatIDString,
                                "", false, true, "");
                        break;
                    case Executor.HANDLE_SENDING_PLUS_ONE:
                        boolean res = (Boolean) result.getObject();
                        String message;
                        if (!res) {
                            message = "+1 Sent.";
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)
                            .show();
                            TextView counter = exe.getCounter();
                            View buttonArea = (View) counter.getParent();
                            int count = 1;
                            if (counter != null && counter.getText() != "") {
                                count = Integer.parseInt((String) counter.getText());
                                count++;
                            } else {
                                ImageView pill_button_plus1_comment_right = (ImageView) buttonArea
                                        .findViewById(R.id.pill_button_plus1_comment_right);
                                int pixels62 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                                        62, buttonArea.getResources().getDisplayMetrics()));
                                int pixels22 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                                        22, buttonArea.getResources().getDisplayMetrics()));
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                        pixels62,
                                        pixels22);
                                lp.setMargins(0, 0, 0, 0);
                                pill_button_plus1_comment_right.setLayoutParams(lp);

                            }
                            counter.setText(count+"");
                        } else {
                            new CustomDialog(getActivity(), "Error",
                                    result.getResponseMessage()).show();
                            }
                        break;
                    
                    }
                }
            });

            // ListView with chat entries
            list = (ListView) getView().findViewById(R.id.listview_places_chat);
            list.setItemsCanFocus(true);
            list.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            ((EditText) getView().findViewById(R.id.edittext_places_chat))
                    .setImeOptions(EditorInfo.IME_ACTION_SEND);
            ((EditText) getView().findViewById(R.id.edittext_places_chat))
                    .setOnEditorActionListener(new OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId,
                                KeyEvent event) {

                            if (actionId == EditorInfo.IME_NULL
                                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                    && event.getAction() == KeyEvent.ACTION_DOWN) {
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
        ((Button) (getActivity()).findViewById(R.id.btn_top_refresh))
                .setVisibility(View.GONE);
        ((Button) (getActivity()).findViewById(R.id.btn_top_cancel))
                .setVisibility(View.VISIBLE);

    }

    public void hideRefreshButton() {
        ((Button) (getActivity()).findViewById(R.id.btn_top_refresh))
                .setVisibility(View.GONE);
        ((Button) (getActivity()).findViewById(R.id.btn_top_cancel))
                .setVisibility(View.VISIBLE);
    }

    public void hideActionButton(int showHide) {
        ImageView plus = (ImageView) (getActivity())
                .findViewById(R.id.imageview_button_plus);
        ImageView minus = (ImageView) (getActivity())
                .findViewById(R.id.imageview_button_minus);

        RelativeLayout rel_map = (RelativeLayout) (getActivity())
                .findViewById(R.id.rel_map);
        RelativeLayout rel_feed = (RelativeLayout) (getActivity())
                .findViewById(R.id.rel_feed);
        RelativeLayout rel_places = (RelativeLayout) (getActivity())
                .findViewById(R.id.rel_places);
        RelativeLayout rel_people = (RelativeLayout) (getActivity())
                .findViewById(R.id.rel_people);
        RelativeLayout rel_contacts = (RelativeLayout) (getActivity())
                .findViewById(R.id.rel_contacts);

        minus.setVisibility(View.GONE);
        plus.setVisibility(showHide);
        rel_map.setVisibility(showHide);
        rel_feed.setVisibility(showHide);
        rel_places.setVisibility(showHide);
        rel_people.setVisibility(showHide);
        rel_contacts.setVisibility(showHide);
    }

    public void removeReplyArea() {
        RelativeLayout replyArea = (RelativeLayout) getView().findViewById(
                R.id.reply_item_area);
        if (replyArea != null){
            EditText editText = (EditText) replyArea.findViewById(
                    R.id.input_in_reply);
            
            
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            LinearLayout replyAreaParent = (LinearLayout) replyArea.getParent();
            replyAreaParent.removeView(replyArea);

        }
    }

    public void hideQuestionUpdateActionButton() {
        RelativeLayout chatView = (RelativeLayout) getView().findViewById(
                R.id.layout_places_chat);
        chatView.setVisibility(View.GONE);
        showRefreshButton();

    }

    public void showRefreshButton() {
        ((Button) (getActivity()).findViewById(R.id.btn_top_refresh))
                .setVisibility(View.VISIBLE);
        ((Button) (getActivity()).findViewById(R.id.btn_top_cancel))
                .setVisibility(View.GONE);

    }

    public void showKeyboard() {
        if (getCaller().contentEquals("pen_button")
                || getCaller().contentEquals("postable_venues")
                || getCaller().contentEquals("question_button")) {

            RelativeLayout chatView = (RelativeLayout) getView().findViewById(
                    R.id.layout_places_chat);
            chatView.setVisibility(View.VISIBLE);
            final EditText editText = (EditText) getView().findViewById(
                    R.id.edittext_places_chat);
            editText.requestFocus();

            editText.postDelayed(new Runnable() {

                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(editText,
                            InputMethodManager.SHOW_IMPLICIT);
                }
            }, 1000);
        }
    }

    private void showDialogQuestionTo(final String input, int numberOfUsersHere) {
        // custom dialog

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    hideQuestionUpdateActionButton();
                    setCaller("feeds_list");
                    ((ActivityVenueFeeds) getActivity())
                            .changeIntentCaller("feeds_list");
                    exe.venueFeeds(venueId, venueName, "0", input, true, true,
                            messageType);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
                }
            }
        };
        
        String mess;
        if (numberOfUsersHere == 1) {
            mess = (String) getResources().getText(
                    R.string.dialog_post_a_question_content_1);
        } else if (numberOfUsersHere > 1) {
            mess = String.format(
                    (String) getResources().getText(
                            R.string.dialog_post_a_question_content_2),
                    numberOfUsersHere);
        } else {
            mess = (String) getResources().getText(
                    R.string.dialog_post_a_question_content_0);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((String) getResources().getText(
                R.string.dialog_post_a_question_title));
        builder.setMessage(mess).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void populateList() {
        if (getActivity() == null) {
            return;
        }
        Log.d("Fragment Feeds", "venueNameAndFeeds messages length..." + venueNameAndFeeds.getFeedsArray().size());
        if (adapter == null) {
            if (AppCAP.getLocalUserPhotoURL().length() > 5) {
                this.userProfileImage = (ImageView) getView().findViewById(
                        R.id.imageview_user_image);
                imageLoader.DisplayImage(AppCAP.getLocalUserPhotoURL(),
                        userProfileImage, R.drawable.default_avatar25, 70);
            }
            adapter = new MyFeedsAdapter(getActivity(), venueNameAndFeeds, this);
            list.setAdapter(adapter);
        } else {
            adapter.setNewData(venueNameAndFeeds);
            adapter.notifyDataSetChanged();
        }
        progress.dismiss();
        showKeyboard();
    }

    public void onClickOpenVenueFeeds(View v) {
        // nothing to do in this Activity
    }

    public void SendEntry(TextView editText) {
        String input = editText.getText().toString();

        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (input != null && input.length() > 0) {
            if (messageType == Feed.FEED_TYPE_QUESTION) {
                exe.getQuestionReceivers(input, AppCAP.getUserLatLon());
            } else {
                hideQuestionUpdateActionButton();
                setCaller("feeds_list");
                ((ActivityVenueFeeds) getActivity())
                        .changeIntentCaller("feeds_list");
                exe.venueFeeds(venueId, venueName, "0", input, true, true,
                        messageType);
            }
        }
    }

    public void SendComment(TextView editText, int original_post_id) {
        String input = editText.getText().toString();

        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                .getWindowToken(), 0);
        if (input != null && input.length() > 0) {
                ((ActivityVenueFeeds) getActivity())
                        .changeIntentCaller("feeds_list");
                exe.newPost(venueId, venueName, "0", input, true,
                        "update", original_post_id);
        }
    }
    
    public void onClickPlusOneOrComment(View v) {
        if (!AppCAP.isUserCheckedIn()) {
            showDialogCheckin();
        } else {
            showDialogPlusOneOrComment(v);
        }
    }

    public void showDialogCheckin() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_be_checked_in);

        Button dialog_btn_checkin = (Button) dialog.findViewById(R.id.btn_ok);
        dialog_btn_checkin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogPlusOneOrComment(View v) {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_plusone_or_comment);
        dialog.setCanceledOnTouchOutside(true);
        final TextView counter = (TextView) v.findViewById(R.id.item_venue_feeds_plus_one_counter);
        LinearLayout plusone_area = (LinearLayout) dialog
                .findViewById(R.id.plusone_area);
        final int feed_id = (Integer) v.getTag(R.id.tag_feed_id);
        final String  feed_type = (String) v.getTag(R.id.tag_feed_type);
        final int feed_by_me = (Integer) v.getTag(R.id.tag_feed_by_me);
        
        ImageView imageview_plusone = (ImageView) plusone_area.findViewById(R.id.imageview_plusone);
        if (feed_by_me == 1){
            if (imageview_plusone.getBackground() != null) {
                imageview_plusone.getBackground().setAlpha(45);
            }
        } else {
            if (imageview_plusone.getBackground() != null) {
                imageview_plusone.getBackground().setAlpha(255);
            }
        }
        if (counter != null && counter.getText() != "") {
            TextView textViewNoOne = (TextView) plusone_area.findViewById(R.id.textViewNoOne);
            TextView textViewHas = (TextView) plusone_area.findViewById(R.id.textViewHas);
            CharSequence counterText = counter.getText();
            if (counterText.equals("1")){
                textViewNoOne.setText("one person");
            } else {
                textViewNoOne.setText(counterText + " people");
                textViewHas.setText(" have +1'd this");
            }
        }
        plusone_area.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                exe.setCounter(counter);
                exe.sendPlusOneForLove(feed_id);
            }
        });

        LinearLayout comment_area = (LinearLayout) dialog
                .findViewById(R.id.comment_area);
        comment_area.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                hideRefreshButton();
                addNewComment(counter, feed_id, feed_type);
            }
        });

        dialog.show();
    }
    
    public void addNewComment(TextView counter, final int feed_id, final String feed_type) {
        // search the comment area
        View reply_item_area = this.getView().getRootView().findViewById(R.id.reply_item_area); 
        if (reply_item_area != null){
            return;
        }
        View buttonArea = (View) counter.getParent();
        if (buttonArea != null) {
            View feedArea = (View) buttonArea.getParent();
            if (feedArea != null){
                LinearLayout reply_area = (LinearLayout) feedArea.findViewById(R.id.item_venue_feeds_reply_listview); 
                
                int pixels10 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                        10, getResources().getDisplayMetrics()));
                int pixels5 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                        5, getResources().getDisplayMetrics()));
                int pixels30 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                        50, getResources().getDisplayMetrics()));
                int pixels50 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                        50, getResources().getDisplayMetrics()));
                RelativeLayout layoutForInflateCategoryHeader = new RelativeLayout(getActivity());
                LayoutParams lpRow = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                layoutForInflateCategoryHeader.setLayoutParams(lpRow); 
                layoutForInflateCategoryHeader.setId(R.id.reply_item_area);
                  // user image
                ImageView image = new ImageView(getActivity());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(pixels50, pixels50);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                image.setLayoutParams(lp); 
                image.setId(R.id.user_image_in_reply);
                image.setPadding(pixels10, pixels5, pixels10, pixels10);
                ImageLoader imageLoader = new ImageLoader(getActivity());
                imageLoader.DisplayImage(AppCAP.getLocalUserPhotoURL(), image,
                        R.drawable.default_avatar50, 30);
                layoutForInflateCategoryHeader.addView(image);
                  // user image mask
                ImageView imageM = new ImageView(getActivity());
                imageM.setLayoutParams(lp);          
                imageM.setPadding(pixels10, pixels5, pixels10, pixels10);
                Drawable drawable = getResources().getDrawable(
                          R.drawable.user_image_mask);
                imageM.setImageDrawable(drawable);
                layoutForInflateCategoryHeader.addView(imageM);
                
                  // Set text
                TextView catName = new TextView(getActivity());
                if (feed_type.contentEquals("question")){
                    catName.setText("Answer:");
                } else {
                    catName.setText("Reply:");
                }
                RelativeLayout.LayoutParams lptext = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                        LayoutParams.WRAP_CONTENT);
                lptext.addRule(RelativeLayout.RIGHT_OF, R.id.user_image_in_reply);
                catName.setId(R.id.input_title_in_reply);
                catName.setLayoutParams(lptext); 
                catName.setTextColor(Color.parseColor("#009590"));
                catName.setPadding(0, pixels5, 0, 0);
                layoutForInflateCategoryHeader.addView(catName);

                final EditText editText = new EditText(getActivity());
                RelativeLayout.LayoutParams lpinput = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
                        LayoutParams.WRAP_CONTENT);
                lpinput.addRule(RelativeLayout.RIGHT_OF, R.id.input_title_in_reply);
                editText.setBackgroundDrawable(null);
                editText.setGravity(Gravity.LEFT | Gravity.TOP);
                editText.setImeActionLabel("Send",  EditorInfo.IME_ACTION_SEND );
                editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
                editText.setId(R.id.input_in_reply);
                editText.setSingleLine(false);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.setLayoutParams(lpinput); 
                layoutForInflateCategoryHeader.addView(editText);
               
                reply_area.addView(layoutForInflateCategoryHeader);
                reply_area.setVisibility(View.VISIBLE);
                
                editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        });
                    }
                });
                editText.setOnEditorActionListener(new OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {

                        if (actionId == EditorInfo.IME_NULL
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                && event.getAction() == KeyEvent.ACTION_DOWN) {
                            SendComment(v, feed_id);
                            showRefreshButton();
                            return true;
                        }
                        return false;
                    }
                });
                
                editText.requestFocus();
            }
        }
    }

    public void onClickSend(View v) {
        EditText editText = (EditText) getView().findViewById(
                R.id.edittext_places_chat);
        SendEntry(editText);
    }


    public void onClickCancel(View v) {
        removeReplyArea();
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
            if (getActivity().getWindow().findViewById(
                    R.id.textview_contact_list) != null) {
                ((CustomFontView) getActivity().getWindow().findViewById(
                        R.id.textview_contact_list)).setText(venueName);
            }
            // Get venue chat
            venueFeeds();
        }
    }
    
    public void venueFeeds() {
        if (venueNameAndFeeds != null &&
                venueNameAndFeeds.getVenueId() == venueId && 
                adapter != null) {
            adapter.setNewData(venueNameAndFeeds);
            adapter.notifyDataSetChanged();
            
        } else {
            if (adapter != null) {
                adapter.setLastViewed(0);
            }
            if (list != null) {
                list.setSelection(0);
            }
            exe.venueFeeds(venueId, venueName, lastChatIDString, "", false,
                true, "");
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
        CustomFontView textUpdateOrQuestion = (CustomFontView) getView()
                .findViewById(R.id.textview_update);
        if (getCaller().contentEquals("question_button") == true) {
            textUpdateOrQuestion.setText((String) getResources().getText(
                    R.string.activityFeedsQuestionText));
            messageType = Feed.FEED_TYPE_QUESTION;
            hideRefreshButton();
        } else if (getCaller().contentEquals("pen_button") == true) {
            textUpdateOrQuestion.setText((String) getResources().getText(
                    R.string.activityFeedsUpdateText));
            messageType = Feed.FEED_TYPE_UPDATE;
            showUpdateActionButton();
        } else {
            textUpdateOrQuestion.setText((String) getResources().getText(
                    R.string.activityFeedsUpdateText));
            messageType = Feed.FEED_TYPE_UPDATE;
            hideQuestionUpdateActionButton();
        }
        RelativeLayout chatView = (RelativeLayout) getView().findViewById(
                R.id.layout_places_chat);
        chatView.setVisibility(View.GONE);
        if (getCaller().contentEquals("postable_venues") == true) {
            chatView.setVisibility(View.VISIBLE);
        }
        if (getCaller().contentEquals("pen_button")
                || getCaller().contentEquals("postable_venues")
                || getCaller().contentEquals("question_button")) {
            showKeyboard();
        } else {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            EditText editText = (EditText) getView().findViewById(
                    R.id.edittext_places_chat);
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null && editText != null) {
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        if (getActivity().getWindow().findViewById(R.id.textview_contact_list) != null) {
            ((CustomFontView) getActivity().getWindow().findViewById(
                    R.id.textview_contact_list)).setText(venueName);
        }
        venueFeeds();
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

    public void getMoreFeeds() {
        exe.getMoreFeeds(venueNameAndFeeds);
    }

}
