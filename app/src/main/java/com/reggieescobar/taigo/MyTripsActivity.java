package com.reggieescobar.taigo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reggieescobar.taigo.Adapters.MyTripsRecyclerAdapter;
import com.reggieescobar.taigo.Helpers.Config;
import com.reggieescobar.taigo.Helpers.DividerItemDecoration;
import com.reggieescobar.taigo.Models.Trip;
import com.reggieescobar.taigo.Models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MyTripsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyTripsRecyclerAdapter myTripsRecyclerAdapter;
    ArrayList<Trip> myTrips = new ArrayList<>();

    private String uid; //TODO - Replace with real uID from authenticated user.
    private FirebaseDatabase database;
    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        uid = User.getInstance().getUid();

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Trips");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);

        myTripsRecyclerAdapter = new MyTripsRecyclerAdapter(MyTripsActivity.this, myTrips);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
       // recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(myTripsRecyclerAdapter);





        getAllTrips();

    }


    private void getAllTrips() {
        final DatabaseReference myTripsRef = database.getReference(Config.PASSENGER_TRIPS_REF + uid);

        loadingLayout.setVisibility(View.VISIBLE);

        myTripsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) postSnapshot.getValue();
                    String tripID = data.get("tripID").toString();




                    DatabaseReference tripRef = database.getReference(Config.TRIPS_REF + tripID);

                    tripRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Trip trip = dataSnapshot.getValue(Trip.class);

                            loadingLayout.setVisibility(View.GONE);

                            myTrips.add(trip);
                            myTripsRecyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }






            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
