package com.coffeeandpower;

import com.coffeeandpower.activity.ActivityDialog;
import com.coffeeandpower.utils.UserAndTabMenu;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CheckOutIntentReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppCAP.isUserCheckedIn()){
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("alarm_message");
            if (message != null) {
                int minutesBefore = 5;  
                long currentTime = System.currentTimeMillis();
                long checkOutTime = currentTime +  
                        (minutesBefore * 60 * 1000);
                Intent intentTimer = new Intent(context, CheckOutIntentReceiver.class);
                intentTimer.putExtra("do_check_out", "y" );
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, intentTimer, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, checkOutTime, sender);   

                Intent intentForDialogActivity = new Intent(context, ActivityDialog.class);
                intentForDialogActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                   
                intentForDialogActivity.putExtra("alarm_message", message); 
                context.startActivity(intentForDialogActivity);   
            } else {
                String do_check_out = bundle.getString("do_check_out");
                if (do_check_out != null) {
                    Intent intentForDialogActivity = new Intent(context, ActivityDialog.class);
                    intentForDialogActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                   
                    intentForDialogActivity.putExtra("do_check_out", do_check_out); 
                    context.startActivity(intentForDialogActivity);   
                }
            }
        }
    }
}
