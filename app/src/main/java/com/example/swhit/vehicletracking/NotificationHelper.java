package com.example.swhit.vehicletracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
//    public static final String channel1ID= "channel1ID";
//    public static final String channel1Name = "leaveNow";
//
//    public static final String channel2ID= "channel2ID";
//    public static final String channel2Name = "leaveNow";

    public static final String channelID = "channelID";
    public static final String channelName = "channel name";

    //13:00


    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createChannels();
            createChannel();
        }
    }

    //https://www.youtube.com/watch?v=ub4_f6ksxL0
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void createChannels(){
//        NotificationChannel channel1 = new NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT);
//        channel1.enableVibration(true);
//        channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//
//        getManager().createNotificationChannel(channel1);
//
//        NotificationChannel channel2 = new NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT);
//        channel2.enableVibration(true);
//        channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//
//        getManager().createNotificationChannel(channel2);
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }


    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }


//    public NotificationCompat.Builder getChannelNotification(String title, String message){
//        return new NotificationCompat.Builder(getApplicationContext(), channel1ID)
//                .setContentTitle(title)
//                .setContentText(message);
//    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Alarm!")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentText("Please leave the warehouse now to deliver your order");


        //do same for 2:
//    public NotificationCompat.Builder getChannel1Notification(String title, String message){
//        return new NotificationCompat.Builder(getApplicationContext(), channel1ID)
//                .setContentTitle(title)
//                .setContentText(message);
//    }

    }
}
