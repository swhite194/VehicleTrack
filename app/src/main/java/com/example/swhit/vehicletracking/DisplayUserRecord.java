package com.example.swhit.vehicletracking;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayUserRecord extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");

    TextView textEmail;

    //PURPOSE OF THIS - to pass a record from a firebase child directly into a class (as seen in datasnapshot.getvalue (user.class))
    //https://stackoverflow.com/questions/47144080/firebase-retrieve-and-assign-data-from-parent-and-children-to-class loosely followed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_record);

        User user = new User();
        //https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
        user.id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textEmail = findViewById(R.id.txtEmailBox);

        DatabaseReference currentUser = myRef.child("users").child(user.id);
//https://stackoverflow.com/questions/47144080/firebase-retrieve-and-assign-data-from-parent-and-children-to-class
        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);

                //this works, although is it okay that im calling user twice? when i want the same one? should it matter.
                textEmail.setText(user1.getId());
//                System.out.println(user1.getEmail());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
