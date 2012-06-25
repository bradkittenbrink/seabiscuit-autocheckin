package com.coffeeandpower.datatiming;

import com.coffeeandpower.cont.DataHolder;

public class CachedDataContainer {
	public static String triggertype = "trigger";
	
	public static String counttype = "count";
	
	private DataHolder data;
	
	public String type = "unknown";
	
	public CachedDataContainer(DataHolder myData) {
		this.type = CachedDataContainer.triggertype;
		this.data = myData;
	}
	
	public DataHolder getData() {
		return data;
	}
}
