package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.coffeeandpower.R;

public class MyNotificationsListAdapter extends BaseAdapter {

	private ArrayList<String> mudArray;
	private LayoutInflater inflater;

	public MyNotificationsListAdapter(Activity context, ArrayList<String> mudArray) {

		this.inflater = context.getLayoutInflater();

		if (mudArray != null) {
			this.mudArray = mudArray;
		} else {
			this.mudArray = new ArrayList<String>();
		}
	}
	
	public void setNewData(ArrayList<String>newData) {
		this.mudArray = newData;
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
		public TextView listItemText;

		public ViewHolder(View convertView) {

			this.listItemText = (TextView) convertView.findViewById(R.id.text_name);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_notifications_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.listItemText.setText(mudArray.get(position));




		return convertView;
	}
}