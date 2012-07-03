package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.imageutil.ImageLoader;

public class MyPlacesAdapter extends BaseAdapter {

	private ArrayList<VenueSmart> venues;
	private LayoutInflater inflater;
	public ImageLoader imageLoader;

	private double myLat;
	private double myLng;

	public MyPlacesAdapter(Activity context, ArrayList<VenueSmart> venues, double myLat, double myLng) {

		this.inflater = context.getLayoutInflater();
		this.myLat = myLat;
		this.myLng = myLng;
		this.imageLoader = new ImageLoader(context.getApplicationContext());

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

		public ImageView image;
		public TextView textVenueName;
		public TextView textAddress;
		public TextView textDistance;
		public TextView textCheckins;

		public ViewHolder(View convertView) {

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

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_places, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textDistance.setText(
				RootActivity.getDistanceBetween(myLat, myLng,
						venues.get(position).getLat(), venues.get(position).getLng(), false));
		
		holder.textAddress.setText(AppCAP.cleanResponseString(venues.get(position).getAddress()));
		holder.textVenueName.setText(AppCAP.cleanResponseString(venues.get(position).getName()));

		if (venues.get(position).getCheckins() != 0) {
			String sc = venues.get(position).getCheckins() == 1 ? "1 person here now" : venues.get(position).getCheckins()
					+ " people here now";
			holder.textCheckins.setText(sc);
		} else {
			holder.textCheckins.setText(venues.get(position).getCheckinsForInterval() + " people this week");
		}

		// Try to load image
		imageLoader.DisplayImage(venues.get(position).getPhotoURL(), holder.image, R.drawable.picture_coming_soon, 70);

		return convertView;
	}

}
