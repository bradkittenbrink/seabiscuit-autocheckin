package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.VenueSmart;

public class MyVenuesAdapter extends BaseAdapter {

    private ArrayList<VenueSmart> venues;
    private LayoutInflater inflater;

    public MyVenuesAdapter(Activity context, ArrayList<VenueSmart> venues) {

        this.inflater = context.getLayoutInflater();

        if (venues != null) {
            this.venues = venues;
        } else {
            this.venues = new ArrayList<VenueSmart>();
        }
    }

    public void setNewData(ArrayList<VenueSmart> newVenues) {
        this.venues = newVenues;
    }

    @Override
    public int getCount() {
        return venues.size();
    }

    @Override
    public Object getItem(int position) {
        return venues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        public TextView textName;
        public TextView textAddress;
        public TextView textDistance;

        public ViewHolder(View convertView) {

            this.textAddress = (TextView) convertView
                    .findViewById(R.id.text_address);
            this.textDistance = (TextView) convertView
                    .findViewById(R.id.text_distance);
            this.textName = (TextView) convertView.findViewById(R.id.text_name);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_venue_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textAddress.setText(venues.get(position).getAddress());

        holder.textDistance.setText(venues.get(position).getFoursquareId()
                .equals("add_place") ? "" : RootActivity
                .formatToMetricsOrImperial(venues.get(position)
                        .getDistanceFloat()));

        holder.textName.setText(venues.get(position).getName());
        if (venues.get(position).getFoursquareId().equals("add_place"))
            holder.textName.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);

        return convertView;
    }

}
