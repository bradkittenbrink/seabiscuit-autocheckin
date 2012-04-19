package com.coffeeandpower.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.VenueSmart;

public class MyPlacesAdapter extends BaseAdapter{

	private ArrayList<VenueSmart> venues;
	private LayoutInflater inflater;
	
	private double myLat;
	private double myLng;
	
	public MyPlacesAdapter(Activity context, ArrayList<VenueSmart> venues, double myLat, double myLng){
		
		this.inflater = context.getLayoutInflater();
		this.myLat = myLat;
		this.myLng = myLng;
		
		if (venues!=null){
			this.venues = venues;
		} else {
			this.venues = new ArrayList<VenueSmart>();
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
		
		public ImageView image;
		public TextView textVenueName;
		public TextView textAddress;
		public TextView textDistance;
		public TextView textCheckins;

		public ViewHolder(View convertView){
		
			this.textAddress = (TextView) convertView.findViewById(R.id.textview_place_adderes);
			this.textDistance = (TextView) convertView.findViewById(R.id.textview_how_far);
			this.textVenueName = (TextView) convertView.findViewById(R.id.textview_place_name);
			this.textCheckins = (TextView) convertView.findViewById(R.id.textview_how_many_people);
			this.image = (ImageView) convertView.findViewById(R.id.imageview_image_places);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder; 
		
		if (convertView == null){
			convertView = inflater.inflate(R.layout.item_list_places, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		float[] results = new float[1];
		Location.distanceBetween(myLat, myLng, venues.get(position).getLat(), venues.get(position).getLng(), results);

		String distanceS = "";
		DecimalFormat oneDForm = new DecimalFormat("#.#");
		if (results[0] < 100){
			float d = Float.valueOf(oneDForm.format(results[0]));
			distanceS = d + "m";
		} else {
			float d = Float.valueOf(oneDForm.format(results[0]/1000));
			distanceS = d + "km";
		}
		
		holder.textDistance.setText(distanceS);
		holder.textAddress.setText(venues.get(position).getAddress());
		holder.textVenueName.setText(venues.get(position).getName());
		
		holder.textCheckins.setText(venues.get(position).getCheckinsForInterval() + " checkins");
		
		return convertView;
	}

}
