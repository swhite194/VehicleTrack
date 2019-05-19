package com.example.swhit.vehicletracking;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchForUser extends AppCompatActivity {

    Button btnSearch;
    EditText txtUserId;


    String strUserId;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference currentUser = database.getReference("Location").child("users");
    DatabaseReference currentDriver = database.getReference("Location").child("users").child("Drivers");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_user);



        btnSearch = (Button) findViewById(R.id.btnSearch);
        txtUserId = (EditText) findViewById(R.id.txtUserId);




        //filling it out for testing, to save me typing
//        txtUserId.setText("gfPQ4NRb5hZtZH32027Z1PKgGE33");



//https://stackoverflow.com/questions/6290531/check-if-edittext-is-empty - MILAP's answer
        strUserId = txtUserId.getText().toString();
//https://stackoverflow.com/questions/6290531/check-if-edittext-is-empty - MILAP's answer

        txtUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strUserId = txtUserId.getText().toString();
                Toast.makeText(getApplicationContext(), "Text changed to " + strUserId, Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable s) {
                strUserId = txtUserId.getText().toString();
            }
        });

        //has to be pressed twice..
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://stackoverflow.com/questions/6290531/check-if-edittext-is-empty
                Toast.makeText(getApplicationContext(), strUserId, Toast.LENGTH_LONG).show();

                if(strUserId.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Text Field can't be empty!", Toast.LENGTH_LONG).show();
                }else{
//right now im testing with drivers only.
                    currentDriver.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(strUserId)){
                                //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
//                                Intent intent = new Intent(SearchForUser.this, AdminEditAnyUserInfo.class);
                                //https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
//https://stackoverflow.com/questions/6707900/pass-a-string-from-one-activity-to-another-activity-in-android
                                Intent intent = new Intent(SearchForUser.this, AdminEditAnyUserInfo.class);
//                        extras.putString("userId", strUserId);
                                intent.putExtra("userId", strUserId);

                                startActivity(intent);
//
//
//                                extras.putString("userId", strUserId);
//                                intent.putExtras(extras);
//
//                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "No user found!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });


    }
}
