package com.coffeeandpower.maps;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coffeeandpower.activity.ActivityPlaceDetails;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;

public class BalloonOverlayView<Item extends MyOverlayItem> extends FrameLayout {

    private LinearLayout layout;

    private TextView title;
    private TextView snippet;

    private int balloonBottomOffset;

    private String foursquareIdKey;
    private VenueSmart pinVenue;

    public BalloonOverlayView(final Context context, int balloonBottomOffset) {
        super(context);

        this.balloonBottomOffset = balloonBottomOffset;

        setPadding(10, 0, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.balloon_overlay, layout);
        title = (TextView) v.findViewById(R.id.balloon_item_title);
        snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

        // Click on Baloon next button
        ImageView next = (ImageView) v.findViewById(R.id.close_img_button);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (pinVenue != null) {
                    Intent intent = new Intent(context, ActivityPlaceDetails.class);
                    intent.putExtra("venueSmart", pinVenue);
                    ((ActivityVenueFeeds) context).startSmartActivity(intent, "ActivityPlaceDetails");
                }
                layout.setVisibility(GONE);
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;

        addView(layout, params);
    }

    public void setData(Item item) {
        this.foursquareIdKey = item.getFoursquareIdKey();
        this.pinVenue = item.getVenueSmartData();

        layout.setVisibility(VISIBLE);

        if (item.isPin()) {
            setPadding(10, 0, 10, 68);
        } else {
            setPadding(10, 0, 10, balloonBottomOffset);
        }

        if (item.getTitle() != null) {
            title.setVisibility(VISIBLE);
            title.setText(item.getTitle());
        } else {
            title.setVisibility(GONE);
        }

        if (item.getSnippet() != null) {
            snippet.setVisibility(VISIBLE);
            snippet.setText(item.getSnippet());
        } else {
            snippet.setVisibility(GONE);
        }

    }

}
