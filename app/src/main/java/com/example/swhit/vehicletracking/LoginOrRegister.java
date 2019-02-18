package com.example.swhit.vehicletracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginOrRegister extends AppCompatActivity {
//all below:
    //https://medium.com/mobiletech/firebase-authentication-sample-371b5940ba93

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


        registerButton.setOnClickListener(new View.OnClickListener() {

                                              @Override
                                              public void onClick(View v) {
                                                  String e = email.getText().toString();
                                                  String p = password.getText().toString();

                                                  if (TextUtils.isEmpty(e)) {
                                                      Toast.makeText(getApplicationContext(), "Please fill in your email address", Toast.LENGTH_SHORT).show();
                                                      return;
                                                  }

                                                  if (TextUtils.isEmpty(p)) {
                                                      Toast.makeText(getApplicationContext(), "Please fill in your password", Toast.LENGTH_SHORT).show();
                                                  }

                                                  if (p.length() < 10) {
                                                      Toast.makeText(getApplicationContext(), "Please have a password of atleast 10 characters", Toast.LENGTH_SHORT).show();
                                                  }

                                                  firebaseAuth.createUserWithEmailAndPassword(e, p)
                                                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                              @Override
                                                              public void onComplete(@NonNull Task<AuthResult> task) {
                                                                  if (task.isSuccessful()) {
                                                                      startActivity(new Intent(getApplicationContext(), GoogleMapsActivity.class));
                                                                      finish();
                                                                  }
                                                                  else {
                                                                      Toast.makeText(getApplicationContext(), "E-mail or password is wrong", Toast.LENGTH_SHORT).show();
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
}
