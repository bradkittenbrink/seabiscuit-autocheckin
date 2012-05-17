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
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.imageutil.ImageLoader;

public class MyFavouritePlacesAdapter extends BaseAdapter {

	private ArrayList<Venue> venues;
	private LayoutInflater inflater;
	public ImageLoader imageLoader;

	public MyFavouritePlacesAdapter(Activity context, ArrayList<Venue> venues) {

		this.inflater = context.getLayoutInflater();
		this.imageLoader = new ImageLoader(context.getApplicationContext());

		if (venues != null) {
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

		public ImageView image;
		public TextView textVenueName;
		public TextView textCheckinsCount;

		public ViewHolder(View convertView) {

			this.textVenueName = (TextView) convertView.findViewById(R.id.textview_place);
			this.image = (ImageView) convertView.findViewById(R.id.imageview_image);
			this.textCheckinsCount = (TextView) convertView.findViewById(R.id.textview_checkin);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_favorite_places, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textVenueName.setText(AppCAP.cleanResponseString(venues.get(position).getName()));

		if (venues.get(position).getCheckinsCount() > 1) {
			holder.textCheckinsCount.setText(venues.get(position).getCheckinsCount() + " Checkins");
		} else {
			holder.textCheckinsCount.setText(venues.get(position).getCheckinsCount() + " Checkin");
		}

		// Try to load image
		imageLoader.DisplayImage(venues.get(position).getPhotoUrl(), holder.image, R.drawable.picture_coming_soon, 70);

		return convertView;
	}

}
