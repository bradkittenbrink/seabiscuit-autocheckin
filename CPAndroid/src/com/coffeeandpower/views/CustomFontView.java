package com.coffeeandpower.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontView extends TextView{

	public CustomFontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Typeface font = Typeface.createFromAsset(context.getAssets(), "LeagueGothic.otf");
		this.setTypeface(font);
	}

}
