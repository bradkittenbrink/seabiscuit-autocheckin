package com.coffeeandpower.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ButtonAnimationListener implements AnimationListener{
    LinearLayout mView;
int mModifier;
Context mContext;

public ButtonAnimationListener(LinearLayout v, int modifier, Context c){
    mView=v;
    int test = mView.getTop();
    mModifier=modifier;
    mContext=c;
}
public void onAnimationEnd(Animation animation) {
    animation.setFillAfter(false);
    MarginLayoutParams marginParams = new MarginLayoutParams(mView.getLayoutParams());
    marginParams.setMargins(0, 0, 0, 0);
    RelativeLayout.LayoutParams par=(RelativeLayout.LayoutParams)mView.getLayoutParams();
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
    layoutParams.alignWithParent=true;
    par.bottomMargin = mModifier;
    mView.setLayoutParams(par); 

}

public void onAnimationRepeat(Animation animation) {}

public void onAnimationStart(Animation animation) {}

}