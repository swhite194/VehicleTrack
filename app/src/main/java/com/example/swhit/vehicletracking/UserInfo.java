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

public class UserInfo extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");


    EditText uname, uemail, uaddress, ucity, upostcode, ulatitude, ulongitude, uEnroute, uBookable;
    EditText utest;
    Button submit_button;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        //        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase


        User user = new User();

//
//
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //why does it ask for these to be final?
        final Customer customer = new Customer();
        final Driver driver = new Driver();

        customer.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        driver.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        uname = findViewById(R.id.txtName);
        uemail = findViewById(R.id.txtEmail);
        uaddress = findViewById(R.id.txtAddress);
        ucity = findViewById(R.id.txtCity);
        upostcode = findViewById(R.id.txtPostcode);
        ulatitude = findViewById(R.id.txtLatitude);
        ulongitude = findViewById(R.id.txtLongitude);
        uEnroute = findViewById(R.id.txtEnroute);
        uBookable = findViewById(R.id.txtBookable);
        utest = findViewById(R.id.txtTest);

        submit_button = findViewById(R.id.btnSubmit);


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

        final DatabaseReference currentUser = myRef.child("users");
        final DatabaseReference currentCustomer = myRef.child("users").child("Customers").child(customer.id);
        final DatabaseReference currentDriver = myRef.child("users").child("Drivers").child(driver.id);


        //https://stackoverflow.com/questions/38017765/retrieving-child-value-firebase
        currentUser.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //https://stackoverflow.com/questions/42519755/populate-textview-from-firebase-database
                //m/questions/45173499/retrieve-and-display-firebase-data-in-edittext-and-edit-content-save-again

//whats the difference between getid and customer.id
                //https://stackoverflow.com/questions/37397205/google-firebase-check-if-child-exists
                //did i use that
                //https://stackoverflow.com/questions/40066901/check-if-child-exists-in-firebase
                //im scared of looking like a cheat

                if (dataSnapshot.child("Customers").hasChild(customer.id)) {
                    //is this fruitless? whats the point in transforming it into a class? i dont use it anywhere else.
                    Customer aCustomer = dataSnapshot.child("Customers").child(customer.id).getValue(Customer.class);


                    //shouldn't need to repeat this should I
//                    isDriver = false;

                    uname.setText(aCustomer.getName());
                    uemail.setText(aCustomer.getEmail());
                    uaddress.setText(aCustomer.getAddress());
                    ucity.setText(aCustomer.getCity());
                    upostcode.setText(aCustomer.getPostcode());
                    ulatitude.setText(String.valueOf(aCustomer.getLatitude()));
                    ulongitude.setText(String.valueOf(aCustomer.getLongitude()));

//                    utest.setText(getLocationFromAddress(context, aCustomer.getAddress()));

                    //a combination of: https://stackoverflow.com/questions/13698556/convert-street-address-to-coordinates-android
                    //and https://stackoverflow.com/questions/24352192/android-google-maps-add-marker-by-address (devgrg's answer)
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    String address = aCustomer.getAddress();
                    List<Address> fromLocationName = null;
                    //perhaps weirdly done because, considering this textbox is at the bottom, it makes it look like it automatically updates when you finish typing in the address; which isn't the case (it updates on submit)

                    //THIS SEEMS TO CAUSE USERINFO TO EITHER CRASH IF ITS FROM A COLD BOOT OR ONLY SHOW THE DETAILS FOR customers@gmail.com.. custpassword IF THEY'VE BEEN LOGGED IN PRIOR
                    try {
                        fromLocationName = geocoder.getFromLocationName(address, 1);
                        if(fromLocationName != null && fromLocationName.size()>0){
                            Address a = fromLocationName.get(0);
                            a.getLatitude();
                            a.getLongitude();

//                            p1 = new LatLng(a.getLatitude(), a.getLongitude());

                            String lat = String.valueOf(a.getLatitude());
                            String lon = String.valueOf(a.getLongitude());

                            ulatitude.setText(lat);
                            ulongitude.setText(lon);
//                            utest.setText(a.getLatitude() + "," + a.getLongitude());
//                            a = null;
//                            fromLocationName = null;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
                if (dataSnapshot.child("Drivers").hasChild(driver.id)) {

                    //did i kind of use this?
                    //https://stackoverflow.com/questions/45173499/retrieve-and-display-firebase-data-in-edittext-and-edit-content-save-again
                    Driver aDriver = dataSnapshot.child("Drivers").child(driver.id).getValue(Driver.class);

//                    isDriver = true;

                    uname.setText(aDriver.getName());
                    uemail.setText(aDriver.getEmail());
                    ulatitude.setText(String.valueOf(aDriver.getLatitude()));
                    ulongitude.setText(String.valueOf(aDriver.getLongitude()));
                    uEnroute.setText(String.valueOf(aDriver.isEnroute()));
                    //change this wording, availability isn't great
                    uBookable.setText(aDriver.getBookable());

                }

//                String name = dataSnapshot.child("name").getValue(String.class);
//                String email = dataSnapshot.child("email").getValue(String.class);
//                //andy said to use long (double makes it do a nullpointer exception.. or can atleast!)????
//                //what even is that https://stackoverflow.com/questions/2382058/unboxing-null-object-to-primitive-type-results-in-nullpointerexception-fine
//                //im making them strings for the new User (...) thing
//                //sure enough, setting the textfields with longs doesn't work, so we're doing Strings. (string.valueof(..long..))
//                //these are longs.
//                //https://stackoverflow.com/questions/1854924/how-to-convert-cast-long-to-string
//
//                //does it matter if its long or string or what, should i keep them seperate; is this even related to what im asking
//                //https://stackoverflow.com/questions/31930406/storing-long-type-in-firebase
//                Double latitude = Double.valueOf(dataSnapshot.child("latitude").getValue(Long.class));
//                Double longitude = Double.valueOf(dataSnapshot.child("longitude").getValue(Long.class));
//
//
//                //https://stackoverflow.com/questions/10577610/what-is-the-difference-between-double-parsedoublestring-and-double-valueofstr?lq=1
//
//
//                String sLatitude = String.valueOf(latitude);
//                String sLongitude = String.valueOf(longitude);
//
//
//                //should this matter for this stage? that they're strings not doubles/longs?
//                //https://stackoverflow.com/questions/5769669/convert-string-to-double-in-java
//
//                //these doubles declarations crash it when they're going from Longs and not Strings? - DOUBLE CHECK THAT?
//                double dLatitude = Double.valueOf(latitude);
//                double dLongitude = Double.valueOf(longitude);
//
//
//                //maybe i should keep longs, doubles and strings... to avoid data accuracy loss?
//
//                //why would they?
//                //is this the same as setters
//                //WHY DO I NEED A USER CLASS AT ALL? especially WHY DO I NEED GETTERS?
//                //what does this mean https://stackoverflow.com/questions/50114944/why-we-need-an-empty-constructor-to-passing-save-a-data-from-firebase
//                //I'm NOT EVEN FOLLOWING THE FIREBASE THING WHERE IT TALKS ABOUT CREATING A CLASS
//                //instead i seem to be doing things like like (Long.class) when I
//                // should be focusing on your User.class
//                //https://firebase.google.com/docs/database/android/read-and-write
//                // https://stackoverflow.com/questions/51423501/firebase-read-getter-setter
//
//
//
//                User user = new User(name, email, latitude, longitude);
//
//                System.out.println("Latitude = " + latitude);
//                System.out.println("Longitude = " + longitude);
//
//                System.out.println("Lat: " + user.getLatitude() + "Long: " + user.getLongitude());
//                uname.setText(name);
//                uemail.setText(email);
//
//                //later being parsed in as doubles when you submit anyways, right? (on submit buttons listener)
//                ulatitude.setText(sLatitude);
//                ulongitude.setText(sLongitude);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        user.setName(myRef.child("users").child(user.id).child("name").get


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //this works (but obviously needs condition statements)
//               writeNewDriver(n, e, doLa, doLo, boEnr);

                //if im continuing to use this... then https://firebase.google.com/docs/database/android/read-and-write amongst other sources.

                //THIS STUFF DOESNT WORK
                //https://stackoverflow.com/questions/37629346/can-i-get-value-without-using-event-listeners-in-firebase-on-android
                //this is still a really interesting point https://stackoverflow.com/questions/35929691/retrieving-arraylistobject-from-firebase-inner-class/35942782#35942782
                //because i was having issue changing data on button press but i think i was accidentally calling it customer and not driver.
                //also good, https://stackoverflow.com/questions/40184797/retrieve-data-from-firebase-database-on-button-click but he makes use of blah.Class a lot and i dont
                //annoys me that the first reference refers to something which wasnt my issue.

//IMPORTANT: SEE MY FACEBOOK NOTEPAD MSG FOR THE KIND OF THINGS THIS WAS CAUSING WHEN THE OTHER WAY ROUND.
//set this to SingleValue; ValueEventListener causes a bug where new accounts will be reverted to the same details as the account which you originally press Submit on
                //I WANT TO SAY THAT THIS WILL HAVE NO IMPACT ON A DRIVERS LOCATION WHEN IT COMES TO LOGGING INTO OTHER DRIVER ACCOUNTS OR WHENEVER THEY GO FROM STATIC TO MOVING?
                currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    String n = uname.getText().toString();
                    String e = uemail.getText().toString();
                    String ad = uaddress.getText().toString();
                    String ci = ucity.getText().toString();
                    String po = upostcode.getText().toString();
                    String la = ulatitude.getText().toString();
                    String lo = ulongitude.getText().toString();

                    String book = uBookable.getText().toString();

                    //should this be the case, its a boolean? will it work
                    String enr = uEnroute.getText().toString();
//
//                    String book = uBookable.getText().toString();

//                    //combination of address, city and postcode
//                    String completeAddress = ad + " " + ci + " " + po;

                    //https://stackoverflow.com/questions/5769669/convert-string-to-double-in-java
                    double doLa = Double.parseDouble(la);
                    double doLo = Double.parseDouble(lo);
                    boolean boEnr = Boolean.parseBoolean(enr);


                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Customers").hasChild(customer.id)) {
                            //does calling this method make up for the fact that i'm not calling those textfields directly into classes?
                            //could this all be made more efficient
                            //i feel bad about using classes if im barely using them and instead doing stuff like "to string" rather than class.

                            writeNewCustomer(n, e, doLa, doLo, ad, ci, po);


                        }
                        if (dataSnapshot.child("Drivers").hasChild(driver.id)) {
                            writeNewDriver(n, e, doLa, doLo, boEnr, book);
                            //i assume i include this; ive not started trying to edit drivers details yet..
                            //enr = null;
                            //book = null;
                            //
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

                //not making use of getters to plug those values in.. instead im parsing in the text fields? is that all i can do?

            }

        });
    }


    //https://firebase.google.com/docs/database/android/read-and-write
    //why does this need userID but the example of GoogleMapsActivity doesn't
//    private void writeNewUser(String name, String email, double latitude, double longitude) {
//        User user = new User(name, email, latitude, longitude);
//
//
//        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        //is this needed?
//        myRef.child("users").child(user.id).setValue(user);
//
//
//    }

    private void writeNewCustomer(String name, String email, double latitude, double longitude, String address, String city, String postcode) {
        //this shouldnt be here because its not really making use of it (atleast not the setter/getter)
        Customer customer = new Customer(name, email, latitude, longitude, address, city, postcode);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        customer.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //is this needed?
        myRef.child("users").child("Customers").child(customer.id).setValue(customer);



    }

    private void writeNewDriver(String name, String email, double latitude, double longitude, boolean isEnroute, String bookable) {

        //this shouldnt be here because its not really making use of it (atleast not setter/getter)
        Driver driver = new Driver(name, email, latitude, longitude, isEnroute, bookable);

        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        driver.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //is this needed?
        myRef.child("users").child("Drivers").child(driver.id).setValue(driver);


    }

    private void populateUserTextFields(String name, String email, double latitude, double longitude) {
        User user = new User(name, email, latitude, longitude);
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        user.setName(myRef.child("users").child(user.id).toString());
        uname.setText(user.getName());


    }


//    public LatLng getLocationFromAddress(Context context, String strAddress) {
//
//        Geocoder geocoder = new Geocoder(context);
//        List<Address> address;
//        LatLng p1 = null;
//        //needed to be in a try catch as suggested by android studio
//        try {
//            address = geocoder.getFromLocationName(strAddress, 5);
//            if(address==null)
//            {
//                return null;
//            }
//            Address location = address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude());
////            double longitude = addresses.get(0).getLongitude();
////            double latitude = addresses.get(0).getLatitude();
//
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return p1;
//
//    }
}
