package com.application.android.partypooper.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private int notificationID;
    private String eventName = "Error retrieving event";
    private String eventDate = "There has been an error while retrieving the event information";

    @Override
    public void onReceive(Context context, Intent intent) {
        eventName = intent.getStringExtra("eventName");
        eventDate = intent.getStringExtra("eventDate");
        notificationID = intent.getIntExtra("ID",0);

        NotificationHelper helper = new NotificationHelper(context);
        NotificationCompat.Builder nb = helper.getChannelNotification(eventName, "You have an event coming up today at " + eventDate);
        helper.getManager().notify(notificationID,nb.build());
    }
}
