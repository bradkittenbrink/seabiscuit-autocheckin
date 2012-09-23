package com.coffeeandpower.activity;

import java.util.ArrayList;

import org.scribe.model.Token;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.LinkedInSkillsAdapter;
import com.coffeeandpower.adapters.LinkedInUsersAdapter;
import com.coffeeandpower.adapters.MyChatAdapter;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserLinkedinSkills;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.linkedin.LinkedIn;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;

public class ActivityLinkedinSkills extends RootActivity {

    private static final int SCREEN_USER = 1;

    private HorizontalPagerModified pager;

    private LinkedInSkillsAdapter adapterUsers;


    private DataHolder result;
    private Executor exe;

    private ListView listView;

    private ArrayList<UserLinkedinSkills> skills;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_skills);

        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP
                .getLoggedInUserNickname());

        // Horizontal Pager
        pager = (HorizontalPagerModified) findViewById(R.id.pager);
        pager.setCurrentScreen(SCREEN_USER, false);
        // Executor
        exe = new Executor(ActivityLinkedinSkills.this);
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
        // Display the list of users if the user is logged in
        listView = (ListView) findViewById(R.id.linkedin_users_listview);

    }
    private void errorReceived() {

    }

    private void actionFinished(int action) {
        result = exe.getResult();

        switch (action) {

        case Executor.HANDLE_USER_LINKEDIN_SKILLS:
            if (result.getObject() instanceof ArrayList<?>) {
                ArrayList<UserLinkedinSkills> skills = (ArrayList<UserLinkedinSkills>) result
                        .getObject();
                displaySkills(skills);
            }
            break;
        }
    }
    
    public void onBackPressed() {
        UserLinkedinSkills firstSkill = this.skills.get(0);
        String skillsList = "";
        if (firstSkill != null){
            skillsList = firstSkill.getVisible(this.skills);
        }
        if (skillsList.contentEquals("")) {
            skillsList = "None";
        }
        Intent intent = new Intent();
        intent.putExtra("stronguestSkillsList", skillsList);
        setResult(RESULT_OK, intent);
        finish();
    }
    
    public void onClickBack(View v) {
        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        if (AppCAP.shouldFinishActivities()) {
            onBackPressed();
        }
        exe.getSkillsForUser();
    }
    
    public void toggleButtonVisible(View v) {
        UserLinkedinSkills clickedSkill = (UserLinkedinSkills) v.getTag();
        if (clickedSkill.isVisible()) {
            exe.changeSkillVisibility(clickedSkill.getId(), 0);
            clickedSkill.setVisible(false);
        } else {
            if (clickedSkill.countVisible(this.skills) > 4) {
                ((ToggleButton) v).setChecked(false);
                new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(getResources().getString(R.string.max_5_skills))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
                
            } else {
                exe.changeSkillVisibility(clickedSkill.getId(), 1);
                clickedSkill.setVisible(true);
            }
        }
    }
    
    protected void displaySkills(ArrayList<UserLinkedinSkills> skills) {

        if (AppCAP.isLoggedIn()) {
            this.skills = skills;
            adapterUsers = new LinkedInSkillsAdapter(
                    ActivityLinkedinSkills.this, this.skills);

            if (listView != null){
                listView.setAdapter(adapterUsers);
            }
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
    }

}
