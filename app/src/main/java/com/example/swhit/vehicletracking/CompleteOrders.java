package com.example.swhit.vehicletracking;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//copied and pasted these from Login/Register

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Locale;

public class CompleteOrders extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");


    //    EditText uname, uemail, uphonenumber, uaddress, ucity, upostcode, ulatitude, ulongitude, uEnroute, uBookable;
    TextView txtCustomerName, txtEmail, txtCustAddress, txtCustCity, txtCustPostcode, txtDriverId, txtDriverName, txtItemID, txtItemQuantity;
    TextView txtRequestedDate, txtRequestedTime, txtDeliveredTime;
    Button btnSubmit;

    LinearLayout userContactDetailsSection, custAndAdminAddressSection, userLatLongSection, driverStatusSection, submitButtonSection;

//    FirebaseAuth firebaseAuth;

    Customer customer = new Customer();
    Driver driver = new Driver();
    Admin admin = new Admin();

    boolean checkDriver;

    String oldAddress, oldCity, oldPostcode;

    boolean okAddress;

    String orderIdFromSearch;

    DatabaseReference currentUser = myRef.child("users");
    DatabaseReference completedOrders = myRef.child("orders").child("Completed Orders");

//    DatabaseReference currentCustomer = myRef.child("users").child("Customers").child(customer.id);
//    DatabaseReference currentDriver = myRef.child("users").child("Drivers").child(driver.id);

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Order order = new Order();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_orders);


        orderIdFromSearch = getIntent().getExtras().getString("orderID");
        Toast.makeText(getApplicationContext(), "USER ID : " + orderIdFromSearch, Toast.LENGTH_LONG).show();


        //        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase


//
//
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //why does it ask for these to be final?
//        userContactDetailsSection = (LinearLayout) findViewById(R.id.UserContactDetailsSection);
//        custAndAdminAddressSection = (LinearLayout) findViewById(R.id.CustAndAdminAddressSection);
//        userLatLongSection = (LinearLayout) findViewById(R.id.UserLatLongSection);
//        driverStatusSection = (LinearLayout) findViewById(R.id.DriverStatusSection);
//        submitButtonSection = (LinearLayout) findViewById(R.id.SubmitButtonSection);


        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtEmail = findViewById(R.id.txtEmail);
        txtCustAddress = findViewById(R.id.txtCustAddress);
        txtCustCity = findViewById(R.id.txtCustCity);
        txtCustPostcode = findViewById(R.id.txtCustPostcode);
        txtDriverId = findViewById(R.id.txtDriverId);
        txtDriverName = findViewById(R.id.txtDriverName);
        txtItemID = findViewById(R.id.txtItemId);
        txtItemQuantity = findViewById(R.id.txtItemQuantity);
        txtRequestedDate = findViewById(R.id.txtRequestedDeliveryDate);
        txtRequestedTime = findViewById(R.id.txtRequestedTime);
        txtDeliveredTime = findViewById(R.id.txtDeliveredTime);
        btnSubmit = findViewById(R.id.btnSubmit);


//        String userAddress = uaddress.getText().toString();
//        String userCity = ucity.getText().toString();
//        String userPostcode = upostcode.getText().toString();
//
//        String completeAddress = userAddress + " " + userCity + " " + userPostcode;

//https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
        //first answer, but this is for Reverse Geocoding; so I switched it to getFromLocationName (because I want address -> coordinates)
        //combined with this
        // https://stackoverflow.com/questions/9698328/how-to-get-coordinates-of-an-address-in-android


//
//        geocoder = new Geocoder(this, Locale.getDefault());
//        //needed to be in a try catch as suggested by android studio
//        try {
//            addresses = geocoder.getFromLocationName(completeAddress, 1);
//            double longitude = addresses.get(0).getLongitude();
//            double latitude = addresses.get(0).getLatitude();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //trying this way: https://stackoverflow.com/questions/9698328/how-to-get-coordinates-of-an-address-in-android (last example!)

//        ucity.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Geocoder geocoder = new Geocoder(this);
//                List<Address> addresses = null;
//
//                //needed to be in a try catch as suggested by android studio
//                try {
//                    addresses = geocoder.getFromLocationName(completeAddress, 1);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (addresses.size()>0) {
//
//                    double latitude = addresses.get(0).getLatitude();
//                    double longitude = addresses.get(0).getLongitude();
//
//                    String lat = String.valueOf(latitude);
//                    String lon = String.valueOf(longitude);
//
//                    ulatitude.setText(lat);
//                    ulongitude.setText(lon);
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


        //need to figure out how to use Getter to keep this populated (with User class data?)


//        userName = myRef.child("users").child(user.id).child("name");

        //https://www.firebase.com/docs/java-api/javadoc/com/firebase/client/DataSnapshot.html#getValue(java.lang.Class)
        //https://stackoverflow.com/questions/37830692/parsing-from-datasnapshot-to-java-class-in-firebase-using-getvalue
//https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase

//        DatabaseReference currentUser = myRef.child("users").child(user.id);

        //should it be customer.getid? that would prove if its linked with the class.. worth checking


        //https://stackoverflow.com/questions/38017765/retrieving-child-value-firebase
        //made this single so that it wouldn't keep overwriting while running LocationService

        completedOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(orderIdFromSearch)) {
                        order = ds.getValue(Order.class);


                        txtCustomerName.setText(order.getCustomerName());
                        txtEmail.setText(order.getCustomerEmail());
                        txtCustAddress.setText(order.getCustomerAddress());
                        txtCustCity.setText(order.getCustomerCity());
                        txtCustPostcode.setText(order.getCustomerPostcode());
                        txtDriverId.setText(order.getDriverID());
                        txtDriverName.setText(order.getDriverName());
                        txtItemID.setText(order.getItemID());
                        txtItemQuantity.setText(order.getItemQuantity());
                        txtRequestedDate.setText(order.getDeliveryRequestedForDate());
                        txtRequestedTime.setText(order.getDeliveryRequestedForTime());
                        txtDeliveredTime.setText(ds.child("Delivered Time").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompleteOrders.this, HomeActivity.class);
                startActivity(intent);
            }

        });
    }
}



