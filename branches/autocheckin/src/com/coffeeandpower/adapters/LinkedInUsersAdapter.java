package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.google.code.linkedinapi.schema.Person;
import com.coffeeandpower.imageutil.ImageLoader;

public class LinkedInUsersAdapter extends BaseAdapter {

    private ArrayList<Person> mudArray;
    private ArrayList<Person> selectedArray;
    private LayoutInflater inflater;
    public ImageLoader imageLoader;

    public LinkedInUsersAdapter(Activity context, ArrayList<Person> mudArray, ArrayList<Person> selectedArray) {

        this.inflater = context.getLayoutInflater();
        this.imageLoader = new ImageLoader(context.getApplicationContext());

        if (mudArray != null) {
            this.mudArray = mudArray;
        } else {
            this.mudArray = new ArrayList<Person>();
        }
        if (selectedArray != null) {
            this.selectedArray = selectedArray;
        } else {
            this.selectedArray = new ArrayList<Person>();
        }
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
        public ImageView profileImage;
        private LinearLayout gray_layout;

        public ViewHolder(View convertView) {

            this.textNickName = (TextView) convertView
                    .findViewById(R.id.textview_persone_nickname);
            this.profileImage = (ImageView) convertView
                    .findViewById(R.id.imageview_image);
            this.gray_layout = (LinearLayout) convertView
                    .findViewById(R.id.gray_layout);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (Constants.debugLog)
            Log.d("MyUsersAdapter",
                    "getView for "
                            + AppCAP.cleanResponseString(mudArray.get(position)
                                    .getFirstName()) + ", image: "
                            + mudArray.get(position).getPictureUrl());

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_about_linkedin_person,
                    null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Display image
        if (AppCAP.isLoggedIn()) {
            holder.textNickName.setText(AppCAP.cleanResponseString(mudArray
                    .get(position).getLastName() + " " + mudArray
                    .get(position).getFirstName()));
            if (selectedArray.contains((Person) mudArray.get(position))) {
                holder.gray_layout.setBackgroundResource(R.drawable.button_turquoise_a);
            } else {
                holder.gray_layout.setBackgroundResource(0);
            }
            imageLoader.DisplayImage(mudArray.get(position).getPictureUrl(),
                    holder.profileImage, R.drawable.default_avatar50, 70);
        } else {
            holder.textNickName.setText("Name Hidden");
            imageLoader.DisplayImage("", holder.profileImage,
                    R.drawable.default_avatar50_login, 70);
        }
        return convertView;
    }


}
