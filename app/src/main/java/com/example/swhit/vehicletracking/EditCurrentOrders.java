package com.example.swhit.vehicletracking;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class EditCurrentOrders extends AppCompatActivity {

    ListView listView;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    DatabaseReference myRef = database.getReference("Location");
//    DatabaseReference currentOrders = myRef.child("orders").child("Current Orders");



    FirebaseListOptions<Order> orders;
    FirebaseListAdapter<Order> firebaseListAdapter;
    Query query = myRef.child("orders").child("Current Orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_current_orders);

        listView = (ListView) findViewById(R.id.listViewCurrentOrders);

//        firebaseListAdapter = new FirebaseListAdapter(EditCurrentOrders.this, Order.class, android.R.layout.simple_list_item_1, currentOrders) {
//            @Override
//            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
//
//            }
//        };

        //https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-firebaseui-to-populate-a-listview
        orders = new FirebaseListOptions.Builder<Order>().setQuery(query, Order.class).build();

        firebaseListAdapter = new FirebaseListAdapter<Order>(orders) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Order model, int position) {

            }
        };


    }
}
