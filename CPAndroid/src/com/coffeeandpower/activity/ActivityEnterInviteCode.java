package com.coffeeandpower.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

public class ActivityEnterInviteCode extends RootActivity {

    private DataHolder result;

    private Executor exe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_invite_code);

        // Executor
        exe = new Executor(ActivityEnterInviteCode.this);
        exe.setExecutorListener(new ExecutorInterface() {
            @Override
            public void onErrorReceived() {
                errorReceived();
            }

            @Override
            public void onActionFinished(int action) {
                actionFinished(action);
            }
        });
        TextView instructionsTextView = (TextView) findViewById(R.id.instructions_text);
        if (AppCAP.getEnteredInviteCode()) {
            instructionsTextView
                    .setText("This invite code will allow someone who is near you right now to join C&P");
            // Go grab the invitation code
            exe.getInvitationCode(AppCAP.getUserLatLon());
        } else {
            // If the user doesn't have an invite code the activity can come
            // from either the login
            // sequence or the settings view so we will always just show the
            // later button to handle
            // both cases
            
            ImageButton btn_linked_in_invite_list = (ImageButton) findViewById(R.id.btn_linked_in_invite_list);
            btn_linked_in_invite_list.setVisibility(View.GONE);
            Button settingsBtn = ((Button) findViewById(R.id.settings_btn));
            settingsBtn.setVisibility(View.GONE);
            Button laterBtn = ((Button) findViewById(R.id.later_btn));
            laterBtn.setVisibility(View.VISIBLE);
            
            TextView instructions_invite_text = (TextView) findViewById(R.id.instructions_invite_text);
            instructions_invite_text.setText("If another C&P user has given you an invite code, type it in above");
            instructionsTextView.setText("");
            ((EditText) findViewById(R.id.edit_text))
                    .setOnEditorActionListener(new OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId,
                                KeyEvent event) {

                            if (actionId == EditorInfo.IME_NULL && 
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER && 
                                    event.getAction() == KeyEvent.ACTION_DOWN) {
                                exe.enterInvitationCode(
                                        ((EditText) findViewById(R.id.edit_text))
                                                .getText().toString(), AppCAP
                                                .getUserCoordinates()[4],
                                        AppCAP.getUserCoordinates()[5]);
                            }
                            return false;
                        }
                    });
        }
    }

    public void onClickBack(View v) {
        AppCAP.setNotFirstStart();
        onBackPressed();
    }

    public void onClickLater(View v) {
        AppCAP.setLoggedIn(false);
        AppCAP.setShouldFinishActivities(false);
        startSmartActivity(new Intent(), "ActivityMap");
    }

    private void errorReceived() {

    }

    public void onClickLinkedInInviteList(View v) {
        TextView inviteCodeText = (TextView) findViewById(R.id.edit_text);
        Intent intent = new Intent(this, ActivityInviteContacts.class);
        intent.putExtra("inviteCodeText", inviteCodeText.getText().toString());
        startActivity(intent);
    }
    

    private void actionFinished(int action) {
        result = exe.getResult();

        switch (action) {

        case Executor.HANDLE_ENTER_INVITATION_CODE: {
            onBackPressed();
        }
        case Executor.HANDLE_GET_INVITATION_CODE: {
            TextView inviteCodeText = (TextView) findViewById(R.id.edit_text);
            if (result != null && result.getObject() != null) {
                // Grab the string out of the DataHolder
                String inviteCode = (String) result.getObject();
                inviteCodeText.setText(inviteCode);
                inviteCodeText.setEnabled(false);
            }
        }

            break;

        }
    }

}
