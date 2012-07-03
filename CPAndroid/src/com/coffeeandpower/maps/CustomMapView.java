package com.coffeeandpower.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class CustomMapView extends MapView {

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawARGB(125, 0, 0, 255);
        super.draw(canvas);
    }

}
