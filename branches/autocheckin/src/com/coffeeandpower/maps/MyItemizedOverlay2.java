package com.coffeeandpower.maps;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay2 extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public MyItemizedOverlay2(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        return true;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow)
            return;
        super.draw(canvas, mapView, shadow);
    }

}
