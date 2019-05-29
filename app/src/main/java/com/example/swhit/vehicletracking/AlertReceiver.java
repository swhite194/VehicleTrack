package com.example.swhit.vehicletracking;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        //14:10
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification1();
        notificationHelper.getManager().notify(1, nb.build());

    }


}

