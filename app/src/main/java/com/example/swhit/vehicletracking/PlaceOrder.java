package com.example.swhit.vehicletracking;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlaceOrder extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");


    EditText utime;
    Button place_order;
    ImageButton huawei, pixel, iphone;
    FirebaseAuth firebaseAuth;


    String item_id;
    int item_quantity;
    Customer aCustomer = new Customer();
    Driver aDriver = new Driver();
    String custID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //used to check availability status of driver
    String check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        //calling it custID here instead of customer.id (the problem lies more in UserInfo's use of saying Customer customer TWICE.. THAT needs to be fixed.. this wasn't such a big deal - check fb messenger)
        //this is what id prefer for UserInfo.



        utime = findViewById(R.id.txtDeliveryTime);
        place_order = findViewById(R.id.btnOrder);

        huawei = findViewById(R.id.imgbtnHuawei);
        pixel = findViewById(R.id.imgbtnPixel);
        iphone = findViewById(R.id.imgbtnIphone);

        //changes based on phone clicked


//this is a bad name for it here. considering its used for current user BUT also to see into driver.
//        final DatabaseReference currentUser = myRef.child("users");
//        final DatabaseReference driverRef = myRef.child("users").child("Drivers").child(driver.id);
        //https://stackoverflow.com/questions/43265668/checking-if-field-data-changed-rather-than-any-field-in-child-data/43265932 -WHAT DOES THIS APPLY TO? just this paragraph or the next?
        DatabaseReference drivers = myRef.child("users").child("Drivers");
        DatabaseReference customers = myRef.child("users").child("Customers");

//should this be value rather than single? yeah?
        customers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(custID)) {
                    //this is good, but in other classes, customer is being made redundant , and the use of customer.id is cheaty
                    aCustomer = dataSnapshot.child(custID).getValue(Customer.class);

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
        drivers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    check = snapshot.child("bookable").getValue(String.class);
                    //works backwards but is fine... or is it the submit/writeorder stuff that does?
                    if (check.equals("available")){
                        aDriver = snapshot.getValue(Driver.class);
                        System.out.println(check);
                    }

                    //NEED TO SAY ELSE HERE.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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
                if(check.equals("available")) {
                    writeNewOrder(aCustomer.getId(), aCustomer.getName(), aCustomer.getEmail(), aCustomer.getAddress(), aCustomer.getCity(), aCustomer.getPostcode(), aDriver.getId(), aDriver.getName(), item_id, item_quantity);
//                aDriver.setBookable(false);
                    aDriver.setBookable("unavailable");
                    myRef.child("users").child("Drivers").child(aDriver.getId()).setValue(aDriver);
                }
                if(check.equals("unavailable")){
                    Toast.makeText(getApplicationContext(), "Sorry no drivers are available!", Toast.LENGTH_LONG).show();
                }
                //should i have an else?
            }
        });




    }
    private void writeNewOrder(String customerid, String customername, String customeremail, String customeraddress, String customercity, String customerpostcode, String driverid, String drivername, String itemid, int itemquantity) {

        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
        Order order = new Order(customerid, customername, customeremail, customeraddress, customercity, customerpostcode, driverid, drivername, itemid, itemquantity);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();




        //is this needed?
        //is push the best option?
        //id still like to categorise them by CUSTOMER id WITHIN FIREBASE ITSELF, for ease of access instead of sorting
        //SHOULD i consider that if their address changes.. should it affect the orders table in Firebase? (considering that archived might be diff)
        myRef.child("orders").child("Orders").push().setValue(order);


    }
}

