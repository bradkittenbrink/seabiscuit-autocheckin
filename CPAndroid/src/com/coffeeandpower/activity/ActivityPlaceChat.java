package com.coffeeandpower.activity;

import android.os.Bundle;

import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

public class ActivityPlaceChat extends RootActivity{

    private DataHolder result;
    
    private Executor exe;
    
    private String venueId;
    private String lastChatIDString;
    
    {
	venueId = "";
	lastChatIDString = "0";
    }
    
    
    @Override
    protected void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	
	// Get data from intent
	Bundle extras = getIntent().getExtras();
	if (extras!=null){
	    venueId = extras.getString("venue_id");
	}
	
	// Executor
	exe = new Executor(ActivityPlaceChat.this);
	exe.setExecutorListener(new ExecutorInterface() {
	    @Override
	    public void onErrorReceived() {
		
	    }

	    @Override
	    public void onActionFinished(int action) {
		result = exe.getResult();
		
		switch (action) {
		case Executor.HANDLE_GET_VENUE_CHAT:
		    
		    break;

		}
	    }
	});
    }

    @Override
    protected void onResume() {
	super.onResume();
	
	// Get venue chat
	exe.getVenueChat(venueId, lastChatIDString);
    }
    
    @Override
    protected void onDestroy() {
	super.onDestroy();
    }



    
}
