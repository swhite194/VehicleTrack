package com.example.swhit.vehicletracking;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfo extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");

    EditText uname, email, latitude, longitude;
    Button submit_button;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        uname = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        latitude = findViewById(R.id.txtLatitude);
        longitude = findViewById(R.id.txtLongitude);



        //need to figure out how to use Getter to keep this populated (with User class data?)

        User user = new User();
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        userName = myRef.child("users").child(user.id).child("name");
        user.setName(myRef.child("users").child(user.id).child("name").getV);


       submit_button = findViewById(R.id.btnSubmit);

       submit_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String n = uname.getText().toString();
               String e = email.getText().toString();
               String la = latitude.getText().toString();
               String lo = longitude.getText().toString();

               //https://stackoverflow.com/questions/5769669/convert-string-to-double-in-java
               double doLa = Double.parseDouble(la);
               double doLo = Double.parseDouble(lo);

                //if im continuing to use this... then https://firebase.google.com/docs/database/android/read-and-write amongst other sources.
               writeNewUser(n, e, doLa, doLo);
           }
       });
    }



    //https://firebase.google.com/docs/database/android/read-and-write
    //why does this need userID but the example of GoogleMapsActivity doesn't
    private void writeNewUser(String name, String email, double latitude, double longitude) {
        User user = new User(name, email, latitude, longitude);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //is this needed?
        myRef.child("users").child(user.id).setValue(user);


    }

    private void populateUserTextFields(String name, String email, double latitude, double longitude){
        User user = new User(name, email, latitude, longitude);
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        user.setName(myRef.child("users").child(user.id).toString());
        uname.setText(user.getName());


    }
}
