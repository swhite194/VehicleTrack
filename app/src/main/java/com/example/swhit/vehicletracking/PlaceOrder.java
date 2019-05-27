package com.example.swhit.vehicletracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlaceOrder extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");

    DatabaseReference orderRef = myRef.child("orders").child("Current Orders");
    DatabaseReference enRouteOrderRef = myRef.child("orders").child("Enroute Orders");
    DatabaseReference completedOrders = myRef.child("orders").child("Completed Orders");
    DatabaseReference drivers = myRef.child("users").child("Drivers");
    DatabaseReference customers = myRef.child("users").child("Customers");



    Button place_order;
    ImageButton huawei, pixel, iphone;
    FirebaseAuth firebaseAuth;


    String item_id;
    int item_quantity;
    Customer aCustomer = new Customer();
    Driver aDriver = new Driver();
    Order order = new Order();
    //this should maybe be called something else, its a bit confusing..
    //i need to add validation in so that it cant be a driver that does this; "custID" almost makes it sound like thats the case already.
    String custID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //used to check availability status of driver
    boolean available;

//    LatLng custLatLng;
//    LatLng driverLatLng;


    //https://stackoverflow.com/questions/35508017/how-to-store-hour-and-minute-in-a-time-object
    TimePicker timePicker;

    //change name
    Calendar time = Calendar.getInstance();
    Calendar currentTime = Calendar.getInstance();

    Date dTime;

    double customerPickedTimeInMinutes;
    double currentTimeInMinutes;
    double durationUntilRequestedTimeInMinutes;

    String key;
    String key1;

    //the warehouse location
//    LatLng warehouse = new LatLng(54.58,-5.93);
    Location warehouse = new Location("");


    Location driverLoc = new Location("");
    Location customerLoc = new Location("");


    float distanceOfTravelToCustomer;
    double posTime;
    double negTime;

    //equivalent of 30 miles/hr which we assume
    double metresPerMinute = 804.67;

    double timeToTravelToCustomerInMinutes;

    double differenceInTimes;

    boolean positiveTime = false;


    //selected values customer is requesting..  change name.
    int hourInMinutes;
    int minutes;

    int currentHourInMinutes;
    int currentMinutes;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    String driverPhoneNumber;
    String messageToDriver;

    String requestedDeliveryDate;
    String requestedDeliveryTime;


    //purpose of this is so that if you select a time, but someone else beats you to it and gets that driver (from another phone),
    //that driver can be double checked when you click on the placeorder (via searching firebase for this driverId), if they're suddenly booked by someone else, it will trigger a restart
    //of this place order page, reinitialising the variables and bool conditions i use throughout the page at certain points which i was using to allow things
    String driverId;


    //https://stackoverflow.com/questions/8745297/want-current-date-and-time-in-dd-mm-yyyy-hhmmss-ss-format
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    boolean driverStillBookable = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        //calling it custID here instead of customer.id (the problem lies more in UserInfo's use of saying Customer customer TWICE.. THAT needs to be fixed.. this wasn't such a big deal - check fb messenger)
        //this is what id prefer for UserInfo.

        warehouse.setLatitude(54.79);
        warehouse.setLongitude(-7.45);

//        utime = findViewById(R.id.txtDeliveryTime);

        //https://www.c-sharpcorner.com/article/create-timepicker-android-app-using-android-studio/
        timePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(true);






        place_order = findViewById(R.id.btnUpdate);

        huawei = findViewById(R.id.imgbtnHuawei);
        pixel = findViewById(R.id.imgbtnPixel);
        iphone = findViewById(R.id.imgbtnIphone);





            place_order.setEnabled(false);



        //https://www.c-sharpcorner.com/article/create-timepicker-android-app-using-android-studio/
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                //so that if they did have a positive time but then changed it to an earlier time in that same screen and there are no longer any positives, that variable is made false.
                positiveTime = false;

                //THE TIME THE CUSTOMER SELECTED
                //https://stackoverflow.com/questions/35508017/how-to-store-hour-and-minute-in-a-time-object
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);


                //TIME CANNOT BE IN PAST, I THINK THIS *MIGHT* CONSIDER DAY TOO CONSIDERING IT USES CALENDAR
                if(time.getTimeInMillis() < currentTime.getTimeInMillis()){
                    //THIS TOAST GETS OVERLOOKED BY THE CHECK FOR 0.0 TIME
                    Toast.makeText(getApplicationContext(), "Time cannot be before current date!", Toast.LENGTH_LONG).show();
                    System.out.println("Time cannot be before current date!");
                    //confusingly, currenthour just means whatever the timepicker says.
                    //think its like this by default (initial load)
                    timePicker.setCurrentHour(currentTime.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(currentTime.get(Calendar.MINUTE));

                }

                //https://stackoverflow.com/questions/9243578/java-util-date-and-getyear

                hourInMinutes = time.get(Calendar.HOUR_OF_DAY) * 60;
                minutes = time.get(Calendar.MINUTE);


                //what the user selects (this changes for click on that clock made), hours first and then minutes.
                customerPickedTimeInMinutes = hourInMinutes + minutes;




                System.out.println("Time requested in minutes: " + customerPickedTimeInMinutes);
                System.out.println("-----------------------------------------");

                //weird place to go maybe the rest needs fixed up; but either way this is used in placeorder method and in the text msg method
                requestedDeliveryTime = String.format(Locale.UK, "%d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));

                //https://stackoverflow.com/questions/8745297/want-current-date-and-time-in-dd-mm-yyyy-hhmmss-ss-format
                //it's called getTime, and will go to our format of dd/mm/yyyy
                //probably a better version than above.
                requestedDeliveryDate = dateFormat.format(time.getTime());

                //WHAT THE CURRENT TIME IS
                currentHourInMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60;
                currentMinutes = currentTime.get(Calendar.MINUTE);

                currentTimeInMinutes = currentHourInMinutes + currentMinutes;


                System.out.println("CURRENT TIME: " + currentTime.getTime());


                //DURATION BETWEEN CURRENT TIME AND REQUESTED TIME IN MINUTES
                durationUntilRequestedTimeInMinutes = customerPickedTimeInMinutes - currentTimeInMinutes;




//will be used to indicate the difference between a positive and negative result when comparing
                //the customer's selected delivery time vs the time that it will take for the driver to get to them
                //I calculate using Customer Requested Time (in minutes) minus the Time Taken to Travel to warehouse and then to customer (seen below)


                posTime = 1000000000;
                negTime = -1000000000;

                //what is this?
//                myRef.child("time").setValue(time.get(Calendar.YEAR));

                drivers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                            //
                            if (snapshot.child("bookable").exists()) {
                                if (snapshot.child("bookable").getValue().equals("available")) {

                                    available = true;
                                    place_order.setEnabled(true);

                                    //https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points
                                    driverLoc.setLatitude(snapshot.child("latitude").getValue(Double.class));
                                    driverLoc.setLongitude(snapshot.child("longitude").getValue(Double.class));

                                    //estimated travel from driver's location to warehouse to customer's location
                                    distanceOfTravelToCustomer = (driverLoc.distanceTo(warehouse)) + (warehouse.distanceTo(customerLoc));
                                    timeToTravelToCustomerInMinutes = distanceOfTravelToCustomer / metresPerMinute;
                                    differenceInTimes = durationUntilRequestedTimeInMinutes - timeToTravelToCustomerInMinutes;


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    //A REALLY DECENT INFO PROFILE//
                                    //think its right?

                                    //customer time - driver time
                                    //the idea here is that we select the driver with the difference in times closest to 0
                                    //what this means is that if the customer were to pick a generous time, we'd allocate a driver who is perhaps currently further away, so as to reserve
                                    //a closer driver for requests that are maybe more immediate.


//                                    System.out.println("ID of Customer " + aCustomer.getId());
//                                    System.out.println("ID of driver: " + snapshot.child("id").getValue());
//
//                                    System.out.println("Customer requests a delivery this many minutes from now: " + customerPickedTimeInMinutes);
//                                    System.out.println("Time to travel to Warehouse" + driverLoc.distanceTo(warehouse));
//                                    System.out.println("Time to travel to Customer from Warehouse: " + warehouse.distanceTo(customerLoc));
//                                    System.out.println("Therefore total travel time: " + timeToTravelToCustomerInMinutes);
//                                    System.out.println("Difference between Requested time " +customerPickedTimeInMinutes + " i.e. " + durationUntilRequestedTimeInMinutes + " minutes from now, and Driver driving duration " + timeToTravelToCustomerInMinutes);
//                                    System.out.println("is: " + differenceInTimes);
//                                    System.out.println("--------------------------------------------------------------------");


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//in both cases.. it's not smart enough to know if they're travelling closer. but to be honest it's not a huge deal is it

// looks for the FARTHEST car that can do it in the right time.. seems strange, but its in the hopes that we will have someone who can attend a nearby person more readily (without going in negatives we hope!)

                                    if (((differenceInTimes) <= posTime) && (differenceInTimes >= 0)) {
                                        posTime = differenceInTimes;
                                        positiveTime = true;
                                        key = snapshot.getKey();
                                        System.out.println("KEY: " + key);
                                        System.out.println("---------------------------------------------");
                                        //bad use, driver isn't really assigned..
                                        System.out.println("Driver ID " + snapshot.child("id").getValue() + "Found a positive time");
                                        System.out.println("Customer Requests Delivery For this time: " + customerPickedTimeInMinutes);
                                        System.out.println("Duration until that time: " + durationUntilRequestedTimeInMinutes);
                                        System.out.println("Driver's travel distance to get to customer: " + timeToTravelToCustomerInMinutes);
                                        System.out.println("Difference" + differenceInTimes);
                                        System.out.println("Positive Time " + posTime);

                                    }

                                    //if the child's calculated difference between times is less than 0, meaning that that driver can't be there in time
                                    //then it considers a tally of the negative times, and ultimately selects the best negative time - i.e. the one closest 0 (whilst trying to find the largest positive time too)

                                    //THIS ISNT TRUE. ^


                                    else if (((differenceInTimes >= negTime) && (differenceInTimes) < 0)) {
                                        negTime = differenceInTimes;
//                                        System.out.println("Difference" + differenceInTimes);
//                                        System.out.println("Negative Time" + negTime);
                                        key1 = snapshot.getKey();
                                        System.out.println("---------------------------------------------");
                                        //^
                                        System.out.println("Driver ID " + snapshot.child("id").getValue() + "Found a negative time");
                                        System.out.println("Customer Requests Delivery For this time: " + customerPickedTimeInMinutes);
                                        System.out.println("Duration until that time: " + durationUntilRequestedTimeInMinutes);
                                        System.out.println("Driver's travel distance to get to customer: " + timeToTravelToCustomerInMinutes);
                                        System.out.println("Difference" + differenceInTimes);
                                        System.out.println("Negative Time " + negTime);

                                    }


//not sure what this else is really for.

//                                    else {
//                                        Toast.makeText(getApplicationContext(), "Can't be done", Toast.LENGTH_LONG).show();
//                                        System.out.println("---------------------------------------------");
//                                        System.out.println("Customer Requests Delivery For this time: " + customerPickedTimeInMinutes);
//                                        System.out.println("Duration until that time: " + durationUntilRequestedTimeInMinutes);
//                                        System.out.println("Driver's travel distance to get to customer: " + timeToTravelToCustomerInMinutes);
//                                        System.out.println("Driver ID " + snapshot.child("id").getValue() + " ----Can't be done");
//                                        System.out.println("Difference" + differenceInTimes);
//
//                                    }

                                }
                            }

                        }

                        if(positiveTime){
//                                aDriver = snapshot.getValue(Driver.class);
//                                aDriver = drivers.child(key)

                            //Yes, this class does change multiple times during iteration, i.e. if a positive time is found
                            //then the next is smaller, then the next etc etc.
                            //but it works. so whatever.
                            //key stuff from other activities, stackoverflow was source(probably mainly idea from Google Maps activity)
                            aDriver = dataSnapshot.child(key).getValue(Driver.class);

                            Toast.makeText(getApplicationContext(), "We're assinging you " + aDriver.getEmail(), Toast.LENGTH_LONG).show();

                            //as described before onCreate
                            driverId = aDriver.getId();

                            System.out.println("1 " + dataSnapshot.child(key).child("email").getValue());
                            System.out.println("2 " + dataSnapshot.child(key).child("email").getValue());
                            System.out.println("1 " + dataSnapshot.child(key).child("email").getValue());

                            System.out.println("THE KEY IS " + key);
                            System.out.println("Your driver" + aDriver.getId());


//                                        available = true;
                            Toast.makeText(getApplicationContext(), "This time is okay!", Toast.LENGTH_LONG).show();
                        }

                        //consider the negative value.. currently no drivers are able to take the customer in that elected time, so we suggest a time that by our predictions will allow for that negative driver to
                        //be with the customer in time

                        //not else if?
                        else{
//                                aDriver = snapshot.getValue(Driver.class);

//                                        double differenceBetweenNegativeand0 = Math.max(0, negTime);

                            place_order.setEnabled(false);
                            double durationUntilOK = negTime * -1;

//                                     value in minutes for driver (it's not exactly a "time")... whereas customertime is based off an actual 24 hour clock
//                                                                        consider that if the difference was like -1000 minutes, but the customer time was around 10pm, you can't do that because we're dealing with a 24 hour
//                                                                        TIME only event, the days aren't factored in.. might want to think about changing terminology of "time" for the driverTime stuff

//                                        Toast.makeText(getApplicationContext(), "Please book " + differenceBetweenNegativeand0 + " minutes from this time!", Toast.LENGTH_LONG).show();

                            if(!available){
                                Toast.makeText(getApplicationContext(), "There are no drivers available on the system right now, please try again later", Toast.LENGTH_LONG).show();
                            }else {

                                Toast.makeText(getApplicationContext(), "Please book " + durationUntilOK + " minutes from this time!", Toast.LENGTH_LONG).show();

                            }
                        }
//                                    //is that even callable
//                                    //what if something happens to either my customer/driver's child? like they get deleted..
//                                    //that's what this was FOR
//                                    //but i think the "else" bit is in reference to the boolean and not any other condition, so it's not considering anything else and as a result this wont be called..
//                                    else{
//                                        Toast.makeText(getApplicationContext(), "No child key", Toast.LENGTH_LONG).show();
//                                    }






//                            aDriver = snapshot.getValue(Driver.class);

//                            System.out.println(aDriver.getId() + " " + driverLoc + " distance to warehouse: " + distanceOfTravel);
//                            available = true;
                        //not bad practice?
//                            break;

//should this be a break?

//
//                                    //without this, any unavailable drivers make it available = false for everyone.. and nobody can book anything.
//                                    //break out so you don't keep iterating, as the next one says if it sees unavailable it will make available false, which affects Place Order's check for the overall availability boolean
//                                    break;


                        //this gets called after i press Place Order..
//                                else if(snapshot.child("bookable").getValue().equals("unavailable")){
//                                    if(available = false)
//                                        System.out.println("WHAT WHAT WHAT");
////                                    available = false;
//                                }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
            //end of timechangedlistener
        });





        //for old times
//        oldTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
//        oldTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));




        //changes based on phone clicked


//this is a bad name for it here. considering its used for current user BUT also to see into driver.
//        final DatabaseReference currentUser = myRef.child("users");
//        final DatabaseReference driverRef = myRef.child("users").child("Drivers").child(driver.id);
        //https://stackoverflow.com/questions/43265668/checking-if-field-data-changed-rather-than-any-field-in-child-data/43265932 -WHAT DOES THIS APPLY TO? just this paragraph or the next?


//should this be value rather than single? yeah? can it even really change and do i actually have anything to prevent that
        customers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(custID)) {

                    aCustomer = dataSnapshot.child(custID).getValue(Customer.class);


                    //is it bad that this is different than how driver's done?
                    customerLoc.setLatitude(aCustomer.getLatitude());
                    customerLoc.setLongitude(aCustomer.getLongitude());


                    //how do i then make use of this data???


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //https://stackoverflow.com/questions/40366717/firebase-for-android-how-can-i-loop-through-a-child-for-each-child-x-do-y based off
//        drivers.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////ITERATE THROUGH ALL DRIVERS TO SEE WHO IS AVAILABLE
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                if(snapshot.child("bookable").equals(true)){
//                    aDriver = snapshot.getValue(Driver.class);
//                }
//            }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        //based on question https://stackoverflow.com/questions/52128852/getting-data-from-firebase-using-orderbychild-query
//but i made it SingleValue like this https://stackoverflow.com/questions/40366717/firebase-for-android-how-can-i-loop-through-a-child-for-each-child-x-do-y
        //check both!


//        drivers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//
//                    //
//                    if(snapshot.child("bookable").exists()){
//                        if(snapshot.child("bookable").getValue().equals("available")){
//
//
//                            //https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points
//                            //should i make them classes first like i did for custLoc? cheaty?
//                            driverLoc.setLatitude(snapshot.child("latitude").getValue(Double.class));
//                            driverLoc.setLongitude(snapshot.child("longitude").getValue(Double.class));
//
//
//                            //estimated travel from driver's location to warehouse to customer's location
//
//                            distanceOfTravelToCustomer = (driverLoc.distanceTo(warehouse))+(warehouse.distanceTo(customerLoc));
//
////                            if(distanceOfTravel >= 0){
////
////                            }
//
//                            timeToTravelToCustomerInMinutes = distanceOfTravelToCustomer/metresPerMinute;
//
//                            differenceInTimes = customerPickedTimeInMinutes - timeToTravelToCustomerInMinutes;
//
//                            //customer time - driver time
//                            if((differenceInTimes)>= posTime){
//                                posTime = differenceInTimes;
//                                positiveTime = true;
//                                key = snapshot.getKey();
//                            }
//
//                            //if the child's calculated difference between times is less than 0, meaning that that driver can't be there in time
//                            //then it considers a tally of the negative times, and ultimately selects the best negative time - i.e. the one closest 0 (whilst trying to find the largest positive time too)
//                            else if(((differenceInTimes)<0)&&(differenceInTimes>negTime)){
//                                negTime = differenceInTimes;
//                                key1 = snapshot.getKey();
//
//                            }
//
//                            if(positiveTime = true){
////                                aDriver = snapshot.getValue(Driver.class);
////                                aDriver = drivers.child(key)
//
//                                //thiiiiiink that should work?
//                                drivers.child(key).setValue(aDriver);
//
//                                available = true;
//                                Toast.makeText(getApplicationContext(), "This time is okay!", Toast.LENGTH_LONG).show();
//                            }
//
//                            //consider the negative value.. currently no drivers are able to take the customer in that elected time, so we suggest a time that by our predictions will allow for that negative driver to
//                            //be with the customer in time
//
//                            else if(positiveTime = false){
////                                aDriver = snapshot.getValue(Driver.class);
//
//                                double differenceBetweenNegativeand0 = Math.max(0, negTime);
//
//                                Toast.makeText(getApplicationContext(), "Please book " + differenceBetweenNegativeand0 + " minutes from this time!", Toast.LENGTH_LONG).show();
//
//
//                            }
//
//
//
//
////                            aDriver = snapshot.getValue(Driver.class);
//
////                            System.out.println(aDriver.getId() + " " + driverLoc + " distance to warehouse: " + distanceOfTravel);
////                            available = true;
//                            //not bad practice?
////                            break;
//                        }
//                        if(snapshot.child("bookable").getValue().equals("unavailable")){
//                            available = false;
//                        }
//                    }
//                    if(!(snapshot.child("bookable").exists())){
//                        //https://stackoverflow.com/questions/11160952/goto-next-iteration-in-for-loop-in-java
//                        continue;
//                    }
////
////
//// check = snapshot.child("bookable").getValue(String.class);
//                    //works backwards but is fine... or is it the submit/writeorder stuff that does?
////                    if (check.equals("available")){
////                        aDriver = snapshot.getValue(Driver.class);
////                        System.out.println(check);
////                    }
//
//
//                    //NEED TO SAY ELSE HERE.
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        huawei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //does this go here


                item_id = "huawei_P30_Pro";

            }
        });

        pixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //does this go here


                item_id = "Pixel";


            }
        });

        iphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //does this go here


                item_id = "iPhone";

            }
        });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.SEND_SMS)) {

                    if (available) {

                        //double check that they've not since been booked by someone else
                        drivers.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(driverId).child("bookable").getValue().equals("unavailable")){
                                    driverStillBookable = false;
                                    Toast.makeText(getApplicationContext(), "Sorry looks like someone else beat you to it and got your driver before you, restarting page!", Toast.LENGTH_LONG).show();
                                    //https://stackoverflow.com/questions/2486934/programmatically-relaunch-recreate-an-activity
                                    //recreates the activity (good way of getting rid of variables that have been changed; which is a problem with pressing back for other screens..
                                    recreate();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(driverStillBookable) {
                            //just dealing with 24 hr single days atm.. not Days.
                            writeNewOrder(aCustomer.getId(), aCustomer.getName(), aCustomer.getEmail(), aCustomer.getAddress(), aCustomer.getCity(), aCustomer.getPostcode(), aDriver.getId(), aDriver.getName(), aDriver.isEnroute(), item_id, item_quantity, requestedDeliveryDate, requestedDeliveryTime, null);

                            //pushing lat/long/speed instead of putting these in an order class
                            orderRef.child(order.getId()).child("latitude").setValue(aDriver.getLatitude());
                            orderRef.child(order.getId()).child("longitude").setValue(aDriver.getLongitude());
                            orderRef.child(order.getId()).child("speed").setValue(aDriver.getSpeed());
//                aDriver.setBookable(false);

                            notifyDriverOnOrderPlaced();

                            aDriver.setBookable("unavailable");
                            //changing availability to unavailable
                            myRef.child("users").child("Drivers").child(aDriver.getId()).setValue(aDriver);


                            Intent intent = new Intent(PlaceOrder.this, HomeActivity.class);
                            startActivity(intent);

//                    custLatLng = (aCustomer.getLatitude(), aCustomer.getLongitude());
                        }
                    }
//                    if (!available) {
//
//                        Toast.makeText(getApplicationContext(), "Sorry no drivers are available on the system!", Toast.LENGTH_LONG).show();
//                    }
                    //should i have an else?
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });






    }



    private void writeNewOrder(String customerid, String customername, String customeremail, String customeraddress, String customercity, String customerpostcode, String driverid, String drivername, boolean driverEnroute, String itemid, int itemquantity, String requestedDeliveryDate, String deliveryRequestedForTime, String deliveredTime) {

        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
        order = new Order(customerid, customername, customeremail, customeraddress, customercity, customerpostcode, driverid, drivername, driverEnroute,  itemid, itemquantity, requestedDeliveryDate, deliveryRequestedForTime, deliveredTime);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //way of figuring out the key for this order, and adding it to the database in one go

        //should really be implemented as ID in classes.

        String pushID = orderRef.push().getKey();
        //giving class this id
        order.setId(pushID);

        //is this needed?
        //is push the best option?
        //id still like to categorise them by CUSTOMER id WITHIN FIREBASE ITSELF, for ease of access instead of sorting
        //SHOULD i consider that if their address changes.. should it affect the orders table in Firebase? (considering that archived might be diff)
        orderRef.child(pushID).setValue(order);


    }

    private void notifyDriverOnOrderPlaced() {

//        double t = (driverLoc.distanceTo(warehouse)/metresPerMinute) + posTime;
//        //https://stackoverflow.com/questions/5387371/how-to-convert-minutes-to-hours-and-minutes-hhmm-in-java
//        double hr = (t/60);
//        double mins = t%60;

        double t = (driverLoc.distanceTo(warehouse)/metresPerMinute) + posTime;
        //https://stackoverflow.com/questions/2654839/rounding-a-double-to-turn-it-into-an-int-java
        int timeInteger = (int) Math.round(t);

        //https://stackoverflow.com/questions/5387371/how-to-convert-minutes-to-hours-and-minutes-hhmm-in-java

//        double hr = (t/60);
//        double mins = t%60;

        int hr = timeInteger/60;
        int mins = timeInteger%60;

        System.out.println("The double of hr: " + hr);
        System.out.println("The double of mins: " + mins);



        //https://stackoverflow.com/questions/5480615/how-to-build-a-formatted-string-in-java
        //https://stackoverflow.com/questions/3693079/problem-with-system-out-printf-command-in-java
        String amountOfTimeToGetToWarehouse = String.format(Locale.UK, "%d.%02d", hr, mins);
        System.out.println("amount of time to get to warehouse : " + amountOfTimeToGetToWarehouse);



//        String amounOfTimeToGetToWarehouse = String.format("%d:%02d", hr, mins);

        //making sure that the currentTime is the current time.. i think this should be a way of resetting it
        Calendar timeToBeAtWarehouse = Calendar.getInstance();

        timeToBeAtWarehouse.add(Calendar.HOUR_OF_DAY, (int) hr);
        timeToBeAtWarehouse.add(Calendar.MINUTE, (int) mins);

        //https://stackoverflow.com/questions/2654025/how-to-get-year-month-day-hours-minutes-seconds-and-milliseconds-of-the-cur
        int hrToBeAtWarehouse = timeToBeAtWarehouse.get(Calendar.HOUR_OF_DAY);
        int minToBeAtWarehouse = timeToBeAtWarehouse.get(Calendar.MINUTE);

        String timeBeAtWarehouse = String.format(Locale.UK, "%d:%02d", hrToBeAtWarehouse, minToBeAtWarehouse);

        System.out.println("time you'll be at warehouse: " + timeBeAtWarehouse);

//        double tt = (driverLoc.distanceTo(warehouse)/metresPerMinute) + posTime + (warehouse.distanceTo(customerLoc)/metresPerMinute);
//        double hhr = (tt/60);
//        double mmins = tt%60;

        //String timeToLeaveWarehouse = String.format(Locale.UK, "%f:%02f", hhr, mmins);

        double timet = (warehouse.distanceTo(customerLoc)/metresPerMinute);
        //https://stackoverflow.com/questions/2654839/rounding-a-double-to-turn-it-into-an-int-java
        int timeint = (int) Math.round(timet);

        //https://stackoverflow.com/questions/5387371/how-to-convert-minutes-to-hours-and-minutes-hhmm-in-java

//        double hr = (t/60);
//        double mins = t%60;

        int hour = timeint/60;
        int minute = timeint%60;

        String hourandminutebetweenwarehouseandcustomer = String.format(Locale.UK, "%d:%02d", hour, minute);

        Calendar test = Calendar.getInstance();

//        test.add(Calendar.HOUR_OF_DAY, -(int) hhr);
//        test.add(Calendar.MINUTE, -(int) mmins);


//        hourInMinutes = time.get(Calendar.HOUR_OF_DAY) * 60;
//        minutes = time.get(Calendar.MINUTE);
//
//        String timeYouArrive = String.format(Locale.UK, "%f:%02f", hhr, mmins);


//
        //already been said in placeorder
        //requestedTime = String.format(Locale.UK, "%d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));


        String currentTimeCal = String.format(Locale.UK, "%d.%02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));


//        String durationToWarehouseHHMM = String.format(Locale.UK, "%f:%02f", hhr, mmins);

        double totalDurationMins = (driverLoc.distanceTo(warehouse)/metresPerMinute) + posTime + (warehouse.distanceTo(customerLoc)/metresPerMinute);
        int totalInt = (int) Math.round(totalDurationMins);
        int totalhour = totalInt/60;
        int totalminute = totalInt%60;

        Calendar total = Calendar.getInstance();
        total.add(Calendar.HOUR_OF_DAY, (int) totalhour);
        total.add(Calendar.MINUTE, (int) totalminute);

        String totalDurationOfJourney = String.format(Locale.UK, "%d:%02d", totalhour, totalminute);

        String timeOfArrival = String.format(Locale.UK, "%d:%02d", total.get(Calendar.HOUR_OF_DAY), total.get(Calendar.MINUTE));

        //https://www.youtube.com/watch?v=Z28s39brZJM
        System.out.println("********************************************************");
        System.out.println("Running inform driver method");
        System.out.println("********************************************************");


        //why have i called it customerUser
        driverPhoneNumber = aDriver.getPhoneNumber();
        System.out.println(driverPhoneNumber);

        SmsManager smsManager = SmsManager.getDefault();



        messageToDriver = "Current time + " + currentTimeCal + "\n" + " Hi driver " + aDriver.getId() +"\n" + ". An order has been placed for " + order.getItemQuantity() + " " +
                order.getItemID() + " at " + requestedDeliveryTime +"\n" + ". The address" +
                "is " + aCustomer.getAddress() + ", " + aCustomer.getCity() + ", " + aCustomer.getPostcode() +"\n" +
                ". You have " + ((driverLoc.distanceTo(warehouse)/metresPerMinute) + posTime) + " minutes to be at the warehouse aka "
                + amountOfTimeToGetToWarehouse + " in hours and minutes\n " +
                "Which means you should be there  " + timeBeAtWarehouse +" oclock\n" + ". You have " +
                (warehouse.distanceTo(customerLoc)/metresPerMinute) + " minutes to get from the warehouse to the customer "+
                "aka " + hourandminutebetweenwarehouseandcustomer + " hours and minutes\n " +
                "All in all this journey takes " + totalDurationMins + " aka " + totalDurationOfJourney + " in hhmm\n " + "So you should arrive there for " + timeOfArrival;

        System.out.println(messageToDriver);



        ArrayList<String> msgList = smsManager.divideMessage(messageToDriver);

        smsManager.sendMultipartTextMessage(driverPhoneNumber, null, msgList, null, null);

        smsManager.sendTextMessage(driverPhoneNumber, null, messageToDriver, null, null);


    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

}

