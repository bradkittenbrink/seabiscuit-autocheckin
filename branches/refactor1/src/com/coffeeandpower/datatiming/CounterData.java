package com.coffeeandpower.datatiming;

import com.coffeeandpower.cont.DataHolder;

public class CounterData {
	public static String triggertype = "trigger";
	
	public static String counttype = "count";
	
	public DataHolder nearbyVenues;
	public DataHolder venuesWithCheckins;
	
	public String type = "unknown";
	
	public CounterData(DataHolder newVenuesWithCheckins,DataHolder newNearbyVenues) {
		this.type = CounterData.triggertype;
		this.venuesWithCheckins = newVenuesWithCheckins;
		this.nearbyVenues = newNearbyVenues;
	}
}
