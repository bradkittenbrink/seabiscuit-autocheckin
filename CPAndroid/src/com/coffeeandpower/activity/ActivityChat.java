package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyChatAdapter;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.views.CustomFontView;

public class ActivityChat extends RootActivity {

    private DataHolder result;

    private Executor exe;

    private ListView listChat;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Executor
        exe = new Executor(ActivityChat.this);
        exe.setExecutorListener(new ExecutorInterface() {
            @Override
            public void onErrorReceived() {
                errorReceived();
            }

            @Override
            public void onActionFinished(int action) {
                actionFinished(action);
            }
        });

        // Views
        listChat = (ListView) findViewById(R.id.listview_chat);

        // Get userId form intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            userId = bundle.getInt("user_id");
            String nickName = bundle.getString("nick_name");
            ((CustomFontView) findViewById(R.id.textview_chat_name))
                    .setText(nickName);

            getChatHistory();
        }
    }

    private void getChatHistory() {
        if (userId != 0) {
            exe.getOneOnOneChatHistory(userId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickSend(View v) {

        if (((EditText) findViewById(R.id.edittext_chat)).getText().toString()
                .length() > 0) {
            final String mess = ((EditText) findViewById(R.id.edittext_chat))
                    .getText().toString();
            Toast.makeText(ActivityChat.this, "Sending...", Toast.LENGTH_SHORT)
                    .show();
            exe.sendOneOnOneChatMessage(userId, mess);
        } else {
            Toast.makeText(ActivityChat.this, "Message can't be empty!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void errorReceived() {

    }

    private void actionFinished(int action) {
        result = exe.getResult();

        switch (action) {

        case Executor.HANDLE_ONE_ON_ONE_CHAT_HISTORY:
            if (result.getObject() instanceof ArrayList<?>) {
                ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) result
                        .getObject();
                MyChatAdapter adapter = new MyChatAdapter(ActivityChat.this,
                        messages);
                listChat.setAdapter(adapter);
                listChat.setSelection(adapter.getCount() - 1);
            }
            break;

        case Executor.HANDLE_SEND_CHAT_MESSAGE:
            Toast.makeText(ActivityChat.this, "Sent...", Toast.LENGTH_SHORT)
                    .show();
            ((EditText) findViewById(R.id.edittext_chat)).setText("");
            getChatHistory();
            break;
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
