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

public class ReferenceWork extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_work);

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
                    if (location != null) {
                        latitude.setText(String.valueOf(location.getLatitude()));
                        longitude.setText(String.valueOf(location.getLongitude()));
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