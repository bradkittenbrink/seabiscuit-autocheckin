package com.coffeeandpower.activity;

import com.coffeeandpower.RootActivity;
import com.coffeeandpower.utils.UserAndTabMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ActivityDialog extends RootActivity {
    
    private Bundle intentExtras;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        String message;
        String do_check_out;
        super.onCreate(savedInstanceState);
        final Context context = this;
        intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            message = intentExtras.getString("alarm_message");      
            do_check_out = intentExtras.getString("do_check_out");
            if (message != null) {
                new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intentForDialogActivity = new Intent(context, ActivityCheckIn.class);
                        context.startActivity(intentForDialogActivity);                
                        finish();
                    }
                })
                .show();
            } else if (do_check_out != null) {
                UserAndTabMenu menu = new UserAndTabMenu(this);
                menu.onClickCheckOut(null, this);
            }
        }
    }

}