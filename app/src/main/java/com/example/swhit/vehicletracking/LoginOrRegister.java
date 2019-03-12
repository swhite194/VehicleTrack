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
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginOrRegister extends AppCompatActivity {
//all below:
    //https://medium.com/mobiletech/firebase-authentication-sample-371b5940ba93

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");


    DatabaseReference myRef = database.getReference("Location");

    EditText email, password;
    Button registerButton, loginButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        registerButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();


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

                                                  String name, latitude, longitude;

                                                  if (TextUtils.isEmpty(e)) {
                                                      Toast.makeText(getApplicationContext(), "Please fill in your email address", Toast.LENGTH_LONG).show();
                                                      return;
                                                  }

                                                  if (TextUtils.isEmpty(p)) {
                                                      Toast.makeText(getApplicationContext(), "Please fill in your password", Toast.LENGTH_LONG).show();
                                                  }

                                                  if (p.length() < 10) {
                                                      Toast.makeText(getApplicationContext(), "Please have a password of atleast 10 characters", Toast.LENGTH_LONG).show();
                                                  }

                                                  firebaseAuth.createUserWithEmailAndPassword(e, p)
                                                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                              @Override
                                                              public void onComplete(@NonNull Task<AuthResult> task) {
                                                                  if (task.isSuccessful()) {
                                                                      Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
                                                                      writeNewUser(null, e, 0, 0);
                                                                      return;
                                                                  }
                                                //THIS IS COMING UP AT THE SAME TIME AS THE 10 CHARACTER VALIDATION... needs fixed; INVISIBLE-> VISIBLE login is a cop-out atm
                                                                  else {
                                                                      Toast.makeText(getApplicationContext(), "E-mail or password is wrong", Toast.LENGTH_LONG).show();
                                                                  }
                                                              }
                                                          });

                                              }
                                          });

        //hybrid of above ref and https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                            startActivity(new Intent(getApplicationContext(), GoogleMapsActivity.class));
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

    private void writeNewUser(String name, String email, double latitude, double longitude) {
        User user = new User(name, email, latitude, longitude);


        //im switching it up and making it like GoogleMap's activity layout in the clickonmap
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //is this needed?
        myRef.child("users").child(user.id).setValue(user);


    }

}
