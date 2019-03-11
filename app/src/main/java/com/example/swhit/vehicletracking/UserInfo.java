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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo extends AppCompatActivity {



    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");



    EditText uname, uemail, ulatitude, ulongitude;
    Button submit_button;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        User user = new User();
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        uname = findViewById(R.id.txtName);
        uemail = findViewById(R.id.txtEmail);
        ulatitude = findViewById(R.id.txtLatitude);
        ulongitude = findViewById(R.id.txtLongitude);



        //need to figure out how to use Getter to keep this populated (with User class data?)



//        userName = myRef.child("users").child(user.id).child("name");

        //https://www.firebase.com/docs/java-api/javadoc/com/firebase/client/DataSnapshot.html#getValue(java.lang.Class)
        //https://stackoverflow.com/questions/37830692/parsing-from-datasnapshot-to-java-class-in-firebase-using-getvalue

        DatabaseReference currentUser = myRef.child("users").child(user.id);

        //https://stackoverflow.com/questions/38017765/retrieving-child-value-firebase
        currentUser.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //https://stackoverflow.com/questions/42519755/populate-textview-from-firebase-database
                //m/questions/45173499/retrieve-and-display-firebase-data-in-edittext-and-edit-content-save-again
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                //andy said to use long (double makes it do a nullpointer exception.. or can atleast!)????
                //what even is that https://stackoverflow.com/questions/2382058/unboxing-null-object-to-primitive-type-results-in-nullpointerexception-fine
                //im making them strings for the new User (...) thing
                //sure enough, setting the textfields with longs doesn't work, so we're doing Strings. (string.valueof(..long..))
                //these are longs.
                //https://stackoverflow.com/questions/1854924/how-to-convert-cast-long-to-string

                //does it matter if its long or string or what, should i keep them seperate; is this even related to what im asking
                //https://stackoverflow.com/questions/31930406/storing-long-type-in-firebase
                Double latitude = Double.valueOf(dataSnapshot.child("latitude").getValue(Long.class));
                Double longitude = Double.valueOf(dataSnapshot.child("longitude").getValue(Long.class));


                //https://stackoverflow.com/questions/10577610/what-is-the-difference-between-double-parsedoublestring-and-double-valueofstr?lq=1


                String sLatitude = String.valueOf(latitude);
                String sLongitude = String.valueOf(longitude);


                //should this matter for this stage? that they're strings not doubles/longs?
                //https://stackoverflow.com/questions/5769669/convert-string-to-double-in-java

                //these doubles declarations crash it when they're going from Longs and not Strings? - DOUBLE CHECK THAT?
                double dLatitude = Double.valueOf(latitude);
                double dLongitude = Double.valueOf(longitude);


                //maybe i should keep longs, doubles and strings... to avoid data accuracy loss?

                //why would they?
                //is this the same as setters
                //WHY DO I NEED A USER CLASS AT ALL? especially WHY DO I NEED GETTERS?
                //what does this mean https://stackoverflow.com/questions/50114944/why-we-need-an-empty-constructor-to-passing-save-a-data-from-firebase
                //I'm NOT EVEN FOLLOWING THE FIREBASE THING WHERE IT TALKS ABOUT CREATING A CLASS
                //instead i seem to be doing things like like (Long.class) when I
                // should be focusing on your User.class
                //https://firebase.google.com/docs/database/android/read-and-write
                // https://stackoverflow.com/questions/51423501/firebase-read-getter-setter



                User user = new User(name, email, latitude, longitude);

                System.out.println("Latitude = " + latitude);
                System.out.println("Longitude = " + longitude);

                System.out.println("Lat: " + user.getLatitude() + "Long: " + user.getLongitude());
                uname.setText(name);
                uemail.setText(email);

                //later being parsed in as doubles when you submit anyways, right? (on submit buttons listener)
                ulatitude.setText(sLatitude);
                ulongitude.setText(sLongitude);




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


       submit_button = findViewById(R.id.btnSubmit);

       submit_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String n = uname.getText().toString();
               String e = uemail.getText().toString();
               String la = ulatitude.getText().toString();
               String lo = ulongitude.getText().toString();

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
