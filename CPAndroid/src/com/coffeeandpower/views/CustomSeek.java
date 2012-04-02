package com.coffeeandpower.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class CustomSeek extends SeekBar{

	private Paint paintNormal;
	private Paint paintBig;

	private Rect textRect;

	private int viewWidth;

	private int TEXT_VERT_OFFSET = 23;
	private boolean lockSetProgress = false;

	
	public interface HoursChangeListener{
		public void onHoursChange(int hours);
	}
	
	HoursChangeListener hoursChangeListener = new HoursChangeListener() {
		@Override
		public void onHoursChange(int hours) {}
	};
	
	public void setOnHoursChangeListener(HoursChangeListener hoursChangeListener){
		this.hoursChangeListener = hoursChangeListener;
	}
	
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {

		viewWidth = MeasureSpec.getSize(widthMeasureSpec); 
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public CustomSeek(Context context, AttributeSet attrs) {
		super(context, attrs);

		setPadding(0, 28, 0, 0);
		setMax(104);  // divide by 8

		paintNormal = new Paint();
		paintNormal.setTextSize(22);      // text Size normal
		paintNormal.setTypeface(Typeface.DEFAULT_BOLD);
		paintNormal.setColor(0xff666666); // text Color Normal
		paintNormal.setAntiAlias(true);

		paintBig = new Paint();
		paintBig.setTextSize(30);       // text Size Big
		paintBig.setTypeface(Typeface.DEFAULT_BOLD);
		paintBig.setColor(0xff42818b);  // text Color Big
		paintBig.setAntiAlias(true);

		textRect = new Rect();
		paintNormal.getTextBounds("5", 0, 1, textRect);
	}


	@Override
	public void draw(Canvas canvas) {

		if (viewWidth!=0){

			int step = viewWidth / 8;

			if (getProgress()>= 0 && getProgress() <  2 * getMax() / 8){
				canvas.drawText("1", 1 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintBig);
				if (!lockSetProgress){
					setProgress(9);
					hoursChangeListener.onHoursChange(1);
				}
			} else {
				canvas.drawText("1", 1 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintNormal);
			}

			if (getProgress() >= 2 * getMax() / 8 && getProgress() <  4 * getMax() / 8){
				canvas.drawText("3", 3 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintBig);
				if (!lockSetProgress){
					setProgress(38);
					hoursChangeListener.onHoursChange(3);
				}
			} else {
				canvas.drawText("3", 3 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintNormal);
			}

			if (getProgress() >= 4 * getMax() / 8 && getProgress() <  6 * getMax() / 8){
				canvas.drawText("5", 5 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintBig);
				if (!lockSetProgress){
					setProgress(67);
					hoursChangeListener.onHoursChange(5);
				}
			} else {
				canvas.drawText("5", 5 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintNormal);
			}

			if (getProgress() >= 6 * getMax() / 8 && getProgress() <= getMax()){
				canvas.drawText("7", 7 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintBig);
				if (!lockSetProgress){
					setProgress(96);
					hoursChangeListener.onHoursChange(7);
				}
			} else {
				canvas.drawText("7", 7 * step - textRect.width() /2, TEXT_VERT_OFFSET, paintNormal);
			}

		}

		//invalidate();
		super.draw(canvas);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()){

		case MotionEvent.ACTION_DOWN:
			lockSetProgress = true;

		case MotionEvent.ACTION_CANCEL:
			lockSetProgress = false;

		case MotionEvent.ACTION_MOVE:
			lockSetProgress = true;

		case MotionEvent.ACTION_UP:
			lockSetProgress = false;
		}


		event.setLocation(event.getX(), event.getY()-28);
		return super.onTouchEvent(event);
	}


}
