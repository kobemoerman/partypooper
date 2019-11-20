package com.application.android.partypooper.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("You have created a notification for an event");

        String eventName = "Error retrieving event";
        String eventDate = "There has been an error while retrieving the event information";
        String action = intent.getAction();

        if (action.equals("partypooper.eventName")) {
            eventName = intent.getExtras().getString("eventName");
        }

        if (action.equals("partypooper.eventDate")) {
            eventDate = intent.getExtras().getString("eventDate");
        }

        Intent notificationIntent = new Intent(context, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pending = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(eventName)
            .setContentText("You have an event coming up today at " + eventDate)
            .setTicker("Event coming up")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pending).build();

        NotificationManager mNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification.notify(0, notification);
    }
}
