package com.coffeeandpower.datatiming;

import java.util.Observable;

import android.util.Log;

import com.coffeeandpower.cont.DataHolder;

public class CachedNetworkData extends Observable{

	private boolean isActive;
	private boolean hasData;
	
	private String type;
	
	private DataHolder cachedData;
	
	
	
	public CachedNetworkData(String myType) {
		isActive = false;
		hasData = false;
		this.type = myType;
	}
	
	public void activate() {
		Log.d("CachedNetworkData",this.type + ": activate()");
		this.isActive = true;
	}
	
	public void deactivate() {
		Log.d("CachedNetworkData",this.type + ": deactivate()");
		this.isActive = false;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	
	public void setNewData(DataHolder newData) {
		cachedData = newData;
		
		
		if (cachedData.getResponseMessage().equals("HTTP 200 OK")) {
			Log.d("Timer","Sending notifyObservers with received data from API call: " + type + "...");
	                    
                        // Send notify for nearby venues
			hasData = true;
                        setChanged();
                        notifyObservers(new CounterData(cachedData));
            	} else {
                    Log.d("CachedNetworkData","Skipping notifyObservers for API call: " + type);
                    	    
            	}
	}
	
	public void sendCachedData() {
		if (hasData) {
			Log.d("CachedNetworkData","Sending cached data for API: " + this.type + "...");
			setChanged();   // Not sure if this is necessary
			notifyObservers(new CounterData(cachedData));
		}
	}
	
	public DataHolder getData() {
		return cachedData;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean hasData() {
		return this.hasData;
	}
	

}
