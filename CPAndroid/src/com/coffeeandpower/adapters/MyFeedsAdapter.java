package com.coffeeandpower.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityFeedsForOneVenue;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.utils.Utils;

public class MyFeedsAdapter extends BaseAdapter {

    private ArrayList<Feed> messages;
    private VenueNameAndFeeds venueNameAndFeeds;
    private LayoutInflater inflater;
    public ImageLoader imageLoader;
    private int localUserId;
    private Activity context;

    public MyFeedsAdapter(Activity context, ArrayList<Feed> messages,
            VenueNameAndFeeds venueNameAndFeeds) {
        this.context = context;
        this.inflater = context.getLayoutInflater();
        this.imageLoader = new ImageLoader(context.getApplicationContext());
        this.localUserId = AppCAP.getLoggedInUserId();
        Log.d("MyFeedsAdapter", "messages length..." + messages.size());
        if (messages != null) {
            this.messages = messages;
        } else {
            this.messages = new ArrayList<Feed>();
        }
        this.venueNameAndFeeds = venueNameAndFeeds;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {

        public TextView textDate;
        public TextView textHour;
        public TextView textMessage;
        public ImageView profileImage;
        private RelativeLayout love_sent_area;
        private ImageView profileImageReceiver;

        public ViewHolder(View convertView) {

            this.textDate = (TextView) convertView
                    .findViewById(R.id.textview_feed_date);
            this.textHour = (TextView) convertView
                    .findViewById(R.id.textview_feed_hour);
            this.textMessage = (TextView) convertView
                    .findViewById(R.id.textview_chat_message);
            this.profileImage = (ImageView) convertView
                    .findViewById(R.id.imageview_image);
            this.profileImageReceiver = (ImageView) convertView
                    .findViewById(R.id.imageview_image_receiver);

            this.love_sent_area = (RelativeLayout) convertView
                    .findViewById(R.id.love_sent_area);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Feed currentMessage = messages.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_feeds, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.holder_id, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder_id);
        }
        convertView.setTag(R.id.venue_name_and_feeds, venueNameAndFeeds);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat dateOnly = new SimpleDateFormat("MMM dd");
        SimpleDateFormat hourOnly = new SimpleDateFormat("hh:mm");
        try {
            date = simpleDateFormat.parse(currentMessage.getDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (currentMessage.getEntryType().contentEquals(Feed.FEED_TYPE_LOVE)) {
            holder.love_sent_area.setVisibility(View.VISIBLE);
            holder.love_sent_area.measure(0, 0);
            int width = holder.love_sent_area.getMeasuredWidth();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(width, 0, 0, 0);
            holder.textMessage.setLayoutParams(lp);
            holder.textMessage.setTypeface(null,Typeface.BOLD);
        } else {
            holder.love_sent_area.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 0);
            holder.textMessage.setLayoutParams(lp);
            holder.textMessage.setTypeface(null,Typeface.NORMAL);
        }

        holder.textDate.setText(dateOnly.format(date));
        holder.textHour.setText(hourOnly.format(date));
        holder.textMessage.setText(AppCAP.cleanResponseString(currentMessage
                .getFormattedEntryText()));
        // Display images
        displayImage(currentMessage.getAuthorPhotoUrl(),
                currentMessage.getAuthorId(), holder.profileImage);
        if (currentMessage.getEntryType().contentEquals(Feed.FEED_TYPE_LOVE)) {
            displayImage(currentMessage.getReceiverPhotoUrl(),
                    currentMessage.getReceiverId(), holder.profileImageReceiver);
        }

        return convertView;
    }

    public void displayImage(String imageUrl, int userId, ImageView profileImage) {
        if (AppCAP.isLoggedIn()) {
            if (imageUrl.contentEquals("") == false) {
                imageLoader.DisplayImage(imageUrl, profileImage,
                        R.drawable.default_avatar50, 70);
            }
            profileImage.setTag(userId);
            profileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppCAP.isLoggedIn()) {
                        context.showDialog(RootActivity.DIALOG_MUST_BE_A_MEMBER);
                    } else {
                        int user_id = (Integer) v.getTag();
                        Intent intent = new Intent(context,
                                ActivityUserDetails.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("from_act", "user_id");
                        context.startActivity(intent);
                    }
                }
            });

        } else {
            imageLoader.DisplayImage("", profileImage,
                    R.drawable.default_avatar50_login, 70);
        }
    }

    public void setNewData(ArrayList<Feed> messages2,
            VenueNameAndFeeds venueNameAndFeeds2) {
        this.messages = messages2;
        this.venueNameAndFeeds = venueNameAndFeeds2;
    }

}
