package com.coffeeandpower.utils;

import java.util.Date;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;

public class Utils {

    public static final int TEXT_TYPE_MAP_BLACK_PIN = 1200;
    public static final int MAP_HOR_OFFSET_FROM_CENTER = 1201;
    public static final int MAP_VER_OFFSET_FROM_CENTER = 1202;
    public static final int TEXT_TYPE_MAP_RED_PIN = 1204;

    /**
     * Use this for ListView inside ScrollView
     * 
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listView.getChildAt(0); // childs have
            // same size
            if (listItem != null) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * Get Date and Time from epoch GMT
     * 
     * @param epoch
     * @return
     */
    public static String getDateFromEpoch(String epoch) {
        long epoch_ = 0;
        try {
            epoch_ = Long.parseLong(epoch);
        } catch (NumberFormatException e) {
        }
        epoch_ = epoch_ * 1000;

        Date d = new Date(epoch_);
        return d.toLocaleString();
    }

    /**
     * Animate List View (try with other views)
     * 
     * @param lv
     */
    public static void animateListView(ListView lv) {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(150);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);
        lv.setLayoutAnimation(controller);
    }

    /**
     * Get sizes for drawing elements
     * 
     * @param textType
     * @return
     */
    public static int getScreenDependentItemSize(int textType) {
        int screenWidth = AppCAP.getScreenWidth();

        switch (textType) {
        case TEXT_TYPE_MAP_BLACK_PIN:
            if (screenWidth <= 245) {
                return 15;
            } else if (screenWidth <= 325) {
                return 18;
            } else if (screenWidth <= 485) {
                return 26;
            } else if (screenWidth > 485) {
                return 32;
            }

        case TEXT_TYPE_MAP_RED_PIN:
            if (screenWidth <= 245) {
                return 18;
            } else if (screenWidth <= 325) {
                return 21;
            } else if (screenWidth <= 485) {
                return 29;
            } else if (screenWidth > 485) {
                return 32;
            }

        case MAP_HOR_OFFSET_FROM_CENTER:
            if (screenWidth <= 245) {
                return 800;
            } else if (screenWidth <= 325) {
                return 1000;
            } else if (screenWidth <= 485) {
                return 1500;
            } else if (screenWidth > 485) {
                return 1800;
            }

        case MAP_VER_OFFSET_FROM_CENTER:
            if (screenWidth <= 245) {
                return 200;
            } else if (screenWidth <= 325) {
                return 300;
            } else if (screenWidth <= 485) {
                return 500;
            } else if (screenWidth > 485) {
                return 800;
            }
        }
        return 0;
    }

    // Converts measure units to their screen independent size
    public static float pixelToDp(float px, DisplayMetrics metrics) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px,
                metrics);
    }

    public static boolean isAboveHoneycomb() {
        if (Build.VERSION.SDK_INT < 11) {
            return false;
        } else {
            return true;
        }
    }
}
