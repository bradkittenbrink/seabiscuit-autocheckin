package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyChatAdapter;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;

public class ActivityChat extends RootActivity {

    private static final int HANDLE_GET_HISTORY = 1444;
    private static final int HANDLE_SEND_MESSAGE = 1445;

    private ProgressDialog progress;

    private DataHolder result;

    private ListView listChat;

    private int userId;
    private int localUserId;

    private Handler handler = new Handler() {

	@Override
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);

	    progress.dismiss();

	    switch (msg.what) {

	    case AppCAP.HTTP_ERROR:
		new CustomDialog(ActivityChat.this, "Error", result.getResponseMessage()).show();
		break;

	    case HANDLE_GET_HISTORY:
		if (result.getObject() instanceof ArrayList<?>) {

		    ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) result.getObject();
		    MyChatAdapter adapter = new MyChatAdapter(ActivityChat.this, messages);
		    listChat.setAdapter(adapter);
		    listChat.setSelection(adapter.getCount() - 1);
		}
		break;

	    case HANDLE_SEND_MESSAGE:
		Toast.makeText(ActivityChat.this, "Sent...", Toast.LENGTH_SHORT).show();
		((EditText) findViewById(R.id.edittext_chat)).setText("");
		getChatHistory();
		break;
	    }
	}

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_chat);

	// Views
	progress = new ProgressDialog(this);
	progress.setMessage("Loading chat...");
	listChat = (ListView) findViewById(R.id.listview_chat);

	// Get userId form intent
	localUserId = AppCAP.getLoggedInUserId();
	Bundle bundle = getIntent().getExtras();
	if (bundle != null) {

	    userId = bundle.getInt("user_id");
	    String nickName = bundle.getString("nick_name");
	    ((CustomFontView) findViewById(R.id.textview_chat_name)).setText(nickName);

	    getChatHistory();
	}
    }

    private void getChatHistory() {
	if (userId != 0) {

	    progress.show();
	    new Thread(new Runnable() {
		@Override
		public void run() {
		    result = AppCAP.getConnection().getOneOnOneChatHistory(userId);
		    if (result.getResponseCode() == AppCAP.HTTP_ERROR) {
			handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
		    } else {
			handler.sendEmptyMessage(HANDLE_GET_HISTORY);
		    }
		}
	    }).start();
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
    }

    public void onClickSend(View v) {

	if (((EditText) findViewById(R.id.edittext_chat)).getText().toString().length() > 0) {
	    final String mess = ((EditText) findViewById(R.id.edittext_chat)).getText().toString();
	    Toast.makeText(ActivityChat.this, "Sending...", Toast.LENGTH_SHORT).show();
	    new Thread(new Runnable() {
		@Override
		public void run() {
		    result = AppCAP.getConnection().sendOneOnOneChatMessage(userId, mess);
		    if (result.getResponseCode() == AppCAP.HTTP_ERROR) {
			handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
		    } else {
			handler.sendEmptyMessage(HANDLE_SEND_MESSAGE);
		    }
		}
	    }).start();

	} else {
	    Toast.makeText(ActivityChat.this, "Message can't be empty!", Toast.LENGTH_SHORT).show();
	}
    }

    public void onClickBack(View v) {
	onBackPressed();
    }

    @Override
    public void onBackPressed() {
	super.onBackPressed();
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

}
