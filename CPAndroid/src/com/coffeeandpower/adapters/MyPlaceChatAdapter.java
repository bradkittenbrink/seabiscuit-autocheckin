package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cont.VenueChatEntry;
import com.coffeeandpower.imageutil.ImageLoader;

public class MyPlaceChatAdapter extends BaseAdapter {

    private ArrayList<VenueChatEntry> messages;

    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public MyPlaceChatAdapter(Activity context,
            ArrayList<VenueChatEntry> messages) {
        this.inflater = context.getLayoutInflater();
        imageLoader = new ImageLoader(context);

        if (messages != null) {
            this.messages = messages;
        } else {
            this.messages = new ArrayList<VenueChatEntry>();
        }
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
        public ImageView image;
        public TextView textMessage;

        public ViewHolder(View convertView) {
            this.image = (ImageView) convertView
                    .findViewById(R.id.imageview_places_chat);
            this.textMessage = (TextView) convertView
                    .findViewById(R.id.textview_places_chat);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater
                    .inflate(R.layout.item_list_places_chat, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textMessage.setText(AppCAP.cleanResponseString(messages.get(
                position).getEntry()));

        if (messages.get(position).getSystemType() != null
                && messages.get(position).getSystemType().equals("checkin")) {
            imageLoader.DisplayImage(messages.get(position)
                    .getSystemData_fineName(), holder.image,
                    R.drawable.default_avatar25, 30);
            holder.textMessage.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        } else {
            imageLoader.DisplayImage(messages.get(position).getFileName(),
                    holder.image, R.drawable.default_avatar25, 30);
            holder.textMessage.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }

        return convertView;
    }

}
