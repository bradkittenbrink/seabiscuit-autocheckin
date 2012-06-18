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

public class PinBlackDrawable extends Drawable {

	private Bitmap bitmapPin;

	private Paint paintPin;
	private Paint paintText;

	private Rect textBound;

	private String text;

	public PinBlackDrawable(Context context, String text) {
		this.text = text;

		paintPin = new Paint();
		paintPin.setAntiAlias(true);

		paintText = new Paint();
		paintText.setColor(Color.BLACK);
		paintText.setTypeface(Typeface.DEFAULT_BOLD);
		paintText.setAntiAlias(true);
		paintText.setTextSize(Utils.getScreenDependentItemSize(Utils.TEXT_TYPE_MAP_BLACK_PIN));

		textBound = new Rect();
		paintText.getTextBounds(text, 0, text.length(), textBound);
		bitmapPin = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker_iphone);
	};

	@Override
	public void draw(Canvas canvas) {
		if (bitmapPin != null) {
			canvas.drawBitmap(bitmapPin, 0 - (bitmapPin.getWidth() / 2) + 2, 0 - bitmapPin.getHeight(), paintPin); // !!!
															       // -3
			canvas.drawText(text, 0 - (textBound.width() / 2), 0 - (bitmapPin.getHeight() + 5), paintText);
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
