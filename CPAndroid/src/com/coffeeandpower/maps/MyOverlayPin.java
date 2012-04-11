package com.coffeeandpower.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

import com.coffeeandpower.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyOverlayPin extends Overlay{

	public static final int TYPE_RED_PIN = 1;
	public static final int TYPE_WHITE_PIN = 2;

	private String number;

	private GeoPoint gp;

	private Bitmap bitmapPin;

	private Paint paintPin;
	private Paint paintText;
	
	private Rect textBound;

	public MyOverlayPin(Context context, GeoPoint gp, int type, int hereNowCount){

		this.gp = gp;
		number = hereNowCount + "";

		paintPin = new Paint();
		paintPin.setAntiAlias(true);

		paintText = new Paint();
		paintText.setColor(Color.WHITE);
		paintText.setTypeface(Typeface.SERIF);
		paintText.setAntiAlias(true);
		
		if (hereNowCount < 10){
			paintText.setTextSize(29);
		} else if (hereNowCount < 100) {
			paintText.setTextSize(24);
		} else if (hereNowCount < 1000){
			paintText.setTextSize(19);
		} else {
			paintText.setTextSize(15);
		}
		
		textBound = new Rect();
		paintText.getTextBounds(number, 0, number.length(), textBound);
		

		switch (type) {
		case TYPE_RED_PIN:
			bitmapPin = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin_checkedin);
			break;
		case TYPE_WHITE_PIN:
			bitmapPin = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin_checkedout);
			break;
		}
	};


	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when){

		Projection projection = mapView.getProjection();

		// This is location on the map, use this 'point'
		Point point = new Point(); 
		projection.toPixels(gp, point); 


		if (bitmapPin!=null){

			canvas.drawBitmap(bitmapPin, point.x - (bitmapPin.getWidth() / 2) + 2, point.y - bitmapPin.getHeight(), paintPin); // !!! -3
			canvas.drawText(number, point.x - (textBound.width() / 2), point.y - (bitmapPin.getHeight() /2) , paintText);
		}
		return super.draw(canvas, mapView, shadow, when);
	}


	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		
		Log.d("LOG", "TAP");
		return true;
	}
	
	
}
