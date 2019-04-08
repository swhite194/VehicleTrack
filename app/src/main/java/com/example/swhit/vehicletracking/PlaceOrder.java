package com.example.swhit.vehicletracking;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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


    int item_id=0;
    Customer customer = new Customer();
    Driver driver = new Driver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        //calling it custID here instead of customer.id (the problem lies more in UserInfo's use of saying Customer customer TWICE.. THAT needs to be fixed.. this wasn't such a big deal - check fb messenger)
        //this is what id prefer for UserInfo.
        final String custID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        utime = findViewById(R.id.txtDeliveryTime);
        place_order = findViewById(R.id.btnOrder);

        huawei = findViewById(R.id.imgbtnHuawei);
        pixel = findViewById(R.id.imgbtnPixel);
        iphone = findViewById(R.id.imgbtnIphone);

        //changes based on phone clicked


//this is a bad name for it here. considering its used for current user BUT also to see into driver.
//        final DatabaseReference currentUser = myRef.child("users");
        final DatabaseReference currentCustomer = myRef.child("users").child("Customers").child(customer.id);
        final DatabaseReference driverRef = myRef.child("users").child("Drivers").child(driver.id);


        currentCustomer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Customers").hasChild(custID)) {
         //this is good, but in other classes, customer is being made redundant , and the use of customer.id is cheaty
                    customer = dataSnapshot.child("Customers").child(custID).getValue(Customer.class);
                    //how do i then make use of this data???


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//ITERATE THROUGH ALL DRIVERS TO SEE WHO IS AVAILABLE
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })

        huawei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //does this go here


                item_id = 1;

            }
        });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewOrder(custID,);
            }
        });




    }
    private void writeNewOrder(String customerid, String customername, String customeremail, String customeraddress, String customercity, String customerpostcode, String driverid, String drivername, int itemid, int itemquantity) {

        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
        Order order = new Order(customerid, customername, customeremail, customeraddress, customercity, customerpostcode, driverid, drivername, itemid, itemquantity);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        order.id = FirebaseAuth.getInstance().getCurrentUser().getUid();



        //is this needed?
        myRef.child("orders").child(customerid).setValue(order);


    }
}

