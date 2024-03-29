package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.imageutil.ImageLoader;

public class MyUsersAdapter extends BaseAdapter {

	private ArrayList<UserSmart> mudArray;
	private LayoutInflater inflater;
	public ImageLoader imageLoader;

	private double myLat = 0;
	private double myLng = 0;
	
	public MyUsersAdapter(Activity context, ArrayList<UserSmart> mudArray) {

		this.inflater = context.getLayoutInflater();
		this.imageLoader = new ImageLoader(context.getApplicationContext());

		if (mudArray != null) {
			this.mudArray = mudArray;
		} else {
			this.mudArray = new ArrayList<UserSmart>();
		}
	}

	public MyUsersAdapter(Activity context, ArrayList<UserSmart> mudArray, double myLat, double myLng) {

		this.inflater = context.getLayoutInflater();
		this.myLat = myLat;
		this.myLng = myLng;
		this.imageLoader = new ImageLoader(context.getApplicationContext());

		if (mudArray != null) {
			this.mudArray = mudArray;
		} else {
			this.mudArray = new ArrayList<UserSmart>();
		}
	}
	
	public void setNewData(ArrayList<UserSmart>newUsers) {
		this.mudArray = newUsers;
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
		public TextView textJobName;

		public ImageView profileImage;

		public ViewHolder(View convertView) {

			this.textCheckinsCount = (TextView) convertView.findViewById(R.id.textview_checkin);
			this.textDistance = (TextView) convertView.findViewById(R.id.textview_how_far);
			this.textStatus = (TextView) convertView.findViewById(R.id.textview_comment);
			this.textVenueName = (TextView) convertView.findViewById(R.id.textview_place);
			this.textNickName = (TextView) convertView.findViewById(R.id.textview_persone_nickname);
			this.textGrayLine = (TextView) convertView.findViewById(R.id.textview_days);
			this.profileImage = (ImageView) convertView.findViewById(R.id.imageview_image);
			this.textJobName = (TextView) convertView.findViewById(R.id.textview_major_job);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (Constants.debugLog)
			Log.d("MyUsersAdapter","getView for " + AppCAP.cleanResponseString(mudArray.get(position).getNickName()) + ", image: " + mudArray.get(position).getFileName() );

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_about_person, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Display image
		if (AppCAP.isLoggedIn()) {
			holder.textNickName.setText(AppCAP.cleanResponseString(mudArray.get(position).getNickName()));
			imageLoader.DisplayImage(mudArray.get(position).getFileName(), holder.profileImage, R.drawable.default_avatar50, 70);
		} else {
			holder.textNickName.setText("Name Hidden");
			imageLoader.DisplayImage("", holder.profileImage, R.drawable.default_avatar50_login, 70);
		}

		// Display status text
		if (mudArray.get(position).getStatusText() != null && mudArray.get(position).getStatusText().length() > 0) {
			holder.textStatus.setText("\"" + AppCAP.cleanResponseString(mudArray.get(position).getStatusText()) + "\"");
		} else {
			holder.textStatus.setText("");
		}
		holder.textVenueName.setText(AppCAP.cleanResponseString(mudArray.get(position).getVenueName()));

		// Display major job category
		String jobName = mudArray.get(position).getMajorJobCategory();
		if (jobName != null && jobName.length() > 1)
			jobName = jobName.substring(0, 1).toUpperCase() + jobName.substring(1);
		holder.textJobName.setText(jobName);

		// Deafult gray line state is gone
		holder.textGrayLine.setVisibility(View.GONE);
		//Not the best check since 0, 0 is a valid lat, long, but there is no coffee or power off the coast of Africa so we should be good
		if(myLat == 0 || myLng == 0)
		{
			//If we have no position fill that space with something else
		}
		else
		{
			holder.textDistance.setText(RootActivity.getDistanceBetween(myLat, myLng, mudArray.get(position).getLat(), mudArray.get(position)
				.getLng()));
		}

		// Check if we have hereNow user
		if (mudArray.get(position).getCheckedIn() == 1 && mudArray.get(position).isFirstInList()) {
			holder.textGrayLine.setText("Checked In Now");
			holder.textGrayLine.setVisibility(View.VISIBLE);
		}
		if (mudArray.get(position).getCheckedIn() == 0 && mudArray.get(position).isFirstInList()) {
			// it was in last seven days
			holder.textGrayLine.setText("Last 7 Days");
			holder.textGrayLine.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

}
