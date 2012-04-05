package com.coffeeandpower.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem{

	private String foursquareIdKey;
	
	private boolean res;
	
	
	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		
	}

	public void setMapUserData (String foursquareIdKey){
		
		this.foursquareIdKey = foursquareIdKey;
	}
	
	public String getFoursquareIdKey(){
		
		return foursquareIdKey;
	}
	
	public void setAsList (boolean res){
		
		this.res = res;
	}
	
	public boolean isList(){
		
		return res;
	}
}
