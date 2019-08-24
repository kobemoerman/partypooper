package com.application.android.partypooper.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Activity.EditEventActivity;
import com.application.android.partypooper.Activity.EditInviteActivity;
import com.application.android.partypooper.Activity.EditRecommendationActivity;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;

public class EditEventDialog extends Dialog implements View.OnClickListener {

    public Dialog d;

    private Activity c;

    private Event event;

    private ImageView close;

    private TextView edit, invite, reco, name;

    public EditEventDialog(Activity a, Event event) {
        super(a);
        this.c = a;
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_event);

        initView();

        close.setOnClickListener(this);
        edit.setOnClickListener(this);
        invite.setOnClickListener(this);
        reco.setOnClickListener(this);
    }

    private void initView() {
        close = findViewById(R.id.dialog_close);
        edit = findViewById(R.id.dialog_edit_event);
        invite = findViewById(R.id.dialog_invite_friends);
        reco = findViewById(R.id.dialog_edit_recommendations);
        name = findViewById(R.id.dialog_event_name);

        name.setText(event.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_close:
                dismiss();
                break;
            case R.id.dialog_edit_event:
                openEditEvent();
                break;
            case R.id.dialog_invite_friends:
                openInviteFriends();
                break;
            case R.id.dialog_edit_recommendations:
                openEditRecommendations();
                break;
        }
    }

    private void openEditEvent() {
        dismiss();
        Intent activity = new Intent(c.getApplicationContext(), EditEventActivity.class);
        activity.putExtra("id",event.getTime_stamp()+"?"+event.getHost());
        c.startActivity(activity);
    }

    private void openInviteFriends() {
        dismiss();
        Intent activity = new Intent(c.getApplicationContext(), EditInviteActivity.class);
        activity.putExtra("id",event.getTime_stamp()+"?"+event.getHost());
        c.startActivity(activity);
    }

    private void openEditRecommendations() {
        dismiss();
        Intent activity = new Intent(c.getApplicationContext(), EditRecommendationActivity.class);
        activity.putExtra("id",event.getTime_stamp()+"?"+event.getHost());
        c.startActivity(activity);
    }
}
