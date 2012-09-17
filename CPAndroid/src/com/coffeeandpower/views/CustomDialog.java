package com.coffeeandpower.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.coffeeandpower.app.R;

public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Button btnOk;
    private TextView textTitle;
    private TextView textMessage;

    ClickListener click = new ClickListener() {
        @Override
        public void onClick() {
        }
    };

    public void setOnClickListener(ClickListener click) {
        this.click = click;
    }

    public CustomDialog(Context context, String title, String message) {

        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        btnOk = (Button) findViewById(R.id.button_ok);
        textTitle = (TextView) findViewById(R.id.text_dialog_title);
        textMessage = (TextView) findViewById(R.id.text_dialog_message);

        btnOk.setOnClickListener(this);
        textTitle.setText(title);
        textMessage.setText(message);
    }

    @Override
    public void onClick(View v) {
        click.onClick();
        dismiss();
    }

    public interface ClickListener {
        public void onClick();

    }
}
