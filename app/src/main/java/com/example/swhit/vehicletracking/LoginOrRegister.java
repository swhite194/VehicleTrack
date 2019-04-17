package com.example.swhit.vehicletracking;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.swhit.vehicletracking.services.LocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginOrRegister extends AppCompatActivity {
//all below:
    //https://medium.com/mobiletech/firebase-authentication-sample-371b5940ba93

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");

    DatabaseReference myRef = database.getReference("Location");

    EditText email, password;
    Button registerButton, loginButton;
    FirebaseAuth firebaseAuth;


    DatabaseReference currentUser = myRef.child("users");

//    //https://stackoverflow.com/questions/44583834/firebase-how-to-check-if-user-is-logged-in#
//    FirebaseAuth.AuthStateListener mAuthListener;

    CheckBox chkCustomer, chkDriver, chkAdmin;

    boolean checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        registerButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnLogin);

        chkCustomer = findViewById(R.id.checkCustomer);
        chkDriver = findViewById(R.id.checkDriver);
        chkAdmin = findViewById(R.id.checkAdmin);

        firebaseAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user != null){
//                    Intent intent = new Intent(LoginOrRegister.this, HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        };


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        loginButton.setVisibility(View.INVISIBLE);
//        registerButton.setVisibility(View.INVISIBLE);

        //https://stackoverflow.com/questions/20743124/setting-transparency-to-buttons-in-android


        loginButton.getBackground().setAlpha(10);
        registerButton.getBackground().setAlpha(10);
        loginButton.setTextColor(Color.GRAY);
        registerButton.setTextColor(Color.GRAY);

        //https://stackoverflow.com/questions/20824634/android-on-text-change-listener
        //https://stackoverflow.com/questions/41348813/hide-button-if-text-length-is-1-in-android-studio
        //https://stackoverflow.com/questions/43294173/android-studio-if-statement-with-and-operator (operators)


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(s.length() >= 10){
//                    loginButton.setVisibility(View.VISIBLE);
//                    registerButton.setVisibility(View.VISIBLE);
                    loginButton.getBackground().setAlpha(100);
                    registerButton.getBackground().setAlpha(100);
                    loginButton.setTextColor(Color.BLACK);
                    registerButton.setTextColor(Color.BLACK);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {

                                              @Override
                                              public void onClick(View v) {
                                                  //made this final for "writenewuser" but idk
                                                  final String e = email.getText().toString();
                                                  String p = password.getText().toString();

                                                  Intent intent = new Intent(LoginOrRegister.this, LocationService.class);
                                                  stopService(intent);

                                                  String name, latitude, longitude;

                                                  //this probably isn't needed.
//                                                  FirebaseAuth.getInstance().signOut();

                                                  if (TextUtils.isEmpty(e)) {
                                                      Toast.makeText(getApplicationContext(), "Please fill in your email address", Toast.LENGTH_LONG).show();
                                                      return;
                                                  }

                                                  if (TextUtils.isEmpty(p)) {
                                                      Toast.makeText(getApplicationContext(), "Please fill in your password", Toast.LENGTH_LONG).show();
                                                      return;
                                                  }

                                                  if (p.length() < 10) {
                                                      Toast.makeText(getApplicationContext(), "Please have a password of atleast 10 characters", Toast.LENGTH_LONG).show();
                                                      return;
                                                  }

                                                  firebaseAuth.createUserWithEmailAndPassword(e, p)
                                                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                              @Override
                                                              public void onComplete(@NonNull Task<AuthResult> task) {
                                                                  if (task.isSuccessful()) {
                                                                      //https://stackoverflow.com/questions/27423485/java-check-if-checkbox-is-checked
                                                                      if (chkDriver.isChecked()){
                                                                          checked = true;
                                                                          Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
                                                                          writeNewDriver(null, e, 0, 0, false, "available");

                                                                          Intent intent = new Intent(LoginOrRegister.this, UserInfo.class);
                                                                          startActivity(intent);
                                                                      }

                                                                      if (chkAdmin.isChecked()){
                                                                          checked = true;
                                                                          Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
                                                                          writeNewAdmin(null, e, 0, 0, "place", null, null);

                                                                          Intent intent = new Intent(LoginOrRegister.this, UserInfo.class);
                                                                          startActivity(intent);
                                                                      }

                                                                      if (chkCustomer.isChecked()) {
                                                                          checked = true;
                                                                          Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
//                                                                          writeNewCustomer(null, e, 0, 0, null);
                                                                          writeNewCustomer(null, e, 0, 0, "place", null, null);

                                                                          Intent intent = new Intent(LoginOrRegister.this, UserInfo.class);
                                                                          startActivity(intent);


//                                                                          return;
                                                                      }
                                                                      if(!checked){
                                                                          Toast.makeText(getApplicationContext(), "Please check a box!", Toast.LENGTH_LONG).show();
                                                                      }


                                                                  }
                                                //THIS IS COMING UP AT THE SAME TIME AS THE 10 CHARACTER VALIDATION... needs fixed; INVISIBLE-> VISIBLE login is a cop-out atm
                                                                  else {
                                                                      Toast.makeText(getApplicationContext(), "E-mail or password poor; please ensure first time registering with this email, the email format, and that your password is 10+ characters", Toast.LENGTH_LONG).show();
                                                                  }
                                                              }
                                                          });


                                              }
                                          });

        //hybrid of above ref and https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //https://stackoverflow.com/questions/16876538/android-stop-start-service-created-in-oncreate slightly different but yeah
                //find a better source because they're different.

                Intent intent = new Intent(LoginOrRegister.this, LocationService.class);
                stopService(intent);


//                currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child("Customers").hasChild(id)) {
////                            Customer aCustomer = dataSnapshot.child("Customers").child(id).getValue(Customer.class);
////                            if ((aCustomer.getName() == null) || (aCustomer.getEmail() == null) || (aCustomer.getAddress() == null) || (aCustomer.getPostcode() == null) || ){
////
////                        }
//                            if(dataSnapshot.child("Customers").child(id)
//                    }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                String e = email.getText().toString();
                String p = password.getText().toString();

                if (TextUtils.isEmpty(e)) {
                    Toast.makeText(getApplicationContext(), "Please fill in your email address", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(p)) {
                    Toast.makeText(getApplicationContext(), "Please fill in your password", Toast.LENGTH_LONG).show();
                }

                firebaseAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(LoginOrRegister.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cannot find account with this email/password", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
            }
        });


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


    }

//    private void writeNewUser(String name, String email, double latitude, double longitude) {
//        User user = new User(name, email, latitude, longitude);
//
//
//        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
//        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
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

    private void writeNewAdmin(String name, String email, double latitude, double longitude, String address, String city, String postcode) {
        //this shouldnt be here because its not really making use of it (atleast not the setter/getter)
        Admin admin = new Admin(name, email, latitude, longitude, address, city, postcode);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        admin.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //is this needed?
        myRef.child("users").child("Admins").child(admin.id).setValue(admin);


    }



//    private void writeNewAdmin(String name, String email, double latitude, double longitude, boolean isEnroute) {
//        Driver driver = new Driver(name, email, latitude, longitude, isEnroute);
//
//
//        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
//        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
//        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        //is this needed?
//        myRef.child("users").child("Drivers").child(user.id).setValue(user);
//
//
//    }


}
