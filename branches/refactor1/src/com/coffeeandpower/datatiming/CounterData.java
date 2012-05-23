package com.coffeeandpower.datatiming;

import com.coffeeandpower.cont.DataHolder;

public class CounterData {
	public static String triggertype = "trigger";
	public static String counttype = "count";
	public DataHolder value;
	public String type = "unknown";
	public CounterData(String type, DataHolder value) {
		this.type = type;
		this.value = value;
	}
}
