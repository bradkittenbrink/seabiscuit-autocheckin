package com.coffeeandpower.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.imageutil.ImageLoader;

public class MyVenueFeedsAdapter extends BaseAdapter {


    private ArrayList<VenueNameAndFeeds> venueNameAndFeeds;

    private LayoutInflater inflater;

    private Activity context;

    private ImageLoader imageLoader;


    public MyVenueFeedsAdapter(Activity context, ArrayList<VenueNameAndFeeds> venueNameAndFeeds) {
        this.context = context;
        if (context == null) return;
        this.inflater = context.getLayoutInflater();
        this.imageLoader = new ImageLoader(context.getApplicationContext());

        if (venueNameAndFeeds != null) {
            this.venueNameAndFeeds = venueNameAndFeeds;
        } else {
            this.venueNameAndFeeds = new ArrayList<VenueNameAndFeeds>();
        }
        Log.d("MyVenueFeedsAdapter", "this.venueNameAndFeeds length..." + this.venueNameAndFeeds.size());
    }

    public void setNewData(ArrayList<VenueNameAndFeeds> venueNameAndFeeds) {
        this.venueNameAndFeeds = venueNameAndFeeds;
    }

    @Override
    public int getCount() {
        return venueNameAndFeeds.size();
    }

    @Override
    public Object getItem(int position) {
        return venueNameAndFeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {

        public TextView textMessage;
        public LinearLayout listFeeds;
        public LinearLayout reply_area;
        public Button removeButton;
        public View item_venue_feeds_1;
        public View item_venue_feeds_2;
        public View item_venue_feeds_3;

        public ViewHolder(View convertView) {

            this.textMessage = (TextView) convertView
                    .findViewById(R.id.textview_venue_name);
            this.listFeeds = (LinearLayout) convertView
                    .findViewById(R.id.item_venue_feeds_listview);
            this.removeButton = (Button) convertView
                    .findViewById(R.id.btn_remove);
            this.item_venue_feeds_1 = convertView
                    .findViewById(R.id.item_venue_feeds_1);
            this.item_venue_feeds_2 = convertView
                    .findViewById(R.id.item_venue_feeds_2);
            this.item_venue_feeds_3 = convertView
                    .findViewById(R.id.item_venue_feeds_3);
        }
        public View getItemVenueFeeds(int position){
            if (position == 0){
                return this.item_venue_feeds_1;
            }
            if (position == 1){
                return this.item_venue_feeds_2;
            }
            if (position == 2){
                return this.item_venue_feeds_3;
            }
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder; 

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_venue_feeds, null);
            holder = new ViewHolder(convertView);
            convertView.setTag( R.id.holder_id, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder_id);
        }
        holder.textMessage.setPadding(20, 6, 20, 9);

        holder.textMessage.setText(AppCAP.cleanResponseString(venueNameAndFeeds.get(
                position).getName()));
        holder.removeButton.setVisibility(View.VISIBLE);
        holder.removeButton.setTag(venueNameAndFeeds.get(position)); 
        convertView.setTag(R.id.venue_name_and_feeds, venueNameAndFeeds.get(position));

        ArrayList<Feed> messages = venueNameAndFeeds.get(
                position).getFeedsArray();
        VenueNameAndFeeds venue = venueNameAndFeeds.get(position);
        for (int i=0; i < 3; i++) {
            View vi = holder.getItemVenueFeeds(i);
            if (i < messages.size()) {
                Feed message = messages.get(i);
                vi.setVisibility(View.VISIBLE);
                fillItem(vi, message, venue);
            } else {
                vi.setVisibility(View.GONE);
            }
        }
        if (messages.size() == 0) {
            holder.listFeeds.setVisibility(View.GONE);
        } else {
            holder.listFeeds.setVisibility(View.VISIBLE);
        }
        return convertView; 
    }

    public void fillItem(View vi, Feed message, VenueNameAndFeeds venueNameAndFeeds) {
        TextView textDate = (TextView) vi
                .findViewById(R.id.textview_feed_date);
        TextView textHour = (TextView) vi
                .findViewById(R.id.textview_feed_hour);
        TextView textMessage = (TextView) vi
                .findViewById(R.id.textview_chat_message);
        ImageView profileImage = (ImageView) vi
                .findViewById(R.id.imageview_image);  
        ImageView profileImageReceiver = (ImageView) vi
                .findViewById(R.id.imageview_image_receiver);  
        vi.setTag(R.id.venue_name_and_feeds, venueNameAndFeeds);
        RelativeLayout love_sent_area = (RelativeLayout) vi
                .findViewById(R.id.love_sent_area); 

    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");        
    SimpleDateFormat dateOnly = new SimpleDateFormat("MMM d");        
    SimpleDateFormat hourOnly = new SimpleDateFormat("hh:mma");        
    try {
        date = simpleDateFormat.parse(message.getDate());
    } catch (ParseException e) { 
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
            
    if (message.getEntryType().contentEquals(Feed.FEED_TYPE_LOVE)) {
        love_sent_area.setVisibility(View.VISIBLE);
        love_sent_area.measure(0, 0);
        int width = love_sent_area.getMeasuredWidth();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(width, 0, 0, 0);
        textMessage.setLayoutParams(lp);
        textMessage.setTypeface(null,Typeface.BOLD);
    } else {
        love_sent_area.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        textMessage.setLayoutParams(lp);
        textMessage.setTypeface(null,Typeface.NORMAL);
    }
    textDate.setText(dateOnly.format(date));
    textHour.setText(hourOnly.format(date));
    // Display images
    displayImage(message.getAuthorPhotoUrl(), message.getAuthorId(), profileImage);
    String messageString = AppCAP.cleanResponseString(message.getFormattedEntryText());
    if (message.getEntryType().contentEquals(Feed.FEED_TYPE_LOVE)) {
        if (message.getSkillName() != null) {
            messageString = "(" + message.getSkillName() + ") " + messageString;
        } // This happens if "general" love is sent.
        displayImage(message.getReceiverPhotoUrl(), message.getReceiverId(), profileImageReceiver);
    }
    textMessage.setText(messageString);
    
}
    public void displayImage(String imageUrl, int userId, ImageView profileImage) {
        if (AppCAP.isLoggedIn()) {
            if (imageUrl.contentEquals("") == false) {
                imageLoader.DisplayImage(imageUrl,
                        profileImage, R.drawable.default_avatar50, 70);
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

}
