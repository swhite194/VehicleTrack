package com.example.swhit.vehicletracking;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swhit.vehicletracking.services.LocationService;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.SQLOutput;

public class HomeActivity extends AppCompatActivity {
//https://developer.android.com/studio/write/layout-editor

    Button btnOpenMap;
    Button btnLogin;
    Button btnEdit;
    Button btnDeleteLocationsFromFirebase;
    Button btnOrderPg;
    TextView txtView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Button btnUserRecordPage;

    Button btnTestPage;
    Button btnTestPage2;
    Button btnRunService;
    Button btnStopService;
    Button btnGoEnroute;

    Button btnParcelDelivered;

    Button btnSearchUserId;

    Button btnYourOrders;

    Button logout;

    LinearLayout titleSection, allUsersSection, currentCustomersOrdersSection, driverStatusSection, adminSection;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");
    DatabaseReference currentUser = myRef.child("users");

    DatabaseReference drivers = myRef.child("users").child("Drivers");
    DatabaseReference customers = myRef.child("users").child("Customers");
    DatabaseReference currentOrders = myRef.child("orders").child("Current Orders");
    DatabaseReference completedOrders = myRef.child("orders").child("Completed Orders");


    Order order = new Order();
    Driver driverUser = new Driver();
    Customer customerUser = new Customer();

    String drID;
    String cuID;
    String key;


    boolean orderHasDriver = false;
    boolean orderHasDriverandCustomer = false;



    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


//    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


    //northgate
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    String phoneNumber;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        btnOpenMap = (Button) findViewById(R.id.btnOpenMap);
//        btnDeleteLocationsFromFirebase = (Button) findViewById(R.id.btnDe);
        btnLogin = (Button) findViewById(R.id.btnLoginPage);
        btnEdit = (Button) findViewById(R.id.btnUserInfo);
        btnOrderPg = (Button) findViewById(R.id.btnOrderPage);

//        btnUserRecordPage = (Button) findViewById(R.id.btnUserRecord);

        btnTestPage = (Button) findViewById(R.id.btnTestPage);
        btnTestPage2 = (Button) findViewById(R.id.btnTestPage2);
        btnRunService = (Button) findViewById(R.id.btnRunService);
        btnStopService = (Button) findViewById(R.id.btnStopService);
        btnGoEnroute = (Button) findViewById(R.id.btnEnroute);

        btnParcelDelivered = (Button) findViewById(R.id.btnParcelDelivered);

        btnSearchUserId = (Button) findViewById(R.id.btnSearchUserId);

        btnYourOrders = (Button) findViewById(R.id.btnYourOrders);

        logout = (Button) findViewById(R.id.btnLogout);


        titleSection = (LinearLayout) findViewById(R.id.TitleSection);
        allUsersSection = (LinearLayout) findViewById(R.id.AllUsersSection);
        currentCustomersOrdersSection = (LinearLayout) findViewById(R.id.CurrentCustomersOrdersSection);
        driverStatusSection = (LinearLayout) findViewById(R.id.DriverStatusSection);
        adminSection = (LinearLayout) findViewById(R.id.AdminSection);


//        btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
//        btnRunService.setVisibility(View.INVISIBLE);
//        btnOrderPg.setVisibility(View.INVISIBLE);

//this isn't dynamically working.. these buttons don't disappear
        //i need to switch accounts (and it shows no change) UNLESS i stop running and rerun.
//        currentUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("Customers").hasChild(id)){
//                    btnDeleteLocationsFromFirebase.setVisibility(View.GONE);
//                    btnRunService.setVisibility(View.GONE);
//                    btnOrderPg.setVisibility(View.VISIBLE);
////                    Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_LONG).show();
//                }
//                if (dataSnapshot.child("Drivers").hasChild(id)){
//                    btnDeleteLocationsFromFirebase.setVisibility(View.GONE);
//                    btnOrderPg.setVisibility(View.GONE);
//                    btnRunService.setVisibility(View.VISIBLE);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        //redundant for now but ye
        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.child("Customers").hasChild(id)) {

                    driverStatusSection.setVisibility(View.GONE);
                    adminSection.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Customer " + id, Toast.LENGTH_LONG).show();
                }
                if (dataSnapshot.child("Drivers").hasChild(id)) {

                    currentCustomersOrdersSection.setVisibility(View.GONE);
                    adminSection.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Driver " + id, Toast.LENGTH_LONG).show();

                }
                if (dataSnapshot.child("Admins").hasChild(id)) {

                    currentCustomersOrdersSection.setVisibility(View.GONE);
                    driverStatusSection.setVisibility(View.GONE);


                    Toast.makeText(getApplicationContext(), "Admin " + id, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            //https://stackoverflow.com/questions/24610527/how-do-i-get-a-button-to-open-another-activity-in-android-studio
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GoogleMapsActivity.class);
                startActivity(intent);
            }
        });

//        btnDeleteLocationsFromFirebase.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                DatabaseReference myRef = mFirebaseDatabase.getReference("Location");
//                myRef.removeValue();
//            }
//        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginOrRegister.class);
                startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserInfo.class);
                startActivity(intent);
            }
        });

//        //can i do the display user stuff just by this click (instead of opening a new page?)
//        btnUserRecordPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, DisplayUserRecord.class);
//                startActivity(intent);
//            }
//        });

        btnOrderPg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlaceOrder.class);
                startActivity(intent);
            }
        });

//        btnTestPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, ReferenceWork.class);
//                startActivity(intent);
//            }
//        });
//
//        btnTestPage2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, ReferenceWork2.class);
//                startActivity(intent);
//            }
//        });

        btnYourOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EditCurrentOrders.class);
                startActivity(intent);
            }
        });

        //https://stackoverflow.com/questions/16876538/android-stop-start-service-created-in-oncreate slightly different but yeah
        //find a better source because they're different.

        btnRunService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//northgate but I changed this to HomeActivity.this (the line below this comment)
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(HomeActivity.this, LocationService.class);
                    startService(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                    }
                }
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LocationService.class);
                stopService(intent);
            }
        });

        btnSearchUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchForUser.class);
                startActivity(intent);
            }
        });



        btnGoEnroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//northgate but I changed this to HomeActivity.this (the line below this comment)



                drivers.addListenerForSingleValueEvent(new ValueEventListener() {
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

//is this check from Northgate?
                //this runs here and really it probably shouldn't, it should run within an if statement down the line
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(HomeActivity.this, LocationService.class);
                    startService(intent);

                    //already called in getLocation
//        drivers.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(id)){
//                    driverUser = dataSnapshot.getValue(Driver.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
                    //https://www.youtube.com/watch?v=Z28s39brZJM ?
                    //different to up top.
                    if (checkPermission(Manifest.permission.SEND_SMS)) {

                        System.out.println("----------------------------------------------");

                        //singlevalue so it won't keep going
                        currentOrders.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    //https://stackoverflow.com/questions/42518637/how-to-compare-the-firebase-retrieved-value-with-a-string
                                    drID = (String) ds.child("driverID").getValue();
                                    cuID = (String) ds.child("customerID").getValue();


                                    if (driverUser.getId().equals(drID)) {
                                        System.out.println("MATCHED DRIVER WITH ORDER!");
                                        //feel like its insecure to put these here?
                                        order = ds.getValue(Order.class);
                                        key = ds.getKey();

                                        orderHasDriver = true;

                                        customers.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataS : dataSnapshot.getChildren()) {
                                                    if (dataS.child("id").getValue().equals(cuID)) {
                                                        System.out.println("MATCHED DRIVER AND CUSTOMER WITH ORDER!");

                                                        driverUser.setEnroute(true);
                                                        order.setDriverEnroute(true);

                                                        drivers.child(id).setValue(driverUser);
                                                        currentOrders.child(key).setValue(order);

                                                        //i'm just re-using cuID because they should be the same according to that check; is that okay?
                                                        //its all a bit convoluted so im worried about that looking sneaky.
//                                                        customers.child(cuID).setValue(customerUser);
                                                        customerUser = dataS.getValue(Customer.class);
                                                        orderHasDriverandCustomer = true;

                                                        informDriverWhenToLeave();
                                                    }
                                                    //end of for loop to find customer in order
                                                }



                                                }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        if (orderHasDriverandCustomer = false) {
                                            Toast.makeText(getApplicationContext(), "There was no customer found for this order" + order.getDriverID(), Toast.LENGTH_SHORT).show();
                                            break;
                                        }


                                        //USED TO BE HERE
//                                        order = ds.getValue(Order.class);
//                                        String key = ds.getKey();
//                        System.out.println("key: " + key);
//


//                                        driverUser.setEnroute(true);
//                                        order.setDriverEnroute(true);

                                        //should these be their own methods like where everything else is
                                        //my use of updating things in methods is a bit redundant.. what with calling a new class etc..
//                                        drivers.child(id).setValue(driverUser);
//                                        orders.child(key).setValue(order);


                                        //DO I NEED THIS?
                                        //does this needs to break here... thought so because i'm comparing a class string to a snapshot
                                        //another reason why classes are bad.
                                        //reason for break is so that drID doesn't get overwritten... does that even matter


                                    }

                                //end of enclosing for loop (goes through the Orders)
                                }

                                if (orderHasDriver = false) {
                                    Toast.makeText(getApplicationContext(), "You don't have any Current Orders" + order.getDriverID(), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        orderHasDriver = false;
                        orderHasDriverandCustomer = false;
                        //resetting

//        orders.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                                    String driverId = String.valueOf(ds.child("driverID").getValue());
//                                    String key = String.valueOf(ds.getKey());
////                                    Toast.makeText(getApplicationContext(), driverId + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                                    if(driverId.equals(id)){
//                                        System.out.println("IT DO");
//                                        drivers.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                                                    String dId = String.valueOf(ds.child("id").getValue());
//
//                                                    if(ds.child(id))
//
////                                                    if(!ds.hasChild(id)){
////                                                        System.out.println("nope");
//////                                                        continue;
////                                                    }
//////                                                    if (ds.hasChild(id)) {
//////                                                        //this never gets called..
//////                                                        System.out.println("yup");
//////                                                        driverUser = ds.getValue(Driver.class);
//////                                                        System.out.println(driverUser.getId());
//////                                                    }
////                                                    if(ds.child("id").equals(id)){
////                                                        System.out.println("yup");
////                                                        driverUser = ds.getValue(Driver.class);
////                                                        System.out.println(driverUser.getId());
////                                                    }
////                                                    if(ds.child)
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                        order = ds.getValue(Order.class);
//                                        //this works without "order" being declared.. huh
//                                        Toast.makeText(getApplicationContext(), "SUCCESS; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                                        order.setDriverEnroute(true);
//                                        orders.child(key).setValue(order);
//
//                                    }
//                                    else if(!driverId.equals(id)){
//                                        Toast.makeText(getApplicationContext(), "FAILED; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

                        //https://www.youtube.com/watch?v=Z28s39brZJM mixed with northgate
                        //end of if SEND_SMS permission true
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
                        }
                    }
                    //northgate
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                    }
                }
            }
        });




        btnParcelDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DO I NEED THIS AGAIN?
                drivers.addListenerForSingleValueEvent(new ValueEventListener() {
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


                //already called in getLocation
//        drivers.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(id)){
//                    driverUser = dataSnapshot.getValue(Driver.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
                //https://www.youtube.com/watch?v=Z28s39brZJM ?
                //different to up top.

                System.out.println("----------------------------------------------");
                currentOrders.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            System.out.println(ds.child("driverID"));

                            drID = (String) ds.child("driverID").getValue();
                            cuID = (String) ds.child("customerID").getValue();

                            if (driverUser.getId().equals(drID)) {
                                System.out.println("MATCHED DRIVER WITH ORDER!");
                                //feel like its insecure to put these here?
                                order = ds.getValue(Order.class);
                                key = ds.getKey();
                                customers.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataS : dataSnapshot.getChildren()) {
                                            if (dataS.child("id").getValue().equals(cuID)) {

                                                customerUser = dataS.getValue(Customer.class);
                                                System.out.println("Matched customer too!");

                                                orderHasDriverandCustomer = true;
//                                                driverUser.setEnroute(true);
//                                                order.setDriverEnroute(true);

                                                //if the driver is enroute, make both not enroute!

//                                                if(ds.child("driverEnroute").getValue().equals("true")){
                                                if (order.isDriverEnroute()) {
//                                                    myRef.child("users").child("Customers").child(customer.id).setValue(cus);
                                                    drivers.child(id).child("enroute").setValue(false);
                                                    currentOrders.child(key).child("driverEnroute").setValue(false);

                                                    moveRecord(currentOrders.child(key), completedOrders.child(key));

                                                }
                                                //prompt them to go enroute!
                                                else {
//                                                    System.out.println("Order ID!!!!!!!!!!!!!!!!!!!!:");
//                                                    System.out.println(order.getId());
                                                    //the reason that order.getId returns null is because "id" is created due firebase PUSHING the child for order
                                                    //as opposed to me specifying the child be the id (like for Admin, Driver, Customer)  which aren't pushes on register.
//                                                    Toast.makeText(getApplicationContext(), "You do have an order " + order.getId() + " for customer " + customerUser.getId() + "for the time - " + order.getDeliveryRequestedForTime() + " but aren't listed as being enroute, please go enroute!", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getApplicationContext(), "You do have an order " + key + " for customer " + customerUser.getId() + "for the time - " + order.getDeliveryRequestedForTime() + " but please go Enroute first! Click Travel to User", Toast.LENGTH_SHORT).show();
                                                }
//                                                drivers.child(id).setValue(driverUser);
//                                                orders.child(key).setValue(order);

                                                //i'm just re-using cuID because they should be the same according to that check; is that okay?
                                                //its all a bit convoluted so im worried about that looking sneaky.
//                                                        customers.child(cuID).setValue(customerUser);

//                                                        phoneNumber = customerUser.getPhoneNumber;

//                                                        orderHasDriverandCustomer = true;
//                                                        SmsManager smsManager = SmsManager.getDefault();
//                                                        smsManager.sendTextMessage();
                                                break;
                                            }
                                            else if (!dataS.child("id").getValue().equals(cuID)) {
                                                Toast.makeText(getApplicationContext(), "The customer in this order doesn't exist", Toast.LENGTH_SHORT).show();
                                            }

//                                            if (orderHasDriverandCustomer = false) {
//
//                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                //USED TO BE HERE
//                                        order = ds.getValue(Order.class);
//                                        String key = ds.getKey();
//                        System.out.println("key: " + key);
//


//                                        driverUser.setEnroute(true);
//                                        order.setDriverEnroute(true);

                                //should these be their own methods like where everything else is
                                //my use of updating things in methods is a bit redundant.. what with calling a new class etc..
//                                        drivers.child(id).setValue(driverUser);
//                                        orders.child(key).setValue(order);


                                //DO I NEED THIS?
                                //does this needs to break here... thought so because i'm comparing a class string to a snapshot
                                //another reason why classes are bad.
                                //reason for break is so that drID doesn't get overwritten... does that even matter
                                break;
                            }
                            if (!driverUser.getId().equals(drID)) {
                                Toast.makeText(getApplicationContext(), "You don't have any Current Orders" + order.getDriverID(), Toast.LENGTH_SHORT).show();
                            }


//
//                    if(dID.equals(driverID)){
//                        System.out.println("Driver exists in a Current Order");
//
//                    }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//        orders.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                                    String driverId = String.valueOf(ds.child("driverID").getValue());
//                                    String key = String.valueOf(ds.getKey());
////                                    Toast.makeText(getApplicationContext(), driverId + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                                    if(driverId.equals(id)){
//                                        System.out.println("IT DO");
//                                        drivers.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                                                    String dId = String.valueOf(ds.child("id").getValue());
//
//                                                    if(ds.child(id))
//
////                                                    if(!ds.hasChild(id)){
////                                                        System.out.println("nope");
//////                                                        continue;
////                                                    }
//////                                                    if (ds.hasChild(id)) {
//////                                                        //this never gets called..
//////                                                        System.out.println("yup");
//////                                                        driverUser = ds.getValue(Driver.class);
//////                                                        System.out.println(driverUser.getId());
//////                                                    }
////                                                    if(ds.child("id").equals(id)){
////                                                        System.out.println("yup");
////                                                        driverUser = ds.getValue(Driver.class);
////                                                        System.out.println(driverUser.getId());
////                                                    }
////                                                    if(ds.child)
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                        order = ds.getValue(Order.class);
//                                        //this works without "order" being declared.. huh
//                                        Toast.makeText(getApplicationContext(), "SUCCESS; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                                        order.setDriverEnroute(true);
//                                        orders.child(key).setValue(order);
//
//                                    }
//                                    else if(!driverId.equals(id)){
//                                        Toast.makeText(getApplicationContext(), "FAILED; " + order.getDriverID(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

                //https://www.youtube.com/watch?v=Z28s39brZJM mixed with northgate
                //end of if SEND_SMS permission true

            }
        });


//        //redundant for now but ye
//        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                if (dataSnapshot.child("Customers").hasChild(id)) {
//
////                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
//                    btnRunService.setVisibility(View.INVISIBLE);
//                    btnStopService.setVisibility(View.INVISIBLE);
//                    btnOrderPg.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Customer " + id, Toast.LENGTH_LONG).show();
//                }
//                if (dataSnapshot.child("Drivers").hasChild(id)) {
////                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
//                    btnOrderPg.setVisibility(View.INVISIBLE);
//                    btnRunService.setVisibility(View.VISIBLE);
//                    btnStopService.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Driver " + id, Toast.LENGTH_LONG).show();
//
//                }
//                if (dataSnapshot.child("Admins").hasChild(id)) {
//
////                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
//                    btnRunService.setVisibility(View.INVISIBLE);
//                    btnStopService.setVisibility(View.INVISIBLE);
//                    btnOrderPg.setVisibility(View.INVISIBLE);
//                    Toast.makeText(getApplicationContext(), "Admin " + id, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void informDriverWhenToLeave() {
        //https://www.youtube.com/watch?v=Z28s39brZJM
        System.out.println("Running inform driver method");
        //why have i called it customerUser
        phoneNumber = driverUser.getPhoneNumber();
        message = "Yeet";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void parcelDelivered() {

    }

//    public void moveFirebaseRecord(DatabaseReference fromPath, final DatabaseReference toPath){
//
//    }

//    public void moveFirebaseRecord(DatabaseReference fromPath, final DatabaseReference toPath){
//        fromPath = currentOrders.child(key);
//        toPath = completedOrders.child(key);
//
//        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isComplete()){
//                            System.out.println("Copied current order to Finished Order");
//                        }else{
//                            System.out.println("Copy failed");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    //completely copied
    //https://stackoverflow.com/questions/44809824/moving-or-copying-data-from-one-node-to-another-in-firebase-database/44809870#44809870
    private void moveRecord(final DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            System.out.println("Success");
                            fromPath.removeValue();
                        } else {
                            System.out.println("Failed");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }


    //https://www.youtube.com/watch?v=Z28s39brZJM
    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    //northgate
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//permission was granted.. do nothing and carry on
                } else {
                    Toast.makeText(getApplicationContext(), "Tracking your route requires location permissions to be granted", Toast.LENGTH_SHORT).show();
//                    finish();
                }

                break;
        }

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//////THIS DOESN'T WORK IF YOU DELETE ALL OF THE FIREBASE STUFF FFS / or if they're "logged" out..
//        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
//
////            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("Customers").hasChild(id)) {
//
//                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
//                    btnRunService.setVisibility(View.INVISIBLE);
//                    btnOrderPg.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Customer " + id, Toast.LENGTH_LONG).show();
//                }
//                if (dataSnapshot.child("Drivers").hasChild(id)) {
//                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
//                    btnOrderPg.setVisibility(View.INVISIBLE);
//                    btnRunService.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Driver " + id, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
