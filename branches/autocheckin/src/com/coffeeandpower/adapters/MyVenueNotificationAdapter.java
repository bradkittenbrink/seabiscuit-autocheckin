package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyVenuesAdapter.ViewHolder;
import com.coffeeandpower.cont.VenueSmart;

public class MyVenueNotificationAdapter extends BaseAdapter {

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
		public ToggleButton toggleButton; 

		public ViewHolder(View convertView) {

			this.textAddress = (TextView) convertView.findViewById(R.id.text_address);
			this.textName = (TextView) convertView.findViewById(R.id.text_name);
			this.toggleButton = (ToggleButton) convertView.findViewById(R.id.autoCheckinToggleButton);
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

		holder.textAddress.setText(venues.get(position).getAddress());
		holder.textName.setText(venues.get(position).getName());
		

		return convertView;
	}

}