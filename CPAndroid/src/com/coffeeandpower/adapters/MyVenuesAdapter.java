package com.coffeeandpower.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.Venue;

public class MyVenuesAdapter extends BaseAdapter{

	private ArrayList<Venue> venues;
	private LayoutInflater inflater;
	
	public MyVenuesAdapter(Activity context, ArrayList<Venue> venues){
		
		this.inflater = context.getLayoutInflater();
		
		if (venues!=null){
			this.venues = venues;
		} else {
			this.venues = new ArrayList<Venue>();
		}
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

		public ViewHolder(View convertView){
		
			this.textAddress = (TextView) convertView.findViewById(R.id.text_address);
			this.textDistance = (TextView) convertView.findViewById(R.id.text_distance);
			this.textName = (TextView) convertView.findViewById(R.id.text_name);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder; 
		
		if (convertView == null){
			convertView = inflater.inflate(R.layout.item_venue_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		double distance = venues.get(position).getDistance();
		String distanceS = "";
		
		if (distance < 100){
			distanceS = distance + "m";
		} else {
			DecimalFormat oneDForm = new DecimalFormat("#.#");
			double d = Double.valueOf(oneDForm.format(distance/1000));
			distanceS = d + "km";
		}
		
		
		holder.textAddress.setText(venues.get(position).getAddress());
		holder.textDistance.setText(distanceS);
		holder.textName.setText(venues.get(position).getName());
		
		return convertView;
	}

}
