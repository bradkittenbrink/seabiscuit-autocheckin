package com.coffeeandpower.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.coffeeandpower.R;
import com.coffeeandpower.utils.Utils;

public class PinDrawable extends Drawable {

	public static final int TYPE_RED_PIN = 1;
	public static final int TYPE_WHITE_PIN = 2;
	public static final float PIN_TEXT_SIZE = 22;

	private String number;
	private Bitmap bitmapPin;
	private Paint paintPin;
	private Paint paintText;
	private Rect textBound;

	public PinDrawable(Context context, int hereNowCount) {
		number = hereNowCount + "";

		paintPin = new Paint();
		paintPin.setAntiAlias(true);

		paintText = new Paint();
		paintText.setColor(Color.WHITE);
		paintText.setTypeface(Typeface.SANS_SERIF);
		paintText.setAntiAlias(true);

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		if (hereNowCount < 10) {
			paintText.setTextSize(Utils.pixelToDp(PinDrawable.PIN_TEXT_SIZE, metrics));
		} else if (hereNowCount < 100) {
			paintText.setTextSize(Utils.pixelToDp(PinDrawable.PIN_TEXT_SIZE - 5, metrics));
		} else if (hereNowCount < 1000) {
			paintText.setTextSize(Utils.pixelToDp(PinDrawable.PIN_TEXT_SIZE - 10, metrics));
		} else {
			paintText.setTextSize(Utils.pixelToDp(PinDrawable.PIN_TEXT_SIZE - 14, metrics));
		}

		textBound = new Rect();
		paintText.getTextBounds(number, 0, number.length(), textBound);
		bitmapPin = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin_checkedin);
	};
	


	@Override
	public void draw(Canvas canvas) {
		float boundWidth = (textBound.width() / 2) + 2f;
		float boundHeight =  (bitmapPin.getHeight() / 2) - 2f;
		
		// For ICS we need to add 2f to the bound width to get it
		// centered, but below 3.0 that's not necessary, this is
		// due to differences in graphics rendering between Andriod
		// versions.
		if (!Utils.isAboveHoneycomb()) {
			boundWidth = boundWidth - 2f;
		}
		
		if (bitmapPin != null) {
			canvas.drawBitmap(bitmapPin, 0 - (bitmapPin.getWidth() / 2) + 2, 0 - bitmapPin.getHeight(), paintPin); // !!!
															       // -3
			canvas.drawText(number, 0 - boundWidth, 0 - boundHeight, paintText);
		}
	}

	@Override
	public int getIntrinsicHeight() {
		return bitmapPin.getHeight();
	}

	@Override
	public int getIntrinsicWidth() {
		return bitmapPin.getWidth();
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		paintPin.setAlpha(alpha);
		paintText.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paintPin.setColorFilter(cf);
		paintText.setColorFilter(cf);
	}

}
