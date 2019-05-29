package com.example.swhit.vehicletracking.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Toast;

import com.example.swhit.vehicletracking.AlertReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.swhit.vehicletracking.app.CHANNEL_1_ID;

public class ListenToFirebase extends Service {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");
    DatabaseReference currentOrders = myRef.child("orders").child("Current Orders");

    NotificationManagerCompat notificationManager;

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String timeString;

    //https://www.tutorialspoint.com/convert-string-of-time-to-time-object-in-java
    DateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

    Calendar currentTimeCal;
    Calendar requestedTimeCal = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.ENGLISH);



    Date currentTime;
    Date requestedTime;







    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();

//        dateFormat.format(currentTime);

        System.out.println("----------------------hi-----------------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println("---------------");



        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }


        notificationManager = NotificationManagerCompat.from(this);





//        try {
//            requestedTime = dateFormat.parse(timeString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        while (currentTime.before(requestedTime)){
//            if(requestedTime.getTime() - currentTime.getTime() > 20*60*1000);
//
//
//        }





    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "GO", Toast.LENGTH_SHORT).show();
        checkReqTime();
        System.out.println("------------------------------------------ STARTING");
        System.out.println("yo");


        System.out.println("THIS USERS ID IS :" + id);

//        updateOrder();
        return START_NOT_STICKY;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkReqTime(){



        //prompt driver when to leave
        currentOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("driverID").getValue().equals(id)) {
                        timeString = ds.child("deliveryRequestedForTime").getValue(String.class);
                        System.out.println("----------------------------------------------");
                        System.out.println(timeString);

                        try {
//really rubbish way of doing thigs.. it makes currentTime think we're in 1970, but I don't use it after this anyway..
                            //gets added to calendar, which does display right date and the time i wanted


                            currentTime = sdf.parse(timeString);

                            requestedTimeCal.set(Calendar.HOUR_OF_DAY, currentTime.getHours());
                            requestedTimeCal.set(Calendar.MINUTE, currentTime.getMinutes());

                            startAlarm(requestedTimeCal);

//                            System.out.println("REQUESTED TIME: " + requestedTimeCal.get(Calendar.HOUR_OF_DAY) + ":" + requestedTimeCal.get(Calendar.MINUTE));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

//                        System.out.println(requestedTimeCal.getTime());


                    }
                    else{
                        System.out.println("couldn't find order");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        //unique for each pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        //unique for each pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        System.out.println("alarm cancelled");
    }

}
