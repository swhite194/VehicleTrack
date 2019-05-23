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

public class AdminEditCurrentOrder extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");

    DatabaseReference currentOrdersRef = myRef.child("orders").child("Current Orders");
    DatabaseReference enRouteOrderRef = myRef.child("orders").child("Enroute Orders");
    DatabaseReference completedOrders = myRef.child("orders").child("Completed Orders");
    DatabaseReference driversRef = myRef.child("users").child("Drivers");
    DatabaseReference customersRef = myRef.child("users").child("Customers");



    Button place_order;
    ImageButton huawei, pixel, iphone;
    FirebaseAuth firebaseAuth;


    String item_id;
    int item_quantity;
    Customer aCustomer = new Customer();
    Driver aDriver = new Driver();
    Order order = new Order();

    String driverID;

    String custID ;

    //used to check availability status of driver
    boolean available = true;

//    LatLng custLatLng;
//    LatLng driverLatLng;


    //https://stackoverflow.com/questions/35508017/how-to-store-hour-and-minute-in-a-time-object
    TimePicker timePicker;

    //change name
    Calendar time = Calendar.getInstance();
    Calendar currentTime = Calendar.getInstance();

    Calendar ordersOldTime = Calendar.getInstance();

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

    String oldRequestedDeliveryTime;

    String requestedDeliveryDate;
    String requestedDeliveryTime;




    //https://stackoverflow.com/questions/8745297/want-current-date-and-time-in-dd-mm-yyyy-hhmmss-ss-format
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

String orderIdFromListClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_current_order);
//taking in order based on adapter position click from AdminEditCustomersCurrentOrder
        orderIdFromListClick = getIntent().getExtras().getString("userId");


        warehouse.setLatitude(54.79);
        warehouse.setLongitude(-7.45);

//        utime = findViewById(R.id.txtDeliveryTime);

        //https://www.c-sharpcorner.com/article/create-timepicker-android-app-using-android-studio/
        timePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(true);



currentOrdersRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        order = dataSnapshot.child(orderIdFromListClick).getValue(Order.class);
        //check to see if driver has since gone enroute
        if(!order.isDriverEnroute()){
            oldRequestedDeliveryTime = order.getDeliveryRequestedForTime();
            //fetch custID string
            custID = order.getCustomerID();
            //fetch driverID string
            driverID = order.getDriverID();
        }else{
            //if at any point they go enroute before order is updated, exit this screen
            Intent intent = new Intent(AdminEditCurrentOrder.this, AdminCustomerCurrentOrders.class);
            startActivity(intent);
        }


    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

        customersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //fetch driver from that order, from drivers ref in firebase
                aCustomer = dataSnapshot.child(custID).getValue(Customer.class);
                customerLoc.setLatitude(aCustomer.getLatitude());
                customerLoc.setLongitude(aCustomer.getLongitude());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //DECIDING TO STAY WITH SAME DRIVER, as changes to firebase (such as making the driver available) would be saved if crash

driversRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        //fetch driver from that order, from drivers ref in firebase
        aDriver = dataSnapshot.child(driverID).getValue(Driver.class);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});








        place_order = findViewById(R.id.btnUpdate);

        huawei = findViewById(R.id.imgbtnHuawei);
        pixel = findViewById(R.id.imgbtnPixel);
        iphone = findViewById(R.id.imgbtnIphone);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        //https://stackoverflow.com/questions/9872419/how-to-convert-a-string-to-a-date-using-simpledateformat
        //need a try catch when using a parse method
//        try {
//            //updating calendar called "time" with the old requested delivery time
//            time.setTime(sdf.parse(oldRequestedDeliveryTime));
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }



        //set the time picker to that of the previous order
        timePicker.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(time.get(Calendar.MINUTE));


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
                                                    if (time.getTimeInMillis() < currentTime.getTimeInMillis()) {
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


                                                    driverLoc.setLatitude(aDriver.getLatitude());
                                                    driverLoc.setLongitude(aDriver.getLongitude());


                                                    distanceOfTravelToCustomer = (driverLoc.distanceTo(warehouse)) + (warehouse.distanceTo(customerLoc));


//                            if(distanceOfTravel >= 0){
//
//                            }

                                                    //value in minutes for driver (it's not exactly a "time")... whereas customertime is based off an actual 24 hour clock
                                                    //consider that if the difference was like -1000 minutes, but the customer time was around 10pm, you can't do that because we're dealing with a 24 hour
                                                    //TIME only event, the days aren't factored in..
                                                    timeToTravelToCustomerInMinutes = distanceOfTravelToCustomer / metresPerMinute;


                                                    differenceInTimes = durationUntilRequestedTimeInMinutes - timeToTravelToCustomerInMinutes;


                                                    if (((differenceInTimes) <= posTime) && (differenceInTimes >= 0)) {
                                                        posTime = differenceInTimes;
//                                        System.out.println("Positive Time" + posTime);
                                                        positiveTime = true;

                                                        System.out.println("Customer Requests Delivery For this time: " + customerPickedTimeInMinutes);
                                                        System.out.println("Duration until that time: " + durationUntilRequestedTimeInMinutes);
                                                        System.out.println("Driver's travel distance to get to customer: " + timeToTravelToCustomerInMinutes);
                                                        System.out.println("Difference" + differenceInTimes);
                                                        System.out.println("Positive Time " + posTime);

                                                    } else if (((differenceInTimes >= negTime) && (differenceInTimes) < 0)) {
                                                        negTime = differenceInTimes;
                                                        System.out.println("Customer Requests Delivery For this time: " + customerPickedTimeInMinutes);
                                                        System.out.println("Duration until that time: " + durationUntilRequestedTimeInMinutes);
                                                        System.out.println("Driver's travel distance to get to customer: " + timeToTravelToCustomerInMinutes);
                                                        System.out.println("Difference" + differenceInTimes);
                                                        System.out.println("Negative Time " + negTime);

                                                    }

                                                    //not sure what this else is really for.

                                                    else {
                                                        Toast.makeText(getApplicationContext(), "Can't be done", Toast.LENGTH_LONG).show();
                                                        System.out.println("Customer Requests Delivery For this time: " + customerPickedTimeInMinutes);
                                                        System.out.println("Duration until that time: " + durationUntilRequestedTimeInMinutes);
                                                        System.out.println("Driver's travel distance to get to customer: " + timeToTravelToCustomerInMinutes);
                                                        System.out.println("Difference" + differenceInTimes);

                                                    }


                                                    if (positiveTime) {

                                                        Toast.makeText(getApplicationContext(), "This time is okay!", Toast.LENGTH_LONG).show();

                                                    }

                                                    //if not positive
                                                    else {

                                                        double durationUntilOK = negTime * -1;

                                                        Toast.makeText(getApplicationContext(), "Please book " + durationUntilOK + " minutes from this time!", Toast.LENGTH_LONG).show();


                                                    }

                                                }
                                                //end of timechangedlistener
                                            });


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
                        //just dealing with 24 hr single days atm.. not Days.
                        UpdateCurrentOrder(item_id, item_quantity, requestedDeliveryTime);
//                aDriver.setBookable(false);

                        notifyDriverOnOrderPlaced();

                        aDriver.setBookable("unavailable");
                        //changing availability to unavailable
                        myRef.child("users").child("Drivers").child(aDriver.getId()).setValue(aDriver);


//                    custLatLng = (aCustomer.getLatitude(), aCustomer.getLongitude());

                    }
                    if (!available) {
                        Toast.makeText(getApplicationContext(), "Sorry no drivers are available on the system!", Toast.LENGTH_LONG).show();
                    }
                    //should i have an else?
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });










    }

    private void UpdateCurrentOrder(String itemid, int itemquantity, String deliveryRequestedForTime) {

        order.setItemID(itemid);
        order.setItemQuantity(itemquantity);
        order.setDeliveryRequestedForTime(deliveryRequestedForTime);


        currentOrdersRef.child(orderIdFromListClick).setValue(Order.class);



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



        messageToDriver = "Current time + " + currentTimeCal + "\n" + " Hi driver " + aDriver.getId() +"\n" + ". The order for " + requestedDeliveryTime + " has now been updated to " + order.getItemQuantity() + " " +
                order.getItemID() +"\n" +
                ". You have " + ((driverLoc.distanceTo(warehouse)/metresPerMinute) + posTime) + " minutes to be at the warehouse aka "
                + amountOfTimeToGetToWarehouse + " in hours and minutes\n " +
                "Which means you should be there  " + timeBeAtWarehouse +" oclock\n" + ". You have " +
                (warehouse.distanceTo(customerLoc)/metresPerMinute) + " minutes to get from the warehouse to the customer "+
                "aka " + hourandminutebetweenwarehouseandcustomer + " hours and minutes\n " +
                "All in all this journey takes " + totalDurationMins + " aka " + totalDurationOfJourney + " in hh:mm\n " + "So you should arrive there for " + timeOfArrival;

        System.out.println(messageToDriver);


////on its own this is too long
//        messageToDriver = "Hi driver " + aDriver.getId() + ". An order has been placed for " + order.getItemQuantity() + " " + order.getItemID() + " at " + requestedTime + ". The address" +
//                "is " + aCustomer.getAddress() + ", " + aCustomer.getCity() + ", " + aCustomer.getPostcode() + ". The time requested is " + time.get(Calendar.HOUR_OF_DAY) + ":" +
//                time.get(Calendar.MINUTE) + " so please be at the warehouse " + amountOfTimeToGetToWarehouse + " from now which means you should be there for " + timeToBeAtWarehouse + ". At " + (warehouse.distanceTo(customerLoc)/metresPerMinute) + " minutes to be there "+
//                "aka " + timeToLeaveWarehouse + " so you should arrive at " + timeYouArrive;
//
//
//
//
//
//                SmsManager smsManager = SmsManager.getDefault();
//
//        smsManager.sendTextMessage(driverPhoneNumber, null, messageToDriver, null, null);

        //on its own this is too long but a shorter message works fine




        ArrayList<String> msgList = smsManager.divideMessage(messageToDriver);

        smsManager.sendMultipartTextMessage(driverPhoneNumber, null, msgList, null, null);




        smsManager.sendTextMessage(driverPhoneNumber, null, messageToDriver, null, null);


    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);

    }


}
