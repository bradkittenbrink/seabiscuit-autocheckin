package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.utils.Utils;

public class MyChatAdapter extends BaseAdapter {

	private ArrayList<ChatMessage> messages;

	private LayoutInflater inflater;

	private int localUserId;

	public MyChatAdapter(Activity context, ArrayList<ChatMessage> messages) {

		this.inflater = context.getLayoutInflater();
		this.localUserId = AppCAP.getLoggedInUserId();

		if (messages != null) {
			this.messages = messages;
		} else {
			this.messages = new ArrayList<ChatMessage>();
		}
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public static class ViewHolder {

		public TextView textDate;
		public TextView textMessage;

		public ViewHolder(View convertView) {

			this.textDate = (TextView) convertView.findViewById(R.id.textview_chat_date);
			this.textMessage = (TextView) convertView.findViewById(R.id.textview_chat_message);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_chat, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		if (messages.get(position).getUserId() == localUserId) {
			holder.textMessage.setBackgroundResource(R.drawable.baloon_1);
			params.gravity = Gravity.RIGHT;
			holder.textMessage.setLayoutParams(params);
		} else {
			holder.textMessage.setBackgroundResource(R.drawable.baloon_2);
			params.gravity = Gravity.LEFT;
			holder.textMessage.setLayoutParams(params);
		}

		holder.textMessage.setPadding(20, 6, 20, 9);

		holder.textDate.setText(Utils.getDateFromEpoch(messages.get(position).getDate()));
		holder.textMessage.setText(AppCAP.cleanResponseString(messages.get(position).getEntryText()));

		return convertView;
	}

}
