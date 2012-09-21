package com.coffeeandpower.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
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
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityPlaceDetails;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.fragments.FragmentFeedsForOneVenue;
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
        private LinearLayout reply_area;
        private RelativeLayout plus_one_comment_button;
        public TextView plus_one_counter;
        private ImageView pill_button_plus1_comment_right;

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
            this.reply_area = (LinearLayout) convertView
                    .findViewById(R.id.item_venue_feeds_reply_listview);
            this.plus_one_comment_button = (RelativeLayout) convertView
                    .findViewById(R.id.item_venue_feeds_plus_one_comment_button);
            this.plus_one_counter = (TextView) convertView
                    .findViewById(R.id.item_venue_feeds_plus_one_counter);
            this.pill_button_plus1_comment_right = (ImageView) convertView
                    .findViewById(R.id.pill_button_plus1_comment_right);
            
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
        SimpleDateFormat dateOnly = new SimpleDateFormat("MMM d");
        SimpleDateFormat hourOnly = new SimpleDateFormat("hh:mma");
        try {
            date = simpleDateFormat.parse(currentMessage.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int pixels14 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                14, convertView.getResources().getDisplayMetrics()));
        String messageString=AppCAP.cleanResponseString(currentMessage
                               .getFormattedEntryText());
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
            if (currentMessage.getSkillName() != null) {
                messageString = " (" + currentMessage.getSkillName() + "): " + messageString;
            }
        } else {
            holder.love_sent_area.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 0);
            holder.textMessage.setLayoutParams(lp);
            holder.textMessage.setTypeface(null,Typeface.NORMAL);
        }
        if (currentMessage.getAuthorId() == AppCAP.getLoggedInUserId()) {
            holder.textDate.setText(dateOnly.format(date));
            holder.textHour.setText(hourOnly.format(date));
        } else {
            holder.textDate.setText("");
            holder.textHour.setText("");
        }

        int pixels62 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                62, convertView.getResources().getDisplayMetrics()));
        int pixels22 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                22, convertView.getResources().getDisplayMetrics()));
        int pixels10 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                10, convertView.getResources().getDisplayMetrics()));
        ArrayList<Feed> feedsReply = currentMessage.getReplyFeeds();
        if (feedsReply != null && feedsReply.size() > 0) {
            holder.reply_area.setVisibility(View.VISIBLE);
            displayReply(holder.reply_area, feedsReply, convertView.getContext());
        } else {
            holder.reply_area.setVisibility(View.GONE);
        }
        holder.plus_one_comment_button.setVisibility(View.VISIBLE);
        holder.plus_one_comment_button.setTag(R.id.tag_feed_id, currentMessage.getId());
        holder.plus_one_comment_button.setTag(R.id.tag_feed_type, currentMessage.getEntryType());
        holder.plus_one_comment_button.setTag(R.id.tag_feed_by_me, currentMessage.getUserHasLiked());

        if (currentMessage.getLikeCount() > 0){
            holder.plus_one_counter.setText("" + currentMessage.getLikeCount() );
            holder.plus_one_counter.setTextColor(Color.parseColor("#111111"));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    pixels62,
                    pixels22);
            lp.setMargins(0, 0, 0, 0);
            holder.pill_button_plus1_comment_right.setLayoutParams(lp);

        } else {
            holder.plus_one_counter.setText("");
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    pixels62,
                    pixels22);
            lp.setMargins(-pixels10, 0, 0, 0);
            holder.pill_button_plus1_comment_right.setLayoutParams(lp);
        }
        holder.textMessage.setText(AppCAP.cleanResponseString(currentMessage
                .getFormattedEntryText()));

        holder.textMessage.setText(messageString);

        // Display images
        displayImage(currentMessage.getAuthorPhotoUrl(),
                currentMessage.getAuthorId(), holder.profileImage);
        if (currentMessage.getEntryType().contentEquals(Feed.FEED_TYPE_LOVE)) {
            displayImage(currentMessage.getReceiverPhotoUrl(),
                    currentMessage.getReceiverId(), holder.profileImageReceiver);
        }

        return convertView;
    }

    private void displayReply(LinearLayout reply_area,
            ArrayList<Feed> feedsReply, final Context context) {
        int pixels10 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                10, context.getResources().getDisplayMetrics()));
        int pixels2 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                2, context.getResources().getDisplayMetrics()));
        int pixels5 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                5, context.getResources().getDisplayMetrics()));
        int pixels30 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                50, context.getResources().getDisplayMetrics()));
        reply_area.removeAllViews();
        for (int i = 0; i < feedsReply.size(); i++) {
            RelativeLayout layoutForInflateCategoryHeader = new RelativeLayout(context);
            LayoutParams lpRow = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            layoutForInflateCategoryHeader.setLayoutParams(lpRow); 
            // user image
            ImageView image = new ImageView(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(pixels30, pixels30);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            image.setLayoutParams(lp); 
            image.setId(R.id.user_image_in_reply);
            image.setPadding(pixels10, pixels5, pixels10, pixels10);
            displayImage(feedsReply.get(i).getAuthorPhotoUrl(), feedsReply.get(i).getAuthorId(),
                    image);
            layoutForInflateCategoryHeader.addView(image);
            // user image mask
            ImageView imageM = new ImageView(context);
            imageM.setLayoutParams(lp);          
            imageM.setPadding(pixels10, pixels5, pixels10, pixels10);
            Drawable drawable = context.getResources().getDrawable(
                    R.drawable.user_image_mask);
            imageM.setImageDrawable(drawable);
            layoutForInflateCategoryHeader.addView(imageM);

            // Set text
            TextView catName = new TextView(context);
            catName.setText(feedsReply.get(i).getEntryText());
            RelativeLayout.LayoutParams lptext = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lptext.addRule(RelativeLayout.RIGHT_OF, R.id.user_image_in_reply);
            catName.setLayoutParams(lptext); 
            catName.setPadding(pixels5, pixels5, 0, 0);
            layoutForInflateCategoryHeader.addView(catName);

            reply_area.addView(layoutForInflateCategoryHeader);
        }
    }

    public void displayImage(String imageUrl, int userId, ImageView profileImage) {
        if (AppCAP.isLoggedIn()) {
            if (imageUrl.contentEquals("") == false) {
                imageLoader.DisplayImage(imageUrl, profileImage,
                        R.drawable.default_avatar50, 30);
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
                    R.drawable.default_avatar50_login, 30);
        }
    }

    public void setNewData(ArrayList<Feed> messages2,
            VenueNameAndFeeds venueNameAndFeeds2) {
        this.messages = messages2;
        this.venueNameAndFeeds = venueNameAndFeeds2;
    }

}
