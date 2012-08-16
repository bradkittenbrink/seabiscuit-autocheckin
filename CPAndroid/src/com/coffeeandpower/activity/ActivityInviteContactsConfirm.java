package com.coffeeandpower.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.linkedin.LinkedIn;
import com.coffeeandpower.views.CustomFontView;

public class ActivityInviteContactsConfirm extends RootActivity {

    
    private String inviteCodeText;
    private List<String> arraySelectedUsersIds;
    private String title;
    private String messageContent;


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
        ((CustomFontView) findViewById(R.id.confirm_header)).setText(styledText);
        text = String.format(res.getString(R.string.activity_invite_confirm_content), escapedUsername, inviteCodeText);
        styledText = Html.fromHtml(text);
        messageContent = styledText.toString();
        ((CustomFontView) findViewById(R.id.confirm_content)).setText(styledText);

    }

    public void onClickSend(View v) {
        // Display image
        if (AppCAP.isLoggedIn()) {
            LinkedIn lastAuthorize = new LinkedIn();
            lastAuthorize.initialize(
                    (String) getResources().getText(R.string.linkedInApiKey),
                    (String) getResources().getText(R.string.linkedInApiSec));
            lastAuthorize.sendInvite(AppCAP.getUserLinkedInToken(),
                    AppCAP.getUserLinkedInTokenSecret(),
                    arraySelectedUsersIds,
                    title,
                    messageContent);
            AppCAP.setShouldFinishActivities(false);
            this.finish();
            startSmartActivity(new Intent(), "ActivityMap");
            
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
