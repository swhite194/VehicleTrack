package com.example.swhit.vehicletracking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//https://www.youtube.com/watch?v=b_tz8kbFUsU
//used as basis in background, but supported by //https://stackoverflow.com/questions/49616900/firebaserecycleradapter-forcing-me-to-implement-methods-that-i-dont-want-need (basically same as official doc - https://github.com/firebase/FirebaseUI-Android/tree/master/database#using-the-firebaserecycleradapter "using the firebaserecycleradapter"))
////and //https://medium.com/android-grid/how-to-use-firebaserecycleradpater-with-latest-firebase-dependencies-in-android-aff7a33adb8b
public class AdminCustomerCompleteOrders extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://vehicletracking-899f3.firebaseio.com/");
    private DatabaseReference myRef = database.getReference("Location");

    private DatabaseReference currentOrderRef = myRef.child("orders").child("Current Orders");

    DatabaseReference customers = myRef.child("users").child("Customers");

    DatabaseReference completedOrders = myRef.child("orders").child("Completed Orders");

    private EditText searchCustId;
    private Button searchBtn;

    private RecyclerView resultList;

    String criteria;
    String custID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer_completed_orders);

        searchCustId = (EditText) findViewById(R.id.txtSearchCustId);
        searchBtn = (Button) findViewById(R.id.btnSearch);
        resultList = (RecyclerView) findViewById(R.id.resultList);
        //https://www.youtube.com/watch?v=b_tz8kbFUsU
        resultList.setHasFixedSize(true);


        resultList.setLayoutManager(new LinearLayoutManager(this));








        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseCurrentOrderSearch();
            }


        });


    }

    private void firebaseCurrentOrderSearch() {

        //spec changed slightly since 3.x+, use FirebaseRecyclerOptions now
        //https://stackoverflow.com/questions/49616900/firebaserecycleradapter-forcing-me-to-implement-methods-that-i-dont-want-need
//and //https://medium.com/android-grid/how-to-use-firebaserecycleradpater-with-latest-firebase-dependencies-in-android-aff7a33adb8b
        //after the first half of this being by https://www.youtube.com/watch?v=b_tz8kbFUsU (slightly outdated, only in cases like this, principle still applies)

        criteria = searchCustId.getText().toString();
        customers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(criteria)){
                        System.out.println("FOUND IT");
                        custID = ds.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getApplicationContext(), custID,  Toast.LENGTH_LONG).show();
        Query query = completedOrders.orderByChild("customerID").equalTo(custID);

        FirebaseRecyclerOptions<Order> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Order>().setQuery(query, Order.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Order, OrdersViewHolder>(firebaseRecyclerOptions) {


            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentorders_list_layout, parent, false);
                //https://stackoverflow.com/questions/49616900/firebaserecycleradapter-forcing-me-to-implement-methods-that-i-dont-want-need
                return new OrdersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, final int position, @NonNull Order model) {
//https://www.youtube.com/watch?v=b_tz8kbFUsU
                holder.setDetails(model.getId(), model.getCustomerID(), model.getDeliveryRequestedForDate(), model.getDeliveryRequestedForTime(), model.getDriverID(),
                        model.getItemID(), model.getItemQuantity());

                //https://www.youtube.com/watch?v=vlXZ287Sf9A
                //capturing the key of the record when pressed
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String edit_order_by_id = getRef(position).getKey();
                        Toast.makeText(getApplicationContext(), "Key: " + edit_order_by_id, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AdminCustomerCompleteOrders.this, CompleteOrders.class);
//                        extras.putString("userId", strUserId);
                        intent.putExtra("orderID", edit_order_by_id);

                        startActivity(intent);
                    }
                });
            }
        };
        //https://www.youtube.com/watch?v=b_tz8kbFUsU
        resultList.setAdapter(firebaseRecyclerAdapter);
        //https://stackoverflow.com/questions/53637770/android-firebase-recycler-adapter-is-not-querying-the-database
        firebaseRecyclerAdapter.startListening();

    }

    //View Holder class
    //https://www.youtube.com/watch?v=b_tz8kbFUsU
    public class OrdersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        //https://www.youtube.com/watch?v=b_tz8kbFUsU
        public void setDetails(String orderId, String customerId, String requestedDeliveryDate, String requestedDeliveryTime, String driverId, String itemId, int itemQuantity) {

            //these R.id.'s are from currentorders_list_layoutrs_list_layout.xml
            TextView order_id = (TextView) mView.findViewById(R.id.txtOrderId);
            TextView customer_id = (TextView) mView.findViewById(R.id.txtCustomerId);
            TextView requested_delivery_date = (TextView) mView.findViewById(R.id.txtRequestedDeliveryDate);
            TextView requested_delivery_time = (TextView) mView.findViewById(R.id.txtRequestedDeliveryTime);
            TextView driver_id = (TextView) mView.findViewById(R.id.txtDriverId);
            TextView item_id = (TextView) mView.findViewById(R.id.txtItemId);
            TextView item_quantity = (TextView) mView.findViewById(R.id.txtItemQuantity);


            order_id.setText(orderId);
            customer_id.setText(customerId);
            requested_delivery_date.setText(requestedDeliveryDate);
            requested_delivery_time.setText(requestedDeliveryTime);
            driver_id.setText(driverId);
            item_id.setText(itemId);
            //https://stackoverflow.com/questions/4105331/how-do-i-convert-from-int-to-string
            item_quantity.setText(String.valueOf(itemQuantity));
        }
    }

}
