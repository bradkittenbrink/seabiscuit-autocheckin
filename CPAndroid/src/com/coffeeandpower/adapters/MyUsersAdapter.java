package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coffeeandpower.R;
import com.coffeeandpower.cont.MapUserData;

public class MyUsersAdapter extends BaseAdapter{

	private ArrayList<MapUserData> mudArray;
	private LayoutInflater inflater;
	
	public MyUsersAdapter(Activity context, ArrayList<MapUserData> mudArray){
		
		this.inflater = context.getLayoutInflater();
		
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

		public ViewHolder(View convertView){
		
			this.textCheckinsCount = (TextView) convertView.findViewById(R.id.textview_checkin);
			this.textDistance = (TextView) convertView.findViewById(R.id.textview_how_far);
			this.textStatus = (TextView) convertView.findViewById(R.id.textview_comment);
			this.textVenueName = (TextView) convertView.findViewById(R.id.textview_place);
			this.textNickName = (TextView) convertView.findViewById(R.id.textview_persone_nickname);
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

		
		
		return convertView;
	}

}
