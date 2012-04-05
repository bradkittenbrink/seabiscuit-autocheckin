package com.coffeeandpower.maps;

import com.coffeeandpower.cont.MapUserData;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem{

	private MapUserData mud;
	
	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		
	}

	public void setMapUserData (MapUserData mud){
		
		this.mud = mud;
	}
	
	public MapUserData getMapuserData (){
		
		return mud;
	}
	
}
