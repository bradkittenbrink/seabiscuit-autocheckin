package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;

public class MyVenueFeedsAdapter extends BaseAdapter {


    private ArrayList<VenueNameAndFeeds> venueNameAndFeeds;

    private LayoutInflater inflater;

    private int localUserId;
    private Activity context;

    public MyVenueFeedsAdapter(Activity context, ArrayList<VenueNameAndFeeds> venueNameAndFeeds) {
        this.context = context;
        this.inflater = context.getLayoutInflater();
        this.localUserId = AppCAP.getLoggedInUserId();

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

        public TextView textDate;
        public TextView textMessage;
        public ListView listFeeds;
        public Button removeButton;

        public ViewHolder(View convertView) {

            this.textDate = (TextView) convertView
                    .findViewById(R.id.textview_last_checkedin_date);  
            this.textMessage = (TextView) convertView
                    .findViewById(R.id.textview_venue_name);
            this.listFeeds = (ListView) convertView
                    .findViewById(R.id.item_venue_feeds_listview);
            this.removeButton = (Button) convertView
                    .findViewById(R.id.btn_remove);
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
        MyFeedsAdapter adapter;
        adapter = (MyFeedsAdapter) holder.listFeeds.getAdapter();
        if (adapter == null) {
            adapter = new MyFeedsAdapter(context, messages, venueNameAndFeeds.get(position));            
            holder.listFeeds.setAdapter(adapter);
        } else {      
            adapter.setNewData(messages);
            adapter.notifyDataSetChanged();
        }
        if (messages.size() == 0) {
            holder.listFeeds.setVisibility(View.GONE);
        } else {
            holder.listFeeds.setVisibility(View.VISIBLE);
        }

        /**
         For an unknown reason the android:layout_height="wrap_content" doesn't work for the feeds list view
         So the list view is calculated and set 
         */
        ListAdapter listAdapter = holder.listFeeds.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return convertView;
        }
        
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, holder.listFeeds);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        
        ViewGroup.LayoutParams params = holder.listFeeds.getLayoutParams();
        params.height = totalHeight + (holder.listFeeds.getDividerHeight() * (listAdapter.getCount() - 1));
        holder.listFeeds.setLayoutParams(params);
        holder.listFeeds.setItemsCanFocus(true);
        holder.listFeeds.requestLayout();
        return convertView;
    }

}
