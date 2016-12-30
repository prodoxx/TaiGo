package com.reggieescobar.taigo;

import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.reggieescobar.taigo.Helpers.Config;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    public MapView mapView;
    protected Location userLocation;
    private ImageView selectPointMarker;
    private TextView pinActionMessage;
    private FrameLayout fabBtnLayout;
    private TextView fabBtnText;
    private enum FabButtonStates  {
        PICKUP,
        DESTINATION,
        GO
    }

    private FabButtonStates fabButtonState;
    private FloatingActionButton goFabBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Declare Map Access Token
        MapboxAccountManager.start(this,getString(R.string.map_access_token));

        //Setting the Content View
        setContentView(R.layout.activity_main);


        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Drivers");


        //Setting up Mapbox MapView on Layout
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl(getString(R.string.map_style));

        //Init Map
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                try {

                    // Customize map with markers, polylines, etc.
                    mapboxMap.setMyLocationEnabled(true);
                    userLocation = mapboxMap.getMyLocation();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .zoom(16)
                            .target(new LatLng(userLocation))
                            .build();

                    mapboxMap.setCameraPosition(cameraPosition);


                }

                catch (Exception e){
                    Log.v(Config.APPTAG,e.getMessage());
                }
            }
        });






        //Navigation Drawer Settings
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        selectPointMarker = (ImageView)findViewById(R.id.select_point_marker);
        pinActionMessage = (TextView) findViewById(R.id.pin_action_message);
        fabBtnLayout = (FrameLayout)findViewById(R.id.fab_btn_layout);
        fabBtnText   = (TextView) findViewById(R.id.fab_btn_text);



        LinearLayout pickUpLayout = (LinearLayout) findViewById(R.id.pickup_layout);
        pickUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(Config.APPTAG, "PickUp Layout CLICKED!!!");
                changeMarkerAction(Config.MarkerPinType.PICKUP);



            }
        });


        LinearLayout destinationLayout = (LinearLayout) findViewById(R.id.destination_layout);
        destinationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(Config.APPTAG, "Destination Layout CLICKED!!");
                changeMarkerAction(Config.MarkerPinType.DESTINATION);

            }
        });


        goFabBtn = (FloatingActionButton) findViewById(R.id.go_fab_btn);
        goFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(Config.APPTAG, "Go Fab Btn CLICKED!!!");

                if(fabButtonState == FabButtonStates.PICKUP){
                    //set the pickup coordinates here

                } else if (fabButtonState == FabButtonStates.DESTINATION){
                    //set the GO text for the fab button
                    fabBtnText.setText(getResources().getString(R.string.go_text));
                    goFabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    pinActionMessage.setText(getResources().getString(R.string.book_a_ride_message));


                    //set the destination coordinates here




                } else if (fabButtonState == FabButtonStates.GO) {

                }


            }
        });






    }

    public void changeMarkerAction(Config.MarkerPinType pinType){
        if(pinType == Config.MarkerPinType.DESTINATION){
            fabButtonState = FabButtonStates.DESTINATION;
            selectPointMarker.setImageDrawable(getResources().getDrawable(R.drawable.primary_marker));
            pinActionMessage.setText(getResources().getString(R.string.choose_dropoff_message));
            fabBtnText.setText(getResources().getString(R.string.ok_text));
            goFabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_light)));
           // goFabBtn.setBackgroundColor(getResources().getColor(R.color.primary_light));


        } else if (pinType == Config.MarkerPinType.PICKUP){
            fabButtonState = FabButtonStates.PICKUP;
            selectPointMarker.setImageDrawable(getResources().getDrawable(R.drawable.secondary_marker));
            pinActionMessage.setText(getResources().getString(R.string.choose_pickup_message));
            fabBtnText.setText(getResources().getString(R.string.ok_text));

            goFabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            //goFabBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        } else {
            pinActionMessage.setText(getResources().getString(R.string.choose_pickup_message));
        }


        selectPointMarker.setVisibility(View.VISIBLE);
        fabBtnLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    @Override
    protected  void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected  void onSaveInstanceState(Bundle onStateOut){
        super.onSaveInstanceState(onStateOut);
        mapView.onSaveInstanceState(onStateOut);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }


}
