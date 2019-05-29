package com.example.swhit.vehicletracking.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.swhit.vehicletracking.Driver;
import com.example.swhit.vehicletracking.Order;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");

    Driver driverUser = new Driver();
    Order order = new Order();

    //we need to make it so that only drivers can do this; saying id makes it sound like theres validation in place already but thats not the case.

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


    //its only fair to put in validation when u decipher what ref you're using.. instead of just making it 1 way to suit a use case with no validation
    DatabaseReference drivers = myRef.child("users").child("Drivers");
    DatabaseReference currentOrders = myRef.child("orders").child("Current Orders");


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


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
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "GO", Toast.LENGTH_SHORT).show();
        getLocation();
//        updateOrder();
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(getApplicationContext(), "STOP", Toast.LENGTH_SHORT).show();
        //unsure if this is "correct" but Akhil's line in https://stackoverflow.com/questions/47708178/fusedlocationproviderclient-removelocationupdates-always-returns-failure
        //should i include the IF statement? im
//        if(fusedLocationProviderClient != null){

        //THIS NEEDS LOCATION PERMISSIONS;WITHOUT IT IT DOESN'T RUN.
        //FOR NOW, I CAN GRANT THEM FROM THE NORTHFACE TEST ACTIVITY but IT SHOULD BE WITHIN THIS ACTIVITY..
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//}


    }

    private void getLocation() {

        //should i make this single?
        drivers.addValueEventListener(new ValueEventListener() {
            //            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id)) {
                    //this is good, but in other classes, customer is being made redundant , and the use of customer.id is cheaty
                    driverUser = dataSnapshot.child(id).getValue(Driver.class);

                    //how do i then make use of this data???


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);//use slower one in future
        locationRequest.setFastestInterval(3000);//used in cases where something else would be updating the locs? yt


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
//            }
            stopSelf();
            return;
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //override result?
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
//                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //update UI with location data
                    if (location != null) {

                        driverUser.setLatitude(location.getLatitude());
                        driverUser.setLongitude(location.getLongitude());
                        driverUser.setSpeed(Math.round(location.getSpeed()));

                        //overwrites it and drops the name etc (woops)
//                        myRef.child("users").child("Drivers").child(id).setValue(driverUser);


                        drivers.child(id).child("latitude").setValue(driverUser.getLatitude());
                        drivers.child(id).child("longitude").setValue(driverUser.getLongitude());
                        drivers.child(id).child("speed").setValue(driverUser.getSpeed());


                        currentOrders.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.child("driverID").getValue().equals(id)) {
                                        order = ds.getValue(Order.class);
                                        if (order.isDriverEnroute()) {
                                            currentOrders.child(order.getId()).child("latitude").setValue(driverUser.getLatitude());
                                            currentOrders.child(order.getId()).child("longitude").setValue(driverUser.getLatitude());
                                            currentOrders.child(order.getId()).child("speed").setValue(driverUser.getSpeed());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
            }


        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }

}