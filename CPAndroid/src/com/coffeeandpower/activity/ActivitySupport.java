package com.coffeeandpower.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;

public class ActivitySupport extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
    }

    public void onClickSupportSendFeedback(View v) {
        Intent intent = new Intent(this, ActivitySupportFeedback.class);
        startActivity(intent);
    }

    public void onClickSupportTOS(View v) {
        Intent intent = new Intent(this, ActivitySupportTOS.class);
        startActivity(intent);
    }

    public void onClickBack(View v) {
        onBackPressed();
    }
}
