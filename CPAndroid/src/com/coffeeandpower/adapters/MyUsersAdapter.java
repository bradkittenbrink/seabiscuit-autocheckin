package com.coffeeandpower.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.imageutil.ImageLoader;

import java.util.ArrayList;

public class MyUsersAdapter extends BaseAdapter {

    private ArrayList<UserSmart> mudArray;
    private LayoutInflater inflater;
    public ImageLoader imageLoader;

    private double myLat = 0;
    private double myLng = 0;

    public MyUsersAdapter(Activity context, ArrayList<UserSmart> mudArray) {

        this.inflater = context.getLayoutInflater();
        this.imageLoader = new ImageLoader(context.getApplicationContext());

        if (mudArray != null) {
            this.mudArray = mudArray;
        } else {
            this.mudArray = new ArrayList<UserSmart>();
        }
    }

    public MyUsersAdapter(Activity context, ArrayList<UserSmart> mudArray,
            double myLat, double myLng) {

        this.inflater = context.getLayoutInflater();
        this.myLat = myLat;
        this.myLng = myLng;
        this.imageLoader = new ImageLoader(context.getApplicationContext());

        if (mudArray != null) {
            this.mudArray = mudArray;
        } else {
            this.mudArray = new ArrayList<UserSmart>();
        }
    }

    public void setNewData(ArrayList<UserSmart> newUsers) {
        this.mudArray = newUsers;
    }

    @Override
    public int getCount() {
        return mudArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mudArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {

        public TextView textNickName;
        public TextView textStatus;
        public TextView textVenueName;
        public TextView textCheckinsCount;
        public TextView textJobName;

        public ImageView profileImage;

        public ViewHolder(View convertView) {

            this.textCheckinsCount = (TextView) convertView
                    .findViewById(R.id.textview_checkin);
            this.textStatus = (TextView) convertView
                    .findViewById(R.id.textview_comment);
            this.textVenueName = (TextView) convertView
                    .findViewById(R.id.textview_place);
            this.textNickName = (TextView) convertView
                    .findViewById(R.id.textview_persone_nickname);
            this.profileImage = (ImageView) convertView
                    .findViewById(R.id.imageview_image);
            this.textJobName = (TextView) convertView
                    .findViewById(R.id.textview_major_job);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        UserSmart current = mudArray.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_about_person,
                    null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Display image
        if (AppCAP.isLoggedIn()) {
            holder.textNickName.setText(AppCAP.cleanResponseString(current.getNickName()));
            imageLoader.DisplayImage(current.getFileName(),
                    holder.profileImage, R.drawable.default_avatar50, 70);
            
            // Display major job category
            String jobName = current.getMajorJobCategory();
            if (jobName != null && jobName.length() > 1)
                jobName = jobName.substring(0, 1).toUpperCase()
                        + jobName.substring(1);
            holder.textJobName.setText(jobName);
            
            // Display status text
            if (current.getStatusText() != null
                    && current.getStatusText().length() > 0) {
                holder.textStatus.setText("\""
                        + AppCAP.cleanResponseString(current.getStatusText()) + "\"");
            } else {
                holder.textStatus.setText("");
            }
        } else {
            holder.textNickName.setText("Name Hidden");
            imageLoader.DisplayImage("", holder.profileImage,
                    R.drawable.default_avatar50_login, 70);
        }

        if (current.getVenueName() != null
                && current.getVenueName().length() > 0) { 
            holder.textVenueName.setText("@" + AppCAP.cleanResponseString(current.getVenueName()));
        } else {
            holder.textVenueName.setText("");
        }

        return convertView;
    }

}
