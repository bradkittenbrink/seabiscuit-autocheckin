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

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.imageutil.ImageLoader;

public class MyUsersAdapter extends BaseAdapter{

	private ArrayList<MapUserData> mudArray;
	private LayoutInflater inflater;
	public ImageLoader imageLoader;

	private int myLat;
	private int myLng;

	public MyUsersAdapter(Activity context, ArrayList<MapUserData> mudArray, int myLat, int myLng){

		this.inflater = context.getLayoutInflater();
		this.myLat = myLat;
		this.myLng = myLng;
		this.imageLoader=new ImageLoader(context.getApplicationContext());

		if (mudArray!=null){
			this.mudArray = mudArray;
		} else {
			this.mudArray = new ArrayList<MapUserData>();
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
		public TextView textStatus;
		public TextView textVenueName;
		public TextView textDistance;
		public TextView textCheckinsCount;
		public TextView textGrayLine;

		public ImageView profileImage;

		public ViewHolder(View convertView){

			this.textCheckinsCount = (TextView) convertView.findViewById(R.id.textview_checkin);
			this.textDistance = (TextView) convertView.findViewById(R.id.textview_how_far);
			this.textStatus = (TextView) convertView.findViewById(R.id.textview_comment);
			this.textVenueName = (TextView) convertView.findViewById(R.id.textview_place);
			this.textNickName = (TextView) convertView.findViewById(R.id.textview_persone_nickname);
			this.textGrayLine = (TextView) convertView.findViewById(R.id.textview_days);
			this.profileImage = (ImageView) convertView.findViewById(R.id.imageview_image);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder; 

		if (convertView == null){
			convertView = inflater.inflate(R.layout.item_list_about_person, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		//String checkStr = mudArray.get(position).getCheckInCount() == 1 ? mudArray.get(position).getCheckInCount() + " Checkin" : mudArray.get(position).getCheckInCount() + " Checkins";

		holder.textNickName.setText(mudArray.get(position).getNickName());
		holder.textStatus.setText(AppCAP.cleanResponseString(mudArray.get(position).getStatusText()));
		//holder.textCheckinsCount.setText(checkStr);
		holder.textVenueName.setText(AppCAP.cleanResponseString(mudArray.get(position).getVenueName()));

		// Deafult gay line state is gone
		holder.textGrayLine.setVisibility(View.GONE);

		float[] results = new float[1];
		Location.distanceBetween(myLat / 1E6, myLng / 1E6, mudArray.get(position).getLat(), mudArray.get(position).getLng(), results);

		String distanceS = "";
		DecimalFormat oneDForm = new DecimalFormat("#.#");
		if (results[0] < 100){
			float d = Float.valueOf(oneDForm.format(results[0]));
			distanceS = d + "m away";
		} else {
			float d = Float.valueOf(oneDForm.format(results[0]/1000));
			distanceS = d + "km away";
		}
		holder.textDistance.setText(distanceS);


		// Check if we have hereNow user
		if (mudArray.get(position).getCheckedIn()==1 && mudArray.get(position).isFirstInList()){
			holder.textGrayLine.setText("Checked In Now");
			holder.textGrayLine.setVisibility(View.VISIBLE);
		} 
		if (mudArray.get(position).getCheckedIn()==0 && mudArray.get(position).isFirstInList()){
			// it was in last seven days
			holder.textGrayLine.setText("Last 7 Days");
			holder.textGrayLine.setVisibility(View.VISIBLE);
		}


		// Try to load profile image
		imageLoader.DisplayImage(mudArray.get(position).getFileName(), holder.profileImage);


		//Log.d("LOG", "cehc: " + mudArray.get(position).getNickName() + "  :" + mudArray.get(position).isFirstInList() + ":" + mudArray.get(position).getCheckedIn());
		return convertView;
	}

}
