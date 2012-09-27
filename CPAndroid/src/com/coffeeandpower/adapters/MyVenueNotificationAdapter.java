package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyVenuesAdapter.ViewHolder;
import com.coffeeandpower.cont.VenueSmart;

public class MyVenueNotificationAdapter extends BaseAdapter {

    private final String TAG = "NotificationsAdapter";
    
    private ArrayList<VenueSmart> venues;
    private LayoutInflater inflater;

    public MyVenueNotificationAdapter(Activity context, ArrayList<VenueSmart> venues) {

        this.inflater = context.getLayoutInflater();

        if (venues != null) {
            this.venues = venues;
        } else {
            this.venues = new ArrayList<VenueSmart>();
        }
    }
    
    
    public void setNewData(ArrayList<VenueSmart>newVenues) {
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
        public TextView textAddress2;
        public TextView textHiddenVenueId;
        public ToggleButton toggleButton; 
        
        public int myVenueId;

        public ViewHolder(View convertView) {

            this.textAddress = (TextView) convertView.findViewById(R.id.text_address);
            this.textName = (TextView) convertView.findViewById(R.id.text_name);
            this.toggleButton = (ToggleButton) convertView.findViewById(R.id.autoCheckinToggleButton);
            
            this.toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    //Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityPlaceDetails.class);
                    //intent.putExtra("venueSmart", (VenueSmart) adapterPlaces.getItem(position));
                    //We are sending the whole place object so we won't need the 4sqId separately
                    //intent.putExtra("foursquare_id", arrayVenues.get(position).getFoursquareId());
                    //I don't know what data is, but I don't think we will need
                    //intent.putExtra("coords", data);
                    //startActivity(intent);
                    
                    
                    
                    Log.d("AutoCheckin","User clicked toggle button for venue: " + myVenueId);                  

                    if (!toggleButton.isChecked()) {
                        Log.d("AutoCheckin","Disabling auto-checkin for venue: " + myVenueId);
                        AppCAP.disableAutoCheckinForVenue(myVenueId);
                    }
                    else {
                        Log.d("AutoCheckin","Enabling auto-checkin for venue: " + myVenueId);
                        AppCAP.enableAutoCheckinForVenue(myVenueId);
                    }
                }
            });
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_venue_autocheckin_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int venueId = venues.get(position).getVenueId();
        
        holder.textAddress.setText(venues.get(position).getAddress());
        holder.textName.setText(venues.get(position).getName());
        holder.myVenueId = venueId;
        
        holder.toggleButton.setChecked(AppCAP.isVenueAutoCheckinEnabled(venueId));

        return convertView;
    }

}
