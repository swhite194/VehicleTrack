package com.example.swhit.vehicletracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swhit.vehicletracking.services.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");
    DatabaseReference currentUser = myRef.child("users");







    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


//    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();



    //northgate
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        btnOpenMap = (Button) findViewById(R.id.bt);
        btnDeleteLocationsFromFirebase = (Button) findViewById(R.id.btnDel);
        btnLogin = (Button) findViewById(R.id.btnLoginPage);
        btnEdit = (Button) findViewById(R.id.btnUserInfo);
        btnOrderPg = (Button) findViewById(R.id.btnOrderPage);

        btnUserRecordPage = (Button) findViewById(R.id.btnUserRecord);

        btnTestPage = (Button) findViewById(R.id.btnTestPage);
        btnTestPage2 = (Button) findViewById(R.id.btnTestPage2);
        btnRunService = (Button) findViewById(R.id.btnRunService);
        btnStopService = (Button) findViewById(R.id.btnStopService);
        btnGoEnroute = (Button) findViewById(R.id.btnEnroute);

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


        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            //https://stackoverflow.com/questions/24610527/how-do-i-get-a-button-to-open-another-activity-in-android-studio
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GoogleMapsActivity.class);
                startActivity(intent);
            }
        });

        btnDeleteLocationsFromFirebase.setOnClickListener(new View.OnClickListener() {

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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserInfo.class);
                startActivity(intent);
            }
        });

        //can i do the display user stuff just by this click (instead of opening a new page?)
        btnUserRecordPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DisplayUserRecord.class);
                startActivity(intent);
            }
        });

        btnOrderPg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlaceOrder.class);
                startActivity(intent);
            }
        });

        btnTestPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReferenceWork.class);
                startActivity(intent);
            }
        });

        btnTestPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReferenceWork2.class);
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
                } else{
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

        btnGoEnroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//northgate but I changed this to HomeActivity.this (the line below this comment)
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(HomeActivity.this, LocationService.class);
                    startService(intent);
                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                    }
                }
            }
        });


        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (dataSnapshot.child("Customers").hasChild(id)) {

                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
                    btnRunService.setVisibility(View.INVISIBLE);
                    btnStopService.setVisibility(View.INVISIBLE);
                    btnOrderPg.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Customer " + id, Toast.LENGTH_LONG).show();
                }
                if (dataSnapshot.child("Drivers").hasChild(id)) {
                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
                    btnOrderPg.setVisibility(View.INVISIBLE);
                    btnRunService.setVisibility(View.VISIBLE);
                    btnStopService.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Driver " + id, Toast.LENGTH_LONG).show();

                }
                if (dataSnapshot.child("Admins").hasChild(id)) {

                    btnDeleteLocationsFromFirebase.setVisibility(View.INVISIBLE);
                    btnRunService.setVisibility(View.INVISIBLE);
                    btnStopService.setVisibility(View.INVISIBLE);
                    btnOrderPg.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Admin " + id, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
