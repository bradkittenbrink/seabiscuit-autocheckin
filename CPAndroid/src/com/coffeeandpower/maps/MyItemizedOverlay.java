package com.coffeeandpower.maps;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;

public class MyItemizedOverlay extends BalloonItemizedOverlay<MyOverlayItem> {

	private ArrayList<MyOverlayItem> m_overlays = new ArrayList<MyOverlayItem>();


	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);

	}

	public void addOverlay(MyOverlayItem overlay) {
		m_overlays.add(overlay);
		populate();
	}

	public void remove (int position){
		m_overlays.remove(position);
		populate();
	}

	@Override
	protected MyOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, MyOverlayItem item) {

		return true;
	}

}
