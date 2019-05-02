package com.example.swhit.vehicletracking;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

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
    //this should maybe be called something else, its a bit confusing..
    //i need to add validation in so that it cant be a driver that does this; "custID" almost makes it sound like thats the case already.
    String custID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //used to check availability status of driver
    boolean available = true;

//    LatLng custLatLng;
//    LatLng driverLatLng;


    //https://stackoverflow.com/questions/35508017/how-to-store-hour-and-minute-in-a-time-object
    TimePicker timePicker;

    Calendar time = Calendar.getInstance();

    Date dTime;

    double customerPickedTimeInMinutes;

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

    //equivalent of 30 miles/hr
    double metresPerMinute = 804.67;

    double timeToTravelToCustomerInMinutes;

    double differenceInTimes;

    boolean positiveTime = false;


    int hourInMinutes;
    int minutes;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        //calling it custID here instead of customer.id (the problem lies more in UserInfo's use of saying Customer customer TWICE.. THAT needs to be fixed.. this wasn't such a big deal - check fb messenger)
        //this is what id prefer for UserInfo.

        warehouse.setLatitude(54.58);
        warehouse.setLongitude(-5.93);

//        utime = findViewById(R.id.txtDeliveryTime);

        //https://www.c-sharpcorner.com/article/create-timepicker-android-app-using-android-studio/
        timePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(true);






        place_order = findViewById(R.id.btnOrder);

        huawei = findViewById(R.id.imgbtnHuawei);
        pixel = findViewById(R.id.imgbtnPixel);
        iphone = findViewById(R.id.imgbtnIphone);

        //https://www.c-sharpcorner.com/article/create-timepicker-android-app-using-android-studio/
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {


                //https://stackoverflow.com/questions/35508017/how-to-store-hour-and-minute-in-a-time-object
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);

                //https://stackoverflow.com/questions/9243578/java-util-date-and-getyear

                hourInMinutes = time.get(Calendar.HOUR_OF_DAY) * 60;
                minutes = time.get(Calendar.MINUTE);

                //what the user selects (this changes for click on that clock made), hours first and then minutes.
                customerPickedTimeInMinutes = hourInMinutes + minutes;

                System.out.println("Time requested in minutes: " + customerPickedTimeInMinutes);


//will be used to indicate the difference between a positive and negative result when comparing
                //the customer's selected delivery time vs the time that it will take for the driver to get to them
                //I calculate using Customer Requested Time (in minutes) minus the Time Taken to Travel to warehouse and then to customer (seen below)


                posTime = 0;
                negTime = -1000000000;

                myRef.child("time").setValue(time.get(Calendar.YEAR));

                drivers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                            //
                            if(snapshot.child("bookable").exists()){
                                if(snapshot.child("bookable").getValue().equals("available")){


                                    //https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points
                                    //should i make them classes first like i did for custLoc? cheaty?
                                    driverLoc.setLatitude(snapshot.child("latitude").getValue(Double.class));
                                    driverLoc.setLongitude(snapshot.child("longitude").getValue(Double.class));


                                    //estimated travel from driver's location to warehouse to customer's location

                                    distanceOfTravelToCustomer = (driverLoc.distanceTo(warehouse))+(warehouse.distanceTo(customerLoc));

//                            if(distanceOfTravel >= 0){
//
//                            }

                                    //value in minutes for driver (it's not exactly a "time")... whereas customertime is based off an actual 24 hour clock
                                    //consider that if the difference was like -1000 minutes, but the customer time was around 10pm, you can't do that because we're dealing with a 24 hour
                                    //TIME only event, the days aren't factored in..
                                    timeToTravelToCustomerInMinutes = distanceOfTravelToCustomer/metresPerMinute;

                                    differenceInTimes = customerPickedTimeInMinutes - timeToTravelToCustomerInMinutes;

                                    //customer time - driver time
                                    //the idea here is that we select the driver with the difference in times closest to 0
                                    //what this means is that if the customer were to pick a generous time, we'd allocate a driver who is perhaps currently further away, so as to reserve
                                    //a closer driver for requests that are maybe more immediate.

                                    if(((differenceInTimes)<= posTime)&&(differenceInTimes>=0)){
                                        posTime = differenceInTimes;
                                        System.out.println("Positive Time" + posTime);
                                        positiveTime = true;
                                        key = snapshot.getKey();
                                        System.out.println("Driver ID " + snapshot.child("id").getValue() + "Found a positive time");
                                    }

                                    //if the child's calculated difference between times is less than 0, meaning that that driver can't be there in time
                                    //then it considers a tally of the negative times, and ultimately selects the best negative time - i.e. the one closest 0 (whilst trying to find the largest positive time too)
                                    else if(((differenceInTimes>=negTime)&&(differenceInTimes)<0)){
                                        negTime = differenceInTimes;
                                        System.out.println("Negative Time" + negTime);
                                        key1 = snapshot.getKey();
                                        System.out.println("Driver ID " + snapshot.child("id").getValue() + "Found a negative time");
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Can't be done", Toast.LENGTH_LONG).show();

                                        System.out.println("Driver ID " + snapshot.child("id").getValue() + " ----Can't be done");
                                    }
//
//                                    if(positiveTime = true){
////                                aDriver = snapshot.getValue(Driver.class);
////                                aDriver = drivers.child(key)
//
//                                        //thiiiiiink that should work?
//                                        drivers.child(key).setValue(aDriver);
//
//                                        available = true;
//                                        Toast.makeText(getApplicationContext(), "This time is okay!", Toast.LENGTH_LONG).show();
//                                    }
//
//                                    //consider the negative value.. currently no drivers are able to take the customer in that elected time, so we suggest a time that by our predictions will allow for that negative driver to
//                                    //be with the customer in time
//
//                                    else if(positiveTime = false){
////                                aDriver = snapshot.getValue(Driver.class);
//
//                                        double differenceBetweenNegativeand0 = Math.max(0, negTime);
//
////                                     value in minutes for driver (it's not exactly a "time")... whereas customertime is based off an actual 24 hour clock
////                                                                        consider that if the difference was like -1000 minutes, but the customer time was around 10pm, you can't do that because we're dealing with a 24 hour
////                                                                        TIME only event, the days aren't factored in.. might want to think about changing terminology of "time" for the driverTime stuff
//
//                                        Toast.makeText(getApplicationContext(), "Please book " + differenceBetweenNegativeand0 + " minutes from this time!", Toast.LENGTH_LONG).show();
//
//
//                                    }
//                                    else{
//                                        Toast.makeText(getApplicationContext(), "No child key", Toast.LENGTH_LONG).show();
//                                    }




//                            aDriver = snapshot.getValue(Driver.class);

//                            System.out.println(aDriver.getId() + " " + driverLoc + " distance to warehouse: " + distanceOfTravel);
//                            available = true;
                                    //not bad practice?
//                            break;
                                }
                                if(snapshot.child("bookable").getValue().equals("unavailable")){
                                    available = false;
                                }
                            }
                            if(!(snapshot.child("bookable").exists())){
                                //https://stackoverflow.com/questions/11160952/goto-next-iteration-in-for-loop-in-java
                                continue;
                            }
//
//
// check = snapshot.child("bookable").getValue(String.class);
                            //works backwards but is fine... or is it the submit/writeorder stuff that does?
//                    if (check.equals("available")){
//                        aDriver = snapshot.getValue(Driver.class);
//                        System.out.println(check);
//                    }


                            //NEED TO SAY ELSE HERE.
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        //for old times
//        oldTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
//        oldTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));




        //changes based on phone clicked


//this is a bad name for it here. considering its used for current user BUT also to see into driver.
//        final DatabaseReference currentUser = myRef.child("users");
//        final DatabaseReference driverRef = myRef.child("users").child("Drivers").child(driver.id);
        //https://stackoverflow.com/questions/43265668/checking-if-field-data-changed-rather-than-any-field-in-child-data/43265932 -WHAT DOES THIS APPLY TO? just this paragraph or the next?


//should this be value rather than single? yeah?
        customers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(custID)) {
                    //this is good, but in other classes, customer is being made redundant , and the use of customer.id is cheaty
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
                System.out.println(aDriver.getEmail());

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
                if(available) {
                    writeNewOrder(aCustomer.getId(), aCustomer.getName(), aCustomer.getEmail(), aCustomer.getAddress(), aCustomer.getCity(), aCustomer.getPostcode(), aDriver.getId(), aDriver.getName(), aDriver.isEnroute(), item_id, item_quantity);
//                aDriver.setBookable(false);
                    aDriver.setBookable("unavailable");
                    myRef.child("users").child("Drivers").child(aDriver.getId()).setValue(aDriver);

//                    custLatLng = (aCustomer.getLatitude(), aCustomer.getLongitude());

                }
                if(!available){
                    Toast.makeText(getApplicationContext(), "Sorry no drivers are available on the system!", Toast.LENGTH_LONG).show();
                }
                //should i have an else?
            }
        });




    }
    private void writeNewOrder(String customerid, String customername, String customeremail, String customeraddress, String customercity, String customerpostcode, String driverid, String drivername, boolean driverEnroute, String itemid, int itemquantity) {

        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
        Order order = new Order(customerid, customername, customeremail, customeraddress, customercity, customerpostcode, driverid, drivername, driverEnroute,  itemid, itemquantity);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();




        //is this needed?
        //is push the best option?
        //id still like to categorise them by CUSTOMER id WITHIN FIREBASE ITSELF, for ease of access instead of sorting
        //SHOULD i consider that if their address changes.. should it affect the orders table in Firebase? (considering that archived might be diff)
        orderRef.push().setValue(order);


    }
}

