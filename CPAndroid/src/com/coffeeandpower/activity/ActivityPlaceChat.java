package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyPlaceChatAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueChatEntry;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.views.CustomFontView;

public class ActivityPlaceChat extends RootActivity {

	private ListView list;

	private DataHolder result;

	private Executor exe;

	private String venueId;
	private String lastChatIDString;

	{
		venueId = "";
		lastChatIDString = "0";
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_places_chat);

		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			venueId = extras.getString("venue_id");

			((CustomFontView) findViewById(R.id.textview_places_chat_name)).setText(extras.getString("venue_name"));
		}

		// Executor
		exe = new Executor(ActivityPlaceChat.this);
		exe.setExecutorListener(new ExecutorInterface() {
			@Override
			public void onErrorReceived() {
				// onBackPressed();
			}

			@Override
			public void onActionFinished(int action) {
				result = exe.getResult();

				switch (action) {
				case Executor.HANDLE_VENUE_CHAT:
					if (result != null && result.getObject() != null && (result.getObject() instanceof ArrayList<?>)) {
						ArrayList<Object> tempArray = (ArrayList<Object>) result.getObject();
						populateList(tempArray);
					}
					break;
				case Executor.HANDLE_SEND_VENUE_CHAT:
					break;
				}
			}
		});

		// ListView with chat entries
		list = (ListView) findViewById(R.id.listview_places_chat);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setStackFromBottom(true);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

			}
		});
	}

	@SuppressWarnings("unchecked")
	private void populateList(ArrayList<Object> chatArray) {
		if (chatArray.size() == 4) {
			if (chatArray.get(3) instanceof ArrayList<?>) {
				list.setAdapter(new MyPlaceChatAdapter(this, (ArrayList<VenueChatEntry>) chatArray.get(3)));

				// Calculate number of users in chat
				HashSet<String> usersIDs = new HashSet<String>();
				for (VenueChatEntry entry : (ArrayList<VenueChatEntry>) chatArray.get(3)) {
					if (entry.getSystemType() != null && !entry.getSystemType().equals("checkin"))
						usersIDs.add(entry.getUserId());

				}
				((CustomFontView) findViewById(R.id.textview_chat_count)).setText(usersIDs.size() == 0 ? "" : usersIDs.size() + "");
			}
		}
	}

	public void onClickSend(View v) {
		EditText editText = (EditText) findViewById(R.id.edittext_places_chat);
		String input = editText.getText().toString();

		if (input != null && input.length() > 0) {
			exe.venueChat(venueId, "0", input, true); // "0" ???
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Get venue chat
		exe.venueChat(venueId, lastChatIDString, "", false);
	}

	public void onClickBack(View v) {
		onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
