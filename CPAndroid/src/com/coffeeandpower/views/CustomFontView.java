package com.coffeeandpower.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontView extends TextView {

    public CustomFontView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        if (!this.isInEditMode()) {
            this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Regular.ttf"));
        }
    }

    public CustomFontView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!this.isInEditMode()) {
            if (defStyle == Typeface.BOLD) {
                this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Bold.ttf"));
            } else {
                this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Regular.ttf"));
            }
        }
    }

    public CustomFontView(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Regular.ttf"));
    }
}
