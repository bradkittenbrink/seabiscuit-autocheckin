package com.coffeeandpower.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
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
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.fragments.FragmentPostableFeedVenue;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.Utils;

public class MyPostableVenuesAdapter extends BaseAdapter {

    private LayoutInflater inflater; 
    private int localUserId; 
    private Activity context;
    private ArrayList<VenueNameAndFeeds> listVenues;
    private int TAG_VENUE_ID = 1; 
    private int TAG_VENUE_NAME = 2; 

    public MyPostableVenuesAdapter(FragmentPostableFeedVenue fragmentPostableFeedVenue, ArrayList<VenueNameAndFeeds> listVenues) {
        this.context = fragmentPostableFeedVenue.getActivity();
        this.inflater = this.context.getLayoutInflater(); 
        this.listVenues = listVenues;
    }

    @Override
    public int getCount() {
        return listVenues.size();
    }

    @Override
    public Object getItem(int position) {
        return listVenues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {

        public TextView text_venue_name;
        private RelativeLayout venue_item;

        public ViewHolder(View convertView) {

            this.text_venue_name = (TextView) convertView
                    .findViewById(R.id.text_venue_name);
            this.venue_item =(RelativeLayout) convertView
                    .findViewById(R.id.venue_item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            /*
             * 
             * ajouter le nom et l'id sur onclick
             */
            convertView = inflater.inflate(R.layout.item_postable_venues_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.holder_id, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder_id);
        }
        holder.venue_item.setTag(R.id.venue_id ,listVenues.get(position).getVenueId());
        holder.venue_item.setTag(R.id.venue_name,listVenues.get(position).getName());
        holder.venue_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppCAP.isLoggedIn()) {
                    context.showDialog(RootActivity.DIALOG_MUST_BE_A_MEMBER);
                } else {
                    int venue_id = (Integer) v.getTag(R.id.venue_id);
                    String venue_name = (String) v.getTag(R.id.venue_name);
                    Intent intent = new Intent(context,
                            ActivityVenueFeeds.class);
                    intent.putExtra("venue_id", venue_id);
                    intent.putExtra("venue_name", venue_name);
                    intent.putExtra("caller", "postable_venues");
                    intent.putExtra("fragment", R.id.tab_fragment_area_feeds_for_one_venue);
                    context.startActivity(intent);
                }
            }
        });
                
        holder.text_venue_name.setText(AppCAP.cleanResponseString(listVenues.get(
                position).getName()));

        return convertView;
    }


    public void setNewData(ArrayList<VenueNameAndFeeds> listVenues) {
        this.listVenues = listVenues;
    }

}
