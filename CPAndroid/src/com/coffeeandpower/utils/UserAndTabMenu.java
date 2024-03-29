package com.coffeeandpower.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityInviteContacts;
import com.coffeeandpower.activity.ActivityLoginPage;
import com.coffeeandpower.activity.ActivityNotifications;
import com.coffeeandpower.activity.ActivitySettings;
import com.coffeeandpower.activity.ActivitySupport;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.tab.activities.ActivityCheckInList;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.views.CustomDialog;

public class UserAndTabMenu implements UserMenu, TabMenu {

    public static final int HANDLE_CHECK_OUT = 1800;
    public static final int HANDLE_LOG_OUT = 1801;
    public static final int HANDLE_GET_NOTIFICATION_SETTINGS = 1802;

    private ProgressDialog progress;
    private ToggleButton toggle;
    private Button btnFrom;

    private RootActivity context;
    private DataHolder result;
    private DataHolder resultNotificationSettings;

    public interface OnUserStateChanged {
        public void onCheckOut();

        public void onLogOut();
    }

    OnUserStateChanged userState = new OnUserStateChanged() {
        @Override
        public void onCheckOut() {
        }

        public void onLogOut() {
        }
    };

    public void setOnUserStateChanged(OnUserStateChanged userState) {
        this.userState = userState;
    }

    public UserAndTabMenu(RootActivity context) {
        this.context = context;
        this.progress = new ProgressDialog(context);

        // If user is not logged in
        if (!AppCAP.isLoggedIn()) {
            View v = ((Activity) context).findViewById(R.id.btn_menu);
            RelativeLayout r = (RelativeLayout) ((Activity) context)
                    .findViewById(R.id.rel_log_in);
            RelativeLayout r1 = (RelativeLayout) ((Activity) context)
                    .findViewById(R.id.rel_contacts);

            if (v != null) {
                v.setVisibility(View.GONE);
            }
            if (r != null) {
                r.setVisibility(View.VISIBLE);
            }
            if (r1 != null) {
                r1.setVisibility(View.GONE);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            progress.dismiss();

            switch (msg.what) {
            case AppCAP.HTTP_ERROR:
                new CustomDialog(context,
                        context.getString(R.string.error),
                        result.getResponseMessage())
                        .show();
                break;

            case HANDLE_CHECK_OUT:
                CacheMgrService.checkOutTrigger();
                userState.onCheckOut();
                displayActionButton();
                break;

            case HANDLE_GET_NOTIFICATION_SETTINGS:
                if (resultNotificationSettings.getObject() != null
                        && resultNotificationSettings.getObject() instanceof Object[]) {
                    Object[] obj = (Object[]) resultNotificationSettings
                            .getObject();

                    String pushDistance = (String) obj[0];
                    String checkedInOnly = (String) obj[1];

                    AppCAP.setPushDistance(pushDistance);
                    AppCAP.setNotificationToggle(checkedInOnly.equals("1"));

                    if (toggle != null && btnFrom != null) {
                        if (Constants.debugLog)
                            Log.d("LOG", "text: " + pushDistance + ":"
                                    + checkedInOnly);
                        toggle.setChecked(checkedInOnly.matches("1"));
                        btnFrom.setText(pushDistance.matches("venue") ? "in venue"
                                : "in city");
                    }
                }
                break;

            }
        }

    };

    @Override
    public void onClickMap(View v) {
        ((RootActivity) context).startSmartActivity(new Intent(), "ActivityMap");
    }
    

    @Override
    public void onClickPlaces(View v) {
        double[] data = new double[6];
        data = AppCAP.getUserCoordinates();

        Intent intent = new Intent(context, ActivityVenueFeeds.class);
        intent.putExtra("sw_lat", data[0]);
        intent.putExtra("sw_lng", data[1]);
        intent.putExtra("ne_lat", data[2]);
        intent.putExtra("ne_lng", data[3]);
        intent.putExtra("user_lat", data[4]);
        intent.putExtra("user_lng", data[5]);
        intent.putExtra("from", "from_tab");
        intent.putExtra("type", "place");
        intent.putExtra("fragment", "FragmentPlaces");
        ((RootActivity) context).startSmartActivity(intent, "ActivityPeopleAndPlaces");

    }
    
    @Override
    public void onClickMapFromTab(View v) {
        double[] data = new double[6];
        data = AppCAP.getUserCoordinates();

        Intent intent = new Intent(context, ActivityVenueFeeds.class);
        intent.putExtra("sw_lat", data[0]);
        intent.putExtra("sw_lng", data[1]);
        intent.putExtra("ne_lat", data[2]);
        intent.putExtra("ne_lng", data[3]);
        intent.putExtra("user_lat", data[4]);
        intent.putExtra("user_lng", data[5]);
        intent.putExtra("from", "from_tab");
        ((RootActivity) context).startSmartActivity(intent, "ActivityMap");

    }

    @Override
    public void onClickCheckIn(View v) {
        hideVerticalMenu(v);
        if (AppCAP.isUserCheckedIn()) {
            Toast.makeText(context, "You will be checked out from your current venue!",
                    Toast.LENGTH_SHORT).show();
        } 
        double[] data = new double[6];
        data = AppCAP.getUserCoordinates();

        double userLat = data[4];
        double userLng = data[5];

        if (userLat != 0 && userLng != 0) {
            Intent intent = new Intent(context, ActivityCheckInList.class);
            context.startActivity(intent);
        } else {
            ((RootActivity) context).startSmartActivity(new Intent(), "ActivityMap");

            Intent intent2 = new Intent(context, ActivityCheckInList.class);
            context.startActivity(intent2);
        }
        
    }
    
    
    @Override
    public void onClickCheckOut(final View v) { 
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle("Are you sure?");
        builder.setMessage(context.getResources().getString(R.string.checked_out_confirmation))
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                                onClickCheckOut(v, null);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickCheckOut(View v, final Activity finishActivity) { 
        if (v != null) {
            hideVerticalMenu(v);
        }
        if (AppCAP.isUserCheckedIn()) {
            progress.setMessage("Checking out...");
            progress.show();
            AppCAP.setUserCheckedIn(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                            result = AppCAP.getConnection()
                                    .checkOut();
                            handler.sendEmptyMessage(result
                                    .getHandlerCode());
                            if (finishActivity != null) {
                                finishActivity.finish();
                            }
                }
            },"UserAndTabMenu.onClickCheckOut").start();
        }
    }

    @Override
    public void onClickPeople(View v) {
        double[] data = new double[6];
        data = AppCAP.getUserCoordinates();

        Intent intent = new Intent(context, ActivityVenueFeeds.class);
        intent.putExtra("sw_lat", data[0]);
        intent.putExtra("sw_lng", data[1]);
        intent.putExtra("ne_lat", data[2]);
        intent.putExtra("ne_lng", data[3]);
        intent.putExtra("user_lat", data[4]);
        intent.putExtra("user_lng", data[5]);
        intent.putExtra("from", "from_tab");
        intent.putExtra("type", "people");
        intent.putExtra("fragment", "FragmentPeople");
        ((RootActivity) context).startSmartActivity(intent, "ActivityPeopleAndPlaces");
    }

    @Override
    public void onClickContacts(View v) {
        Intent intent = new Intent(context, ActivityVenueFeeds.class);
        intent.putExtra("fragment", "FragmentContacts");
        ((RootActivity) context).startSmartActivity(intent, "ActivityContacts");
    }

    @Override
    public void onClickEnterInviteCode(View v) {
        Intent intent = new Intent(context, ActivityInviteContacts.class);
        context.startActivity(intent);
    }

    @Override
    public void onClickSettings(View v) {
        Intent intent = new Intent(context, ActivitySettings.class);
        context.startActivity(intent);
    }
    
    @Override
    public void onClickNotifications(View v) {
        Intent intent = new Intent(context, ActivityNotifications.class);
        context.startActivity(intent);
    }

    @Override
    public void onClickSupport(View v) {
        Intent intent = new Intent(context, ActivitySupport.class);
        context.startActivity(intent);
    }

    @Override
    public void onClickLogout(View v) {
        AppCAP.setLoggedInUserId(0);
        AppCAP.setLocalUserPhotoLargeURL("");
        AppCAP.setLocalUserPhotoURL("");
        AppCAP.setLoggedInUserNickname("");
        AppCAP.setShouldFinishActivities(false);
        AppCAP.setStartLoginPageFromContacts(false); 
        AppCAP.setUserLinkedInDetails("", "", "");
        AppCAP.setUserEmail("");
        userState.onLogOut();

    }

    @Override
    public void onClickPlus(View v) {
        ImageView plus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_plus);
        ImageView minus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_minus);
        LinearLayout layout_action_menu = (LinearLayout) ((Activity) context)
                .findViewById(R.id.layout_action_menu);

        layout_action_menu.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(0, 0, 170, -60);   
        animation.setDuration(400);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        ButtonAnimationListener listener=new ButtonAnimationListener(layout_action_menu, 56,((Activity) context));
        animation.setAnimationListener(listener);
        
        layout_action_menu.startAnimation(animation);        
        plus.startAnimation(AnimationUtils.loadAnimation((Activity) context, R.anim.rotate_indefinitely_plus)); 
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView plus = (ImageView) ((Activity) context)
                        .findViewById(R.id.imageview_button_plus);
                ImageView minus = (ImageView) ((Activity) context)
                        .findViewById(R.id.imageview_button_minus);
                plus.clearAnimation();
                if (plus != null) {
                    plus.setVisibility(View.GONE);
                }
                if (minus != null) {
                    minus.setVisibility(View.VISIBLE);
                }       
            }
        }, 400);
    }

    @Override
    public void onClickMinus(View v) {
        ImageView plus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_plus);
        ImageView minus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_minus); 
        LinearLayout layout_action_menu = (LinearLayout) ((Activity) context)
                .findViewById(R.id.layout_action_menu);  
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 340); 
        animation.setDuration(400);
        animation.setFillAfter(true);
        ButtonAnimationListener listener=new ButtonAnimationListener(layout_action_menu, 0,((Activity) context));
        animation.setAnimationListener(listener);
        layout_action_menu.startAnimation(animation);        
        minus.startAnimation(AnimationUtils.loadAnimation((Activity) context, R.anim.rotate_indefinitely_minus));
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView plus = (ImageView) ((Activity) context)
                        .findViewById(R.id.imageview_button_plus);
                ImageView minus = (ImageView) ((Activity) context)
                        .findViewById(R.id.imageview_button_minus);
                LinearLayout layout_action_menu = (LinearLayout) ((Activity) context)
                        .findViewById(R.id.layout_action_menu);
                layout_action_menu.clearAnimation();
                minus.clearAnimation();
                displayActionButton();       
                layout_action_menu.setVisibility(View.GONE);
            }
        }, 400);
    }
    
    
    public void displayActionButton() {
        ImageView plus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_plus);
        ImageView minus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_minus);
        ImageView checkin = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_checkin); 
        if (minus != null) {
            minus.setVisibility(View.GONE); 
        }
        if (AppCAP.isUserCheckedIn()) {
            if (plus != null) {
                plus.setVisibility(View.VISIBLE); 
            }
            if (checkin != null) {
                checkin.setVisibility(View.GONE);
            }       
        } else {
            if (plus != null) {
                plus.setVisibility(View.GONE); 
            }
            if (checkin != null) {
                checkin.setVisibility(View.VISIBLE);
            }       
        }

    }
    public void hideVerticalMenu(View v) {
        ImageView minus = (ImageView) ((Activity) context)
                .findViewById(R.id.imageview_button_minus); 
        if (minus != null && minus.isShown() == true){
            onClickMinus(v);
        }
    }
    

    private void showDialogLogin() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login);

        Button dialog_btn_login = (Button) dialog.findViewById(R.id.btn_login);
        dialog_btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent();
                i.setClass(context, ActivityLoginPage.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        });



        dialog.show();
    }

    public void showDialogCheckin() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_checkin);

        Button dialog_btn_checkin = (Button) dialog.findViewById(R.id.btn_checkin);
        dialog_btn_checkin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onClickCheckIn(v);
            }
        });
        dialog.show();
    }

}
