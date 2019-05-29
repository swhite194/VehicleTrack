package com.example.swhit.vehicletracking.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.example.swhit.vehicletracking.Customer;
import com.example.swhit.vehicletracking.Driver;
import com.example.swhit.vehicletracking.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
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
    DatabaseReference currentUser = myRef.child("users");

    private NotificationHelper mNotificationHelper;

    NotificationManagerCompat notificationManager;

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String timeString;

    boolean informedOfEnroute;

    //https://www.tutorialspoint.com/convert-string-of-time-to-time-object-in-java
    DateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

    Calendar currentTimeCal = Calendar.getInstance();
    Calendar requestedTimeCal = Calendar.getInstance();

    Calendar timeToLeave = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.ENGLISH);


    Date currentTime;
    Date requestedTime;

    String test;


    Location warehouse = new Location("");
    Location driverLoc = new Location("");
    Location customerLoc = new Location("");

    Driver driver = new Driver();

    Customer cust = new Customer();


    String custID;


    float distanceOfTravelToCustomer;
    double timeToTravelToCustomerInMinutes;
    double differenceInTimes;
    double durationUntilRequestedTimeInMinutes;

    double metresPerMinute = 804.67;

    int currentHourInMinutes;
    int currentMinutes;

    int currentTimeInMinutes;

    int timeToTravelToCustomerInWholeMinutes;

    int hours;
    int minutes;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();

//        dateFormat.format(currentTime);

        checkForOrderPlaced();

        warehouse.setLatitude(54.79);
        warehouse.setLongitude(-7.45);





        mNotificationHelper = new NotificationHelper(this);



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
        checkEnroute();
        System.out.println("------------------------------------------ STARTING");
        System.out.println("yo");


        System.out.println("THIS USERS ID IS :" + id);

//        updateOrder();
        return START_NOT_STICKY;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkReqTime() {

        currentUser.child("Drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id)) {

                    double testLoc = dataSnapshot.child(id).child("latitude").getValue(Double.class);
                    System.out.println("TEST LOC " + testLoc);
                    driver = dataSnapshot.child(id).getValue(Driver.class);
                    String testName = driver.getName();
                    System.out.println(testName);
                    System.out.println(driver.getLatitude());
                    System.out.println(driver.getLongitude());

                    driverLoc.setLatitude(driver.getLatitude());
                    driverLoc.setLongitude(driver.getLongitude());

                    currentOrders.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                if(ds.child("driverID").getValue().equals(id)){

//                                    custID = ds.child("customerID").getValue(String.class);
                                    customerLoc.setLatitude(ds.child("customerLat").getValue(Double.class));
                                    customerLoc.setLongitude(ds.child("customerLong").getValue(Double.class));
                                }
                                else{
                                    System.out.println();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

//        System.out.println("CHECK THE DRIVER");
//        System.out.println(driver.getLatitude());
//        System.out.println(driver.getLongitude());



//        currentUser.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.hasChild(custID)){
//                    System.out.println("WHAT THE DEUCE");
//                    cust = dataSnapshot.child(custID).getValue(Customer.class);
//                    System.out.println(cust.getName());
//                    System.out.println(cust.getLongitude());
//                    customerLoc.setLatitude(cust.getLatitude());
//                    customerLoc.setLongitude(cust.getLongitude());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Drivers").hasChild(id)) {




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


                                        requestedTime = sdf.parse(timeString);

                                        requestedTimeCal.set(Calendar.HOUR_OF_DAY, requestedTime.getHours());
                                        requestedTimeCal.set(Calendar.MINUTE, requestedTime.getMinutes());


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    System.out.println("Requested time " + requestedTimeCal.get(Calendar.HOUR) + " : " + requestedTimeCal.get(Calendar.MINUTE));


                                    currentHourInMinutes = currentTimeCal.get(Calendar.HOUR_OF_DAY) * 60;
                                    currentMinutes = currentTimeCal.get(Calendar.MINUTE);

                                    currentTimeInMinutes = currentHourInMinutes + currentMinutes;

//                                            durationUntilRequestedTimeInMinutes = ((requestedTime.getHours()*60)+(requestedTime.getMinutes())) - currentTimeInMinutes;

                                    System.out.println("CUSTOMER LOCATION: " + customerLoc.getLatitude() + "," + customerLoc.getLongitude());
                                    System.out.println("DRIVER LOCATION: " + driverLoc.getLatitude() + "," + driverLoc.getLongitude());


                                    System.out.println("Distance between customer and warehouse " + driverLoc.distanceTo(warehouse));
                                    System.out.println("Distance between customer and driver: " + driverLoc.distanceTo(customerLoc));



                                    distanceOfTravelToCustomer = (driverLoc.distanceTo(warehouse)) + (warehouse.distanceTo(customerLoc));

                                    System.out.println("Travel distance: " + distanceOfTravelToCustomer);

                                    timeToTravelToCustomerInMinutes = distanceOfTravelToCustomer / metresPerMinute;


                                    System.out.println("Speed per minute " + metresPerMinute);


                                    timeToTravelToCustomerInWholeMinutes = (int) Math.round(timeToTravelToCustomerInMinutes);

                                    System.out.println("Travel Time in minutes " + timeToTravelToCustomerInWholeMinutes);











                                    hours = timeToTravelToCustomerInWholeMinutes / 60; //since both are ints, you get an int
                                    minutes = timeToTravelToCustomerInWholeMinutes % 60;



                                    timeToLeave.add(Calendar.HOUR, hours);
                                    timeToLeave.add(Calendar.MINUTE, minutes);

                                    System.out.println("Requested Time: " + requestedTimeCal.get(Calendar.HOUR) + ":" + timeToLeave.get(Calendar.MINUTE));
                                    System.out.println("Distance to travel: " + distanceOfTravelToCustomer);
                                    System.out.println("Time to Leave: " + timeToLeave.get(Calendar.HOUR) + ":" + timeToLeave.get(Calendar.MINUTE));




//                                            differenceInTimes = durationUntilRequestedTimeInMinutes - timeToTravelToCustomerInMinutes;



//                                        String currentTimeString;
//                                        currentTimeString = currentTime


                                        startAlarm(timeToLeave);



//                                        long diff =


//                            System.out.println("REQUESTED TIME: " + requestedTimeCal.get(Calendar.HOUR_OF_DAY) + ":" + requestedTimeCal.get(Calendar.MINUTE));

//                                        } catch (ParseException e) {
//                                            e.printStackTrace();
//                                        }


                                    if (ds.child("driverEnroute").getValue().equals(false)) {
                                        if (requestedTimeCal.before(Calendar.getInstance())) {
                                            driverRunningLate("You haven't left yet!", "You haven't gone enroute yet, and it's past the request ");
                                        }
                                    }
//                        System.out.println(requestedTimeCal.getTime());


                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkEnroute(){
        currentOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("customerID").getValue().equals(id)) {
                        test = String.valueOf(ds.child("driverEnroute").getValue(Boolean.class));
                        if (!informedOfEnroute){
                            if (test.equals("true")) {

                                checkDriverStatus();
                                informedOfEnroute = true;
                            }
                    }
                    }
                    else{
                        informedOfEnroute = false;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkDriverStatus(){

                    NotificationHelper notificationHelper = new NotificationHelper(this);

                    NotificationCompat.Builder nb = notificationHelper.getChannelNotification5("Your driver is enroute", "Your driver is enroute to deliver to you. Check Tracker Map for Live Updates");
                    notificationHelper.getManager().notify(5, nb.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        //unique for each pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);


    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        //unique for each pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        System.out.println("alarm cancelled");
    }

    private void driverRunningLate(String title, String message) {
        NotificationHelper notificationHelper = new NotificationHelper(this);

        NotificationCompat.Builder nb = notificationHelper.getChannelNotification2(title, message);
        notificationHelper.getManager().notify(2, nb.build());
    }


    private void checkForOrderPlaced(){
            currentOrders.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.child("customerID").getValue().equals(id)) {
                        customerHasOrderPlaced("Order placed for you", "An Order has been placed for you!");
                    }
                    if(dataSnapshot.child("driverID").getValue().equals(id)){
                        driverHasNewOrder("Order placed for you by a customer!", "An Order has been placed for you by a customer!!");
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void customerHasOrderPlaced(String title, String message) {
        NotificationHelper notificationHelper = new NotificationHelper(this);

        NotificationCompat.Builder nb = notificationHelper.getChannelNotification3(title, message);
        notificationHelper.getManager().notify(3, nb.build());
    }

    private void driverHasNewOrder(String title, String message) {
        NotificationHelper notificationHelper = new NotificationHelper(this);

        NotificationCompat.Builder nb = notificationHelper.getChannelNotification4(title, message);
        notificationHelper.getManager().notify(4, nb.build());
    }

}
