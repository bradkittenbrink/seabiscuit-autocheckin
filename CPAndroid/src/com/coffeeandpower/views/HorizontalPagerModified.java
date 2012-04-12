
package com.coffeeandpower.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;


public final class HorizontalPagerModified extends ViewGroup {
	/*
	 * How long to animate between screens when programmatically setting with setCurrentScreen using
	 * the animate parameter
	 */
	private static final int ANIMATION_SCREEN_SET_DURATION_MILLIS = 800;
	// What fraction (1/x) of the screen the user must swipe to indicate a page change
	private static final int FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE = 4;
	private static final int INVALID_SCREEN = -1;
	/*
	 * Velocity of a swipe (in density-independent pixels per second) to force a swipe to the
	 * next/previous screen. Adjusted into mDensityAdjustedSnapVelocity on init.
	 */
	private static final int SNAP_VELOCITY_DIP_PER_SECOND = 600;
	// Argument to getVelocity for units to give pixels per second (1 = pixels per millisecond).
	private static final int VELOCITY_UNIT_PIXELS_PER_SECOND = 1000;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
	private static final int TOUCH_STATE_VERTICAL_SCROLLING = -1;
	private int mCurrentScreen;
	private int mDensityAdjustedSnapVelocity;
	private boolean mFirstLayout = true;
	private float mLastMotionX;
	private float mLastMotionY;
	private OnScreenSwitchListener mOnScreenSwitchListener;
	private int mMaximumVelocity;
	private int mNextScreen = INVALID_SCREEN;
	private Scroller mScroller;
	private int mTouchSlop;
	private int mTouchState = TOUCH_STATE_REST;
	private VelocityTracker mVelocityTracker;
	private int mLastSeenLayoutWidth = -1;


	public HorizontalPagerModified(final Context context) {
		super(context);
		init();
	}

	public HorizontalPagerModified(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());

		// Calculate the density-dependent snap velocity in pixels
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
		.getMetrics(displayMetrics);
		mDensityAdjustedSnapVelocity =
				(int) (displayMetrics.density * SNAP_VELOCITY_DIP_PER_SECOND);

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("ViewSwitcher can only be used in EXACTLY mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("ViewSwitcher can only be used in EXACTLY mode.");
		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			mFirstLayout = false;
		}

		else if (width != mLastSeenLayoutWidth) { // Width has changed

			Display display =
					((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int displayWidth = display.getWidth();

			mNextScreen = Math.max(0, Math.min(getCurrentScreen(), getChildCount() - 1));
			final int newX = mNextScreen * displayWidth;
			final int delta = newX - getScrollX();

			mScroller.startScroll(getScrollX(), 0, delta, 0, 0);
		}

		mLastSeenLayoutWidth   = width;
	}

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r,
			final int b) {
		int childLeft = 0;
		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));

			// Notify observer about screen change
			if (mOnScreenSwitchListener != null) {
				mOnScreenSwitchListener.onScreenSwitched(mCurrentScreen);
			}

			mNextScreen = INVALID_SCREEN;
		}
	}

	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	public void setCurrentScreen(final int currentScreen, final boolean animate) {
		mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
		if (animate) {
			snapToScreen(currentScreen, ANIMATION_SCREEN_SET_DURATION_MILLIS);
		} else {
			scrollTo(mCurrentScreen * getWidth(), 0);
		}
		invalidate();
	}


	public void setOnScreenSwitchListener(final OnScreenSwitchListener onScreenSwitchListener) {
		mOnScreenSwitchListener = onScreenSwitchListener;
	}

	private void snapToDestination() {
		final int screenWidth = getWidth();
		int scrollX = getScrollX();
		int whichScreen = mCurrentScreen;
		int deltaX = scrollX - (screenWidth * mCurrentScreen);

		// Check if they want to go to the prev. screen
		if ((deltaX < 0) && mCurrentScreen != 0
				&& ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < -deltaX)) {
			whichScreen--;
			// Check if they want to go to the next screen
		} else if ((deltaX > 0) && (mCurrentScreen + 1 != getChildCount())
				&& ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < deltaX)) {
			whichScreen++;
		}

		snapToScreen(whichScreen);
	}


	private void snapToScreen(final int whichScreen) {
		snapToScreen(whichScreen, -1);
	}


	private void snapToScreen(final int whichScreen, final int duration) {

		mNextScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		final int newX = mNextScreen * getWidth();
		final int delta = newX - getScrollX();

		if (duration < 0) {
			// E.g. if they've scrolled 80% of the way, only animation for 20% of the duration
			mScroller.startScroll(getScrollX(), 0, delta, 0, (int) (Math.abs(delta)
					/ (float) getWidth() * ANIMATION_SCREEN_SET_DURATION_MILLIS));
		} else {
			if (whichScreen==0){
				mScroller.startScroll(getScrollX(), 0, delta + (getWidth()/4), 0, duration);
			} else {
				mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
			}
		}

		invalidate();
	}

	public static interface OnScreenSwitchListener {

		void onScreenSwitched(int screen);
		void onScrollStart();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		invalidate();
		super.onDraw(canvas);
	}


}
