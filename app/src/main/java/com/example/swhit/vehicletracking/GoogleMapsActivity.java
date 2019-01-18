package com.example.swhit.vehicletracking;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");

    DatabaseReference myRef = database.getReference("Location");
    DatabaseReference latRef = myRef.child("Latitude");
    DatabaseReference longRef = myRef.child("Longitude");

    Double[] latArray;
    Double[] longArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//------------------------------------------------------------------------------------------------------------------------------
        //not currently working
        // firstly, an attempt to read through all the longitudes and latitudes and compile them into an array.
        //(from firebase to app)
        //https://stackoverflow.com/questions/49324085/retrieve-location-from-firebase-and-put-marker-on-google-map-api-for-android-app
        //needs look at more with something like https://stackoverflow.com/questions/30564735/android-firebase-simply-get-one-child-objects-data
        //would be best sorted into couples under a child name instead of current structure


//
//
//
//        Double lat;
//        Double longt;
//
//        //add an event listener that will read all of the latitude values and add them to an array
//        latRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int x = 0;
//                for (DataSnapshot snapm : dataSnapshot.getChildren()) {
//                    Double latitude = snapm.child("(x)").getValue(Double.class);
//                    latArray.add(latitude);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        //add an event listener that will read all of the longitude values and add them to an array
//        longRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int x = 0;
//                for (DataSnapshot snapm: dataSnapshot.getChildren()){
//                    Double longtitude = snapm.child("(x)").getValue(Double.class);
//                    longArray.add(longtitude);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        //go through both arrays, and assign their values to the position of a proposed marker, then add the marker.
//        MarkerOptions loadedMarkers;
//    for (int i = 0;i<latArray.length;i++){
//        for(int j =0;j<longArray.length;j++){
//            lat = latArray[i];
//            longt = longArray[j];
//            mMap.addMarker(new MarkerOptions().position(lat,longt));
//        }
//        }

        //does not work when I try to parse the value of an individual (generated) ID (result of pushing).
        //there are guides to get this all working. could do with sorting out my structure so that it is paired Latitude, Longitude, in child
        //fields.. as this is more compliant with guides.

//        mLat = myRef.child("Latitude").child("LWEs6OloRzLSdZnQcND").toString();
//        double lLat = Double.parseDouble(mLat);
//        mLong = myRef.child("Longitude").child("LWEs6OoAdQ5GZ2YPnwR").toString();
//        double lLong = Double.parseDouble(mLong);
//
//        LatLng testPos = new LatLng(lLat,lLong);
//
//        mMap.addMarker(new MarkerOptions().position(testPos).title("test position"));

//-----------------------------------------------------------------------------------------------------------------------------
        //http://wptrafficanalyzer.in/blog/adding-marker-on-touched-location-of-google-maps-using-android-api-v2-with-supportmapfragment/

        //passing values of clicked areas on map to firebase (stored as push)
        //hope is that they can be retrieved when map reloaded.



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Latitude: " + latLng.latitude + " : " + "Longitude: " + latLng.longitude);
                myRef.child("Latitude").push().setValue(latLng.latitude);
                myRef.child("Longitude").push().setValue(latLng.longitude);

                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        double markerLat = marker.getPosition().latitude;
        double markerLong = marker.getPosition().longitude;

        if (marker.equals(this)) {

            Toast.makeText(this,
                    "Latitude: " + markerLat + " : " + "Longitude: " + markerLong,
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
