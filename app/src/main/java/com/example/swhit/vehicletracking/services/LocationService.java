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
    DatabaseReference orders = myRef.child("orders").child("Current Orders");



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

                        //overwrites it and drops the name etc (woops)
//                        myRef.child("users").child("Drivers").child(id).setValue(driverUser);

                        myRef.child("users").child("Drivers").child(id).child("latitude").setValue(driverUser.getLatitude());
                        myRef.child("users").child("Drivers").child(id).child("longitude").setValue(driverUser.getLongitude());
                    }
                }
            }


        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }

//
//    private void updateOrder(){
//        drivers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
////                    if(!ds.hasChild(id)){
////                        System.out.println("nope");
//////                        continue;
////                    }
//                    //why isn't this working?
//                    if (ds.hasChild(id)) {
//                        driverUser = ds.getValue(Driver.class);
//                        System.out.println("---------------------------");
//                        System.out.println(driverUser.getId());
//                        System.out.println("---------------------------");
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
////        System.out.println("---------------------------");
////        System.out.println(driverUser.getId());
////        System.out.println("---------------------------");
//
//        orders.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataS : dataSnapshot.getChildren()){
//                    String driverId = String.valueOf(dataS.child("driverID").getValue());
//                    if(driverId.equals(id)){
//                        order = dataS.getValue(Order.class);
//                        driverUser.setEnroute(true);
//
//                        break;
//                    }
////                    System.out.println(driverId);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//--------------------------------------------------------------------------------------------------

//    private void updateOrder() {
//
//        //already called in getLocation
////        drivers.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if(dataSnapshot.hasChild(id)){
////                    driverUser = dataSnapshot.getValue(Driver.class);
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
//        System.out.println("----------------------------------------------");
//        orders.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
////                    String driverId = String.valueOf(ds.child("driverID").getValue());
////                    String dID = String.valueOf(driverUser.getId());
//                    System.out.println(driverUser.getId());
////                    String driverID = String.valueOf(ds.child("driverID"));
//                    System.out.println(ds.child("driverID"));
//                    //https://stackoverflow.com/questions/42518637/how-to-compare-the-firebase-retrieved-value-with-a-string
//                    String drID = (String) ds.child("driverID").getValue();
//                    if (driverUser.getId().equals(drID)) {
//                        System.out.println("MATCH!");
//                        order = ds.getValue(Order.class);
//                        String key = ds.getKey();
////                        System.out.println("key: " + key);
////
//                        driverUser.setEnroute(true);
//                        order.setDriverEnroute(true);
//
//                        //should these be their own methods like where everything else is
//                        //my use of updating things in methods is a bit redundant.. what with calling a new class etc..
//                        drivers.child(id).setValue(driverUser);
//                        orders.child(key).setValue(order);
//
//
//                        //DO I NEED THIS?
//                        //does this needs to break here... thought so because i'm comparing a class string to a snapshot
//                        //another reason why classes are bad.
//                        //reason for break is so that drID doesn't get overwritten... does that even matter
//                        break;
//                    }
//                    if (!driverUser.getId().equals(drID)) {
//                        Toast.makeText(getApplicationContext(), "You don't have any Current Orders" + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                    }
//
//
//
////
////                    if(dID.equals(driverID)){
////                        System.out.println("Driver exists in a Current Order");
////
////                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
////        orders.addValueEventListener(new ValueEventListener() {
////
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                for(DataSnapshot ds : dataSnapshot.getChildren()){
////                                    String driverId = String.valueOf(ds.child("driverID").getValue());
////                                    String key = String.valueOf(ds.getKey());
//////                                    Toast.makeText(getApplicationContext(), driverId + order.getDriverID(), Toast.LENGTH_SHORT).show();
////                                    if(driverId.equals(id)){
////                                        System.out.println("IT DO");
////                                        drivers.addValueEventListener(new ValueEventListener() {
////                                            @Override
////                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                                for(DataSnapshot ds : dataSnapshot.getChildren()){
////                                                    String dId = String.valueOf(ds.child("id").getValue());
////
////                                                    if(ds.child(id))
////
//////                                                    if(!ds.hasChild(id)){
//////                                                        System.out.println("nope");
////////                                                        continue;
//////                                                    }
////////                                                    if (ds.hasChild(id)) {
////////                                                        //this never gets called..
////////                                                        System.out.println("yup");
////////                                                        driverUser = ds.getValue(Driver.class);
////////                                                        System.out.println(driverUser.getId());
////////                                                    }
//////                                                    if(ds.child("id").equals(id)){
//////                                                        System.out.println("yup");
//////                                                        driverUser = ds.getValue(Driver.class);
//////                                                        System.out.println(driverUser.getId());
//////                                                    }
//////                                                    if(ds.child)
////
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                            }
////                                        });
////                                        order = ds.getValue(Order.class);
////                                        //this works without "order" being declared.. huh
////                                        Toast.makeText(getApplicationContext(), "SUCCESS; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
////                                        order.setDriverEnroute(true);
////                                        orders.child(key).setValue(order);
////
////                                    }
////                                    else if(!driverId.equals(id)){
////                                        Toast.makeText(getApplicationContext(), "FAILED; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
////                                    }
////                                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
//    }
//-------------------------------------------------------------------------------------------------
//    private void checkDistance{
//
//    }
//
//    private void updateOrder(){
//        orders.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    if(ds.child("driverID").exists()){
//                        order = ds.getValue(Order.class);
    //better examples used order by child https://stackoverflow.com/questions/37579872/android-firebase-query
    //better still ones used childeventlistener etc..
    //similar almost but not really https://stackoverflow.com/questions/43742366/firebase-check-if-child-value-in-node-a-matches-child-value-in-node-b
////https://stackoverflow.com/questions/31847080/how-to-convert-any-object-to-string
//                        String driverId = String.valueOf(ds.child("driverID").getValue());
//                        if (driverId.equals(id)){
//                            Toast.makeText(getApplicationContext(), "SUCCESS; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                        }
//                        if (!driverId.equals(id)){
//                            Toast.makeText(getApplicationContext(), "FAILED; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    Query query = orders.orderByChild("driverID").equalTo(id);
//
//    private void updateOrder(){
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String key = dataSnapshot.getKey();
//                Toast.makeText(getApplicationContext(), key, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void updateOrder() {
//        //https://stackoverflow.com/questions/42423779/android-firebase-orderbychild-query OR SOMETHING BETTER? crappy example
//        //WHAT IF THEY DONT EXIST?
//        orders.orderByChild("driverID").equalTo(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String key = dataSnapshot.getKey();
//                Toast.makeText(getApplicationContext(), key, Toast.LENGTH_SHORT).show();
//                order = dataSnapshot.getValue(Order.class);
//
//                drivers.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.hasChild(id)){
//                            //should this be a different class? you dont even make use of classes so dont make it the same
//                            driverUser = dataSnapshot.child(id).getValue(Driver.class);
//                            //if enroute is false.. does this all work fine? check all cases.
//                            if(!driverUser.isEnroute()){
//                                driverUser.setEnroute(true);
//                                drivers.child(id).setValue(driverUser);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//                orders.child(key).setValue(order);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


//        drivers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(id)){
//                    //should this be different?
//                    driverUser = dataSnapshot.child(id).getValue(Driver.class);
//                    //if driver is not "enroute"
//                    //although we might not need these statements.. considering if they press it they're probably not enroute..
//                    if(!driverUser.isEnroute()){
//                        Toast.makeText(getApplicationContext(), "JOLENE!!!", Toast.LENGTH_SHORT).show();
//                        driverUser.setEnroute(true);
//                        //https://stackoverflow.com/questions/43706540/update-child-value-in-firebase
//                        //if not enroute, now enroute!
//                        drivers.child(id).setValue(driverUser);
//                        orders.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    //    private void writeNewDriver(String name, String email, double latitude, double longitude, boolean isEnroute, String bookable) {
//
//        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
//        Driver driver = new Driver(name, email, latitude, longitude, isEnroute, bookable);
//
//        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
//        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
////        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        driver.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//        myRef.child("users").child("Drivers").child(id).setValue(driver);
//
//
//    }
//    private void UpdateDriver(String name, String email, double latitude, double longitude, boolean enroute, String bookable) {
//
//        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
//        Driver dri = new Driver(name, email, latitude, longitude, enroute, bookable);
//
//        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
//        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        //without this, the id drops off the child in firebase
////        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        dri.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        //is this needed?
//        myRef.child("users").child("Drivers").child(id).setValue(dri);
//
//
//    }

}



