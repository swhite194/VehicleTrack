package com.example.swhit.vehicletracking;

import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //https://stackoverflow.com/questions/43545527/how-to-retrieve-data-from-firebase-to-google-map
    //making googlemap not private?
    private GoogleMap mMap;

//    public long latitude;
//    public long longitude;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");


    DatabaseReference myRef = database.getReference("Location");

    //dno if it makes a diff, but going by https://stackoverflow.com/questions/48528836/i-want-to-display-all-markers-of-the-locations-for-all-the-users-in-the-firebase?noredirect=1&lq=1
    DatabaseReference rootRef = database.getReference();
    DatabaseReference locationRef = rootRef.child("Location");
    DatabaseReference userRef = locationRef.child("users");
    DatabaseReference driversRef = locationRef.child("users").child("Drivers");

    DatabaseReference latRef = myRef.child("Latitude");
    DatabaseReference longRef = myRef.child("Longitude");

    DatabaseReference adminRef = userRef.child("Admins");

    //https://stackoverflow.com/questions/37886301/tag-has-private-access-in-android-support-v4-app-fragmentactivity
    private static final String TAG = "GoogleMapsActivity";

    Double[] latArray;
    Double[] longArray;

    double latitude;
    double longitude;


    Button btnTest;

    Map<String, Marker> markers = new HashMap();

    Customer cust;
    Driver drive;

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


//    customer.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    driver.id = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        btnTest = (Button) findViewById(R.id.btnTestForMap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //this is an admin view
        //took it from https://stackoverflow.com/questions/42466483/how-to-see-other-markers-in-google-map-moving-android-studio-google-maps
        //this helped too for things like calling in the lat/long  andhttps://stackoverflow.com/questions/55567149/change-marker-position-by-realtime-lat-lang-in-firebase-database-without-added
        //for consistency sake if you want to make them ValueEvent listeners like you've done everywhere else; you could use: ttps://stackoverflow.com/questions/48528836/i-want-to-display-all-markers-of-the-locations-for-all-the-users-in-the-firebase

        //this works for showing admins only.. if I change it to userRef.addchild it screws up a lil.


//userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.child("Admins").hasChild(id)) {
//
//            driversRef.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    //this is a good example of how classes do nothing in my project
//                    drive = dataSnapshot.getValue(Driver.class);
//
////                    latitude = dataSnapshot.child("latitude").getValue(Double.class);
////                    longitude = dataSnapshot.child("longitude").getValue(Double.class);
//
//                    latitude = drive.getLatitude();
//                    longitude = drive.getLongitude();
//
//                    LatLng location = new LatLng(latitude, longitude);
//
//                    Marker uMarker = mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.getKey()));
//                    markers.put(dataSnapshot.getKey(), uMarker);
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    drive = dataSnapshot.getValue(Driver.class);
//
//                    latitude = drive.getLatitude();
//                    longitude = drive.getLongitude();
//
//                    LatLng location = new LatLng(latitude, longitude);
//
//                    if (markers.containsKey(dataSnapshot.getKey())) {
//                        Marker marker = markers.get(dataSnapshot.getKey());
//                        marker.remove();
//                        //makes it show twice
////                    marker.setPosition(location);
//                    }
//                    Marker uMarker = mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.getKey()));
//                    markers.put(dataSnapshot.getKey(), uMarker);
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                    if (markers.containsKey(dataSnapshot.getKey())) {
//                        Marker marker = markers.get(dataSnapshot.getKey());
//                        marker.remove();
//                    }
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        } else{
//            Toast.makeText(getApplicationContext(), "Yeeterbix", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//});

        ///////////////////////////////////////////////////////////////////////////////////////////////////////

//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (dataSnapshot.child("Admins").hasChild(id)) {
//
//            driversRef.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    //this is a good example of how classes do nothing in my project
//                    drive = dataSnapshot.getValue(Driver.class);
//
////                    latitude = dataSnapshot.child("latitude").getValue(Double.class);
////                    longitude = dataSnapshot.child("longitude").getValue(Double.class);
//
//                    latitude = drive.getLatitude();
//                    longitude = drive.getLongitude();
//
//                    LatLng location = new LatLng(latitude, longitude);
//
//                    Marker uMarker = mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.getKey()));
//                    markers.put(dataSnapshot.getKey(), uMarker);
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    drive = dataSnapshot.getValue(Driver.class);
//
//                    latitude = drive.getLatitude();
//                    longitude = drive.getLongitude();
//
//                    LatLng location = new LatLng(latitude, longitude);
//
//                    if (markers.containsKey(dataSnapshot.getKey())) {
//                        Marker marker = markers.get(dataSnapshot.getKey());
//                        marker.remove();
//                        //makes it show twice
////                    marker.setPosition(location);
//                    }
//                    Marker uMarker = mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.getKey()));
//                    markers.put(dataSnapshot.getKey(), uMarker);
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                    if (markers.containsKey(dataSnapshot.getKey())) {
//                        Marker marker = markers.get(dataSnapshot.getKey());
//                        marker.remove();
//                    }
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        } else{
//            Toast.makeText(getApplicationContext(), "Yeeterbix", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//});

        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////THIS WORKS FOR SHOWING EVERYONE BUT ITS SUPER INEFFICIENT
        //ITD BE BEST IMPLEMENTING THIS INTO THE CHILD LISTENER



        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Admins").hasChild(id)) {

//https://stackoverflow.com/questions/46382925/android-firebase-getting-child-of-child-data
                    //and //took it from https://stackoverflow.com/questions/42466483/how-to-see-other-markers-in-google-map-moving-android-studio-google-maps
                    //        //this helped too for things like calling in the lat/long  andhttps://stackoverflow.com/questions/55567149/change-marker-position-by-realtime-lat-lang-in-firebase-database-without-added
                    //        //for consistency sake if you want to make them ValueEvent listeners like you've done everywhere else; you could use: ttps://stackoverflow.com/questions/48528836/i-want-to-display-all-markers-of-the-locations-for-all-the-users-in-the-firebase
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String userId = dataSnapshot.getKey();
//        DatabaseReference users= dataSnapshot.child(userId);
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                for (DataSnapshot dataS : ds.getChildren()) {
                                    latitude = dataS.child("latitude").getValue(Double.class);
                                    longitude = dataS.child("longitude").getValue(Double.class);

                                    LatLng location = new LatLng(latitude, longitude);
////

                                    Marker uMarker;

                                    //https://stackoverflow.com/questions/16598169/changing-colour-of-markers-google-map-v2-android
                                    System.out.println(ds.getKey());

//                                    if (ds.getKey() == "Admin"){
//
//                                    }


                                    //need to figure out how to to change them based on their ds... it says uMarker may not have been initialised at the bottom if i dont..

                                    uMarker = mMap.addMarker(new MarkerOptions().position(location).title(ds.getKey() + " " + dataS.getKey()));

                                    //removing the trail behind existing, moving markers.
                                    if (markers.containsKey(dataS.getKey())) {
                                        Marker marker = markers.get(dataS.getKey());
                                        marker.remove();
                                        //makes it show twice
//                    marker.setPosition(location);
                    }
                                        markers.put(dataS.getKey(), uMarker);
                                    }


                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError databaseError){

                            }
                        });
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }
            });


            /////////////////////////////////////////////////////////////////////////////////////////////

//userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String userId = dataSnapshot.getKey();
////        DatabaseReference users= dataSnapshot.child(userId);
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    latitude = drive.getLatitude();
//                    longitude = drive.getLongitude();
//
//                    LatLng location = new LatLng(latitude, longitude);
////
//                    Marker uMarker = mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.getKey()));
//                    markers.put(dataSnapshot.getKey(), uMarker);
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
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//});


            //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase

//        myRef.child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                //whats the difference between getid and customer.id
//                //https://stackoverflow.com/questions/37397205/google-firebase-check-if-child-exists
//                //did i use that
//                //https://stackoverflow.com/questions/40066901/check-if-child-exists-in-firebase
//                //im scared of looking like a cheat
//                if(dataSnapshot.child("Customers").hasChild(id)){
//                    cust = dataSnapshot.child("Customers").child(id).getValue(Customer.class);
//                    latitude = cust.getLatitude();
//                    longitude = cust.getLongitude();
//
//                    btnTest.setVisibility(View.INVISIBLE);
//
//                    LatLng markerLoc = new LatLng(latitude, longitude);
//                    mMap.addMarker(new MarkerOptions().position(markerLoc).title("customer"));
//                }
//                if(dataSnapshot.child("Drivers").hasChild(id)){
//                    drive = dataSnapshot.child("Drivers").child(id).getValue(Driver.class);
//                    latitude = drive.getLatitude();
//                    longitude = drive.getLongitude();
//
//                    LatLng markerLoc = new LatLng(latitude, longitude);
//                    mMap.addMarker(new MarkerOptions().position(markerLoc).title("drive"));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


            //https://stackoverflow.com/questions/48528836/i-want-to-display-all-markers-of-the-locations-for-all-the-users-in-the-firebase
            //kind of based off that, but mainly off my other usages of for ds:datanspshot.getchild
            //alongside the knowledge from above for the latter half
//        driversRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    //put in checks
//                    double lat = ds.child("latitude").getValue(Double.class);
//                    double lon = ds.child("longitude").getValue(Double.class);
//
//                    LatLng markLoc = new LatLng(lat, lon);
//                    mMap.addMarker(new MarkerOptions().position(markLoc).title(ds.getKey()));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        driversRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String key = dataSnapshot.getKey();
//
//                Double lat = dataSnapshot.child("latitude").getValue(Double.class);
//                Double lon = dataSnapshot.child("longitude").getValue(Double.class);
//
//                LatLng location = new LatLng(lat, lon);
//
//                Marker mark = mMap.addMarker(new MarkerOptions().position(location).title(key));
//                markers.put(dataSnapshot.getKey(), mark);
//
//                }
//
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (markers.containsValue(dataSnapshot.getKey())) {
//                    String key = dataSnapshot.getKey();
//                    Marker amark = markers.get(dataSnapshot.getKey());
//
//                    Double lat = dataSnapshot.child("latitude").getValue(Double.class);
//                    Double lon = dataSnapshot.child("longitude").getValue(Double.class);
//
//                    LatLng location = new LatLng(lat, lon);
//
//
//                    amark.remove();
//                    amark.setPosition(location);
//
////                    Marker mark = mMap.addMarker(new MarkerOptions().position(location).title(key));
////                    markers.put(dataSnapshot.getKey(), mark);
//                }
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            if (dataSnapshot.child("Customers").hasChild(customer.id)){
//                //does calling this method make up for the fact that i'm not calling those textfields directly into classes?
//                //could this all be made more efficient
//                //i feel bad about using classes if im barely using them and instead doing stuff like "to string" rather than class.
//                writeNewCustomer(n, e, doLa, doLo, ad);
//
//            }
//            if (dataSnapshot.child("Drivers").hasChild(driver.id)){
//                writeNewDriver(n, e, doLa, doLo, boEnr);
//            }
//        }


            //https://stackoverflow.com/questions/46351725/retrieve-map-coordinates-from-firebase-and-plot-on-google-maps
//        final double[] latitude = new double[0];
//        final double[] longitude = new double[0];
//
//        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Location");
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                latitude[0] = (double) dataSnapshot.child("Latitude").getValue();
//                longitude[0] = (double) dataSnapshot.child("Longitude").getValue();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        LatLng markerLoc = new LatLng(latitude[0], longitude[0]);
//        mMap.addMarker(new MarkerOptions().position(markerLoc).title("yeet"));


            //https://stackoverflow.com/questions/48528836/i-want-to-display-all-markers-of-the-locations-for-all-the-users-in-the-firebase?noredirect=1&lq=1
            //https://stackoverflow.com/questions/42540400/android-app-crashes-when-google-maps-connected-to-firebase TYPES?

//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    latitude = (long)ds.child("Latitude").getValue();
//                    longitude = (long)ds.child("Longitude").getValue();
//
////                    Log.d("TAG", latitude + " / " + longitude);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        locationRef.addListenerForSingleValueEvent(eventListener);
//        LatLng markerLoc = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(markerLoc).title("yeet"));


            //GOING OFF THE FIREBASE GUIDE FROM TOOLS (regarding event listener)
//        locationRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //GIVES THE HASHMAP ERROR IN LOGCAT
//                String latitude = dataSnapshot.getValue(String.class);
//                String longitude = dataSnapshot.getValue(String.class);
//                Double lLat = Double.parseDouble(latitude);
//                Double lLong = Double.parseDouble(longitude);
//        LatLng markerLoc = new LatLng(lLat, lLong);
//        mMap.addMarker(new MarkerOptions().position(markerLoc).title("yeet"));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        });


            //https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
            //COULD HELP with stuff idk.

        locationRef.addListenerForSingleValueEvent(new

            ValueEventListener() {
                @Override
                public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                    long count = dataSnapshot.getChildrenCount();
                    //THIS WORKS
                    Log.d("TAG", "CHILDREN COUNT IS: " + count);

                    //https://stackoverflow.com/questions/43545527/how-to-retrieve-data-from-firebase-to-google-map
                    //doesn't help at all https://stackoverflow.com/questions/38017765/retrieving-child-value-firebase
                    //https://stackoverflow.com/questions/47837229/getting-map-markers-from-firebase
//                    String latitude = dataSnapshot.child("Longitude").getValue(String.class);
//                    String longitude = dataSnapshot.child("Latitude").getValue(String.class);
                    //https://stackoverflow.com/questions/43216708/how-to-add-google-map-marker-from-firebase-database?noredirect=1&lq=1
                    //https://stackoverflow.com/questions/49766208/android-studio-unboxing-of-xxx-may-produce-java-lang-nullpointerexception (why i called them Int? so confusing!)
//https://stackoverflow.com/questions/49351853/how-to-fix-unboxing-may-produce-nullpointer-exception-with-firebase
//                    Double latitude = ds.child("Latitude").getValue(Double.class);
//                    Double longitude = ds.child("Longitude").getValue(Double.class);

//                    if(ds.hasChild("yeah"))
//                    {
//                        Log.d("TAG", "H E C K  Y E A H");
//                    }
//                    else{
//                        Log.d("TAG", "AHHHHHHHHHHHHHH");
//                    }


                    //https://stackoverflow.com/questions/35149670/how-to-set-firebase-getchildren-values-in-marker
                    //https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
                    //is this worth looking at


                    //https://stackoverflow.com/questions/37257166/android-firebase-why-does-ondatachange-returns-null-values
                    //i might want to make them store as hashmaps...

                    //this is saying NULL/NULL in logcat
//                    Log.d("TAG", latitude + " / " + longitude);
//
//
//
//                    if ((latitude != null) && (longitude != null)){
//                        LatLng markerLoc = new LatLng(latitude, longitude);
//                        mMap.addMarker(new MarkerOptions().position(markerLoc).title("yeet"));
//                }


//                String a = (String) dataSnapshot.getValue();
//                Log.d("TAG", a);
                    //log could only be recognised by doing "Import class" for it
                    //tag had to be made a constant
                    //valueeventlistener had a red underline oncancelled needed to be imported or something,
                    //but it was already here? so i deleted it and it should be the same layout as the stackoverflow example idk what diff was


                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){
                }
            });

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

            //https://stackoverflow.com/questions/43635994/retrieve-location-from-firebase-and-put-marker-on-google-map-api-for-android
            //(last bit of 1st (and only) answer)l
//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                LatLng newLocation = new LatLng(
//                        dataSnapshot.child("Latitude").getValue(Long.class),
//                        dataSnapshot.child("Longitude").getValue(Long.class)
//                );
//
//                mMap.addMarker(new MarkerOptions()
//                .position(newLocation)
//                .title(dataSnapshot.getKey()));
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

            //https://stackoverflow.com/questions/43216708/how-to-add-google-map-marker-from-firebase-database
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                double location_left = dataSnapshot.child("Longitude").getValue(Double.class);
//                double location_right = dataSnapshot.child("Latitude").getValue(Double.class);
//                LatLng sydney = new LatLng(location_left, location_right);
//                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()

            {
                @Override
                public void onMapClick (LatLng latLng){

                User user = new User();
                //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
                user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Latitude: " + latLng.latitude + " : " + "Longitude: " + latLng.longitude);
//                myRef.child("Latitude").push().setValue(latLng.latitude);
//                myRef.child("Longitude").push().setValue(latLng.longitude);
                user.setLatitude(latLng.latitude);
                user.setLongitude(latLng.longitude);
                myRef.child("Users").child(user.id).setValue(user);


                mMap.addMarker(markerOptions);
            }
            });
        }

        @Override
        public boolean onMarkerClick (Marker marker){

            double markerLat = marker.getPosition().latitude;
            double markerLong = marker.getPosition().longitude;

            if (marker.equals(this)) {

                Toast.makeText(this,
                        "Latitude: " + markerLat + " : " + "Longitude: " + markerLong,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

//    private void writeNewUser(String userId, String email, double latitude, double longitude){
//        User user = new User (email, latitude, longitude);
//        myRef.child("Users").child(userId).setValue(user);
//    }
    }


///yoo