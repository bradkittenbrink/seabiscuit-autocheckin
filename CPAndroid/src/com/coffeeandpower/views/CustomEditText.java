package com.coffeeandpower.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends EditText {

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        if (!this.isInEditMode()) {
            this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Regular.ttf"));
        }
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!this.isInEditMode()) {
            if (defStyle == Typeface.BOLD) {
                this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Bold.ttf"));
            } else {
                this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Regular.ttf"));
            }
        }
    }

    public CustomEditText(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "LiberationSans-Regular.ttf"));
    }

}
