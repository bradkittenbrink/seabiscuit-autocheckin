package com.coffeeandpower.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
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

    public MyFeedsAdapter(Activity context, ArrayList<Feed> messages, VenueNameAndFeeds venueNameAndFeeds) {

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

        public ViewHolder(View convertView) {

            this.textDate = (TextView) convertView
                    .findViewById(R.id.textview_feed_date);
            this.textHour = (TextView) convertView
                    .findViewById(R.id.textview_feed_hour);
            this.textMessage = (TextView) convertView
                    .findViewById(R.id.textview_chat_message);
            this.profileImage = (ImageView) convertView
                    .findViewById(R.id.imageview_image);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_feeds, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.holder_id, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder_id);
        }
        convertView.setTag(R.id.venue_name_and_feeds, venueNameAndFeeds);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");        
        SimpleDateFormat dateOnly = new SimpleDateFormat("MMM dd");        
        SimpleDateFormat hourOnly = new SimpleDateFormat("hh:mm");        
        try {
            date = simpleDateFormat.parse(messages.get(position).getDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
        holder.textDate.setText(dateOnly.format(date));
        holder.textHour.setText(hourOnly.format(date));
        holder.textMessage.setText(AppCAP.cleanResponseString(messages.get(
                position).getEntryText()));
        // Display image
        if (AppCAP.isLoggedIn()) {
            imageLoader.DisplayImage(messages.get(position).getAuthorPhotoUrl(),
                    holder.profileImage, R.drawable.default_avatar50, 70);
        } else {
            imageLoader.DisplayImage("", holder.profileImage,
                    R.drawable.default_avatar50_login, 70);
        }

        return convertView;
    }

    public void setNewData(ArrayList<Feed> messages2) {
        this.messages = messages2;
    }

}
