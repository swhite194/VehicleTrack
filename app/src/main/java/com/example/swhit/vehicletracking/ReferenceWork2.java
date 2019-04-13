package com.example.swhit.vehicletracking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReferenceWork2 extends AppCompatActivity {

    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private TextView accuracy;
    private TextView speed;
    private TextView sensorType;
    private TextView updatesOnOff;
    private ToggleButton switchGpsBalanced;
    private ToggleButton locationOnOff;

    private FusedLocationProviderClient fusedLocationProviderClient;
    //can be any value; used to show we're working with the fine location
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;

    //pt4
    //1.30
    //used to set parameters for the provider; meaning it looks at the period and what types of stuff u will use
    private LocationRequest locationRequest;
    //this is for the provider client
    private LocationCallback locationCallback;
    private boolean updatesOn = false;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");

    Driver driverUser = new Driver();

    //we need to make it so that only drivers can do this; saying id makes it sound like theres validation in place already but thats not the case.
    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //its only fair to put in validation when u decipher what ref you're using.. instead of just making it 1 way to suit a use case with no validation
    DatabaseReference drivers = myRef.child("users").child("Drivers");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_work2);

        latitude = (TextView) findViewById(R.id.txtLatitude);
        longitude = (TextView) findViewById(R.id.txtLongitude);
        altitude = (TextView) findViewById(R.id.txtAltitude);
        accuracy = (TextView) findViewById(R.id.txtAccuracy);
        speed = (TextView) findViewById(R.id.txtSpeed);
        sensorType = (TextView) findViewById(R.id.txtSensor);
        updatesOnOff = (TextView) findViewById(R.id.txtUpdates);
        switchGpsBalanced = (ToggleButton) findViewById(R.id.tbGps_Balanced);
        locationOnOff = (ToggleButton) findViewById(R.id.tvLocationOnOff);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);//use slower one in future
        locationRequest.setFastestInterval(3000);//used in cases where something else would be updating the locs? yt
        //tells provider which sensor it will use
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);



        //find current driver - no validation..
        drivers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id)) {
                    //this is good, but in other classes, customer is being made redundant , and the use of customer.id is cheaty
                    driverUser = dataSnapshot.child(id).getValue(Driver.class);

                    //how do i then make use of this data???


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        switchGpsBalanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchGpsBalanced.isChecked()) {
                    //using GPS only
                    sensorType.setText("GPS");
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                } else {
                    //using balanced power accuracy
                    sensorType.setText("Cell Tower and WiFi");
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        });

        locationOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationOnOff.isChecked()) {
                    //location updates on
                    //starts picking up continuous locations! :D lockito works well!!
                    updatesOnOff.setText("On");
                    updatesOn = true;
                    startLocationUpdates();
                } else {
                    //location updates off
                    updatesOnOff.setText("Off");
                    updatesOn = false;
                    stopLocationUpdates();
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //are we worried about failure?

                    if (location != null) {
                        //if we know the last known location, set the text fields to it; although this might be a security issue? considering different users
                        //however, it can be assumed that there wouldn't be different users on it
                        //should these be set to fetch from firebase or leave it how it is?
                        latitude.setText(String.valueOf(location.getLatitude()));
                        longitude.setText(String.valueOf(location.getLongitude()));

                        ///should this be left alone? bit confused! no i wouldn't put it in here actually.. it would affect the position of the truck if you're reading that value from firebase.
//                        driverUser.setLatitude(location.getLatitude());
//                        driverUser.setLongitude(location.getLongitude());

                        //should this be in here - it works as it is
//                        myRef.child("users").child("Drivers").child(driverUser.getId()).setValue(driverUser);

                        accuracy.setText(String.valueOf(location.getAccuracy()));
                        if (location.hasAltitude()) {
                            altitude.setText(String.valueOf(location.getAltitude()));
                        } else {
                            altitude.setText("none");
                        }
                        if (location.hasSpeed()) {
                            speed.setText(String.valueOf(location.getSpeed() + "m/s"));
                        } else {
                            speed.setText("none");
                        }
                    }
                }
            });

        } else {
            //request the permissions
            //14:50 of part 3
            //16:00 - important for report!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //override result?
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    //update UI with location data
                    if (location != null) {
                        latitude.setText(String.valueOf(location.getLatitude()));
                        longitude.setText(String.valueOf(location.getLongitude()));

                        driverUser.setLatitude(location.getLatitude());
                        driverUser.setLongitude(location.getLongitude());

                        myRef.child("users").child("Drivers").child(driverUser.getId()).setValue(driverUser);

                        accuracy.setText(String.valueOf(location.getAccuracy()));
                        if (location.hasAltitude()) {
                            altitude.setText(String.valueOf(location.getAltitude()));
                        } else {
                            altitude.setText("none");
                        }
                        if (location.hasSpeed()) {
                            speed.setText(String.valueOf(location.getSpeed() + "m/s"));
                        } else {
                            speed.setText("none");
                        }
                    }
                }
            }


        };
    }

    //16:40 will be populated with the results of the dialog box (which would be either allowing or denying the permissions; the grant results side of things)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//permission was granted.. do nothing and carry on
                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;
        }
    }

    //this is apparently called on when we start the app?
    //these are method we pull in by typing onR.. and your man made a point about them being outside/inside the onCreate
    //watch yt/read about this.
    @Override
    protected void onResume() {
        super.onResume();
        if (updatesOn) startLocationUpdates();
    }

    //created this (not my personally)
    private void startLocationUpdates() {
        //this is for the fused provider client member
        //looper set to null ensures that we will be calling the location updates from this thread, not from another new one
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            //why are we adding this again?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    //created this
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}

//by the looks of it, up to part 3, the location changes every time i load that page up.