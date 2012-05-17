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

import com.coffeeandpower.R;
import com.coffeeandpower.utils.Utils;

public class PinDrawable extends Drawable {

	public static final int TYPE_RED_PIN = 1;
	public static final int TYPE_WHITE_PIN = 2;

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
		paintText.setTypeface(Typeface.SERIF);
		paintText.setAntiAlias(true);

		if (hereNowCount < 10) {
			paintText.setTextSize(Utils.getScreenDependentItemSize(Utils.TEXT_TYPE_MAP_RED_PIN));
		} else if (hereNowCount < 100) {
			paintText.setTextSize(Utils.getScreenDependentItemSize(Utils.TEXT_TYPE_MAP_RED_PIN) - 5);
		} else if (hereNowCount < 1000) {
			paintText.setTextSize(Utils.getScreenDependentItemSize(Utils.TEXT_TYPE_MAP_RED_PIN) - 10);
		} else {
			paintText.setTextSize(Utils.getScreenDependentItemSize(Utils.TEXT_TYPE_MAP_RED_PIN) - 14);
		}

		textBound = new Rect();
		paintText.getTextBounds(number, 0, number.length(), textBound);
		bitmapPin = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin_checkedin);

	};

	@Override
	public void draw(Canvas canvas) {
		if (bitmapPin != null) {
			canvas.drawBitmap(bitmapPin, 0 - (bitmapPin.getWidth() / 2) + 2, 0 - bitmapPin.getHeight(), paintPin); // !!!
															       // -3
			canvas.drawText(number, 0 - (textBound.width() / 2), 0 - (bitmapPin.getHeight() / 2), paintText);
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
