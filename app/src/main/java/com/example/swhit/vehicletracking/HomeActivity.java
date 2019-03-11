package com.example.swhit.vehicletracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {
//https://developer.android.com/studio/write/layout-editor

    Button btnOpenMap;
    Button btnLogin;
    Button btnEdit;
    Button btnDeleteLocationsFromFirebase;
    TextView txtView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFirebaseDatabase = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
        DatabaseReference myRef = mFirebaseDatabase.getReference("Location");


        btnOpenMap = (Button) findViewById(R.id.bt);
        btnDeleteLocationsFromFirebase = (Button) findViewById(R.id.btnDel);
        btnLogin = (Button) findViewById(R.id.btnLoginPage);
        btnEdit = (Button) findViewById(R.id.btnUserInfo);

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            //https://stackoverflow.com/questions/24610527/how-do-i-get-a-button-to-open-another-activity-in-android-studio
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GoogleMapsActivity.class);
                startActivity(intent);
            }
        });

        btnDeleteLocationsFromFirebase.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DatabaseReference myRef = mFirebaseDatabase.getReference("Location");
                myRef.removeValue();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginOrRegister.class);
                startActivity(intent);
            }
        });


    }
}
