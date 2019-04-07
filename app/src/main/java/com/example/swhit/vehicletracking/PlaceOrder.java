package com.example.swhit.vehicletracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlaceOrder extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");


    EditText utime;
    Button place_order;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        Customer customer = new Customer();
        Driver driver = new Driver();

        customer.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        driver.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        utime = findViewById(R.id.txtDeliveryTime);
        place_order = findViewById(R.id.btnOrder);

    }
}
