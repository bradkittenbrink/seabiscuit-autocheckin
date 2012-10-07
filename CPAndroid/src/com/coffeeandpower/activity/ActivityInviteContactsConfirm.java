package com.coffeeandpower.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.linkedin.LinkedIn;
import com.coffeeandpower.linkedin.LinkedInInitException;
import com.coffeeandpower.views.CustomDialog;

public class ActivityInviteContactsConfirm extends RootActivity {

    
    private String inviteCodeText;
    private List<String> arraySelectedUsersIds;
    private String title;
    private String messageContent;
    ProgressDialog progress;
    private DataHolder result;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts_confirm);
        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            inviteCodeText = extras.getString("inviteCodeText");
            arraySelectedUsersIds = extras.getStringArrayList("arraySelectedUsersIds");
        }

        String escapedUsername = TextUtils.htmlEncode(AppCAP
               .getLoggedInUserNickname());

        Resources res = getResources();
        String text = String.format(res.getString(R.string.activity_invite_confirm_header), escapedUsername);
        CharSequence styledText = Html.fromHtml(text);
        title = styledText.toString();
        ((TextView) findViewById(R.id.confirm_header)).setText(styledText);
        text = String.format(res.getString(R.string.activity_invite_confirm_content), escapedUsername, inviteCodeText);
        styledText = Html.fromHtml(text);
        messageContent = styledText.toString();
        ((TextView) findViewById(R.id.confirm_content)).setText(styledText);

    }
    
    public void SendProgressHandler() {
        progress = new ProgressDialog(this);
        progress.setOwnerActivity(this);
        progress.setMessage("Sending...");
        progress.show();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AppCAP.setShouldFinishActivities(false);
            progress.dismiss();

            switch (msg.what) {
            case 0:
                Toast.makeText(ActivityInviteContactsConfirm.this, "Invitation sent!",
                        Toast.LENGTH_LONG).show();
                finish();
                startSmartActivity(new Intent(), "ActivityMap");
                break;

            default:
                new CustomDialog(ActivityInviteContactsConfirm.this, "Error", result.getResponseMessage()).show();
                break;

            }
        }

    };

    public void onClickSend(View v) {
        // Display image
        if (AppCAP.isLoggedIn()) {
            SendProgressHandler();
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LinkedIn lastAuthorize = new LinkedIn();
                    try {
                        lastAuthorize.initialize(
                                (String) getResources().getText(R.string.linkedInApiKey),
                                (String) getResources().getText(R.string.linkedInApiSec));
                    } catch (LinkedInInitException e) {
                        final String errorMsg = getString(R.string.message_internet_connection_error);
                        final int errorCode = -1;
                        result = new DataHolder(errorCode, errorMsg, null);
                        handler.sendEmptyMessage(result.getResponseCode());
                    }
                    result = lastAuthorize.sendInvite(AppCAP.getUserLinkedInToken(),
                            AppCAP.getUserLinkedInTokenSecret(),
                            arraySelectedUsersIds,
                            title,
                            messageContent);
                    handler.sendEmptyMessage(result.getHandlerCode());
                }
            }, "ActivityInviteContactsConfirm").start();
            
            
        } else {
            showDialog(DIALOG_MUST_BE_A_MEMBER);
        }

    }

    public void onClickCancel(View v) {
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
        } 
    }


}
