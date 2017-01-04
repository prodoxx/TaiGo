package com.reggieescobar.taigo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.geocoder.GeocoderCriteria;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.android.AndroidGeocoder;
import com.mapbox.geocoder.service.models.GeocoderResponse;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


import com.reggieescobar.taigo.Dialogs.DriverFoundDialog;
import com.reggieescobar.taigo.Helpers.AppResultListener;
import com.reggieescobar.taigo.Helpers.Config;
import com.reggieescobar.taigo.Helpers.FireBasePushIdGenerator;
import com.reggieescobar.taigo.Helpers.GetAddress;
import com.reggieescobar.taigo.Models.MyAddress;
import com.reggieescobar.taigo.Models.Trip;
import com.reggieescobar.taigo.Models.TripMarker;

import java.util.HashMap;


import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    private HashMap<Config.MarkerPinType, TripMarker> tripMarkers = new HashMap<>();
    private Polyline routeLine;

    private FabButtonStates fabButtonState;
    private FloatingActionButton goFabBtn;
    private TextView pickUpAddressText;
    private TextView destAddressText;
    private TextView tripFareEstimateText;
    private TextView tripDistanceText;

    private DirectionsRoute currentRoute;

    // Write a message to the database
    FirebaseDatabase database;



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


        database = FirebaseDatabase.getInstance();


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


        pickUpAddressText = (TextView) findViewById(R.id.pickup_text);
        destAddressText = (TextView) findViewById(R.id.dest_text);

        tripFareEstimateText = (TextView) findViewById(R.id.trip_fare_estimate);
        tripDistanceText = (TextView) findViewById(R.id.trip_distance_text);

        Button button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TripActivity.class);
                i.putExtra("tripID", "-K_cIWde0ysh7dfFVDXS");

                startActivity(i);
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



                    if(tripMarkers.get(Config.MarkerPinType.PICKUP) == null){

                        pickUpAddressText.setHint("Loading...");

                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(MapboxMap mapboxMap) {
                                CameraPosition cameraPosition = mapboxMap.getCameraPosition();

                                Log.v(Config.APPTAG, "LAT: " + cameraPosition.target.getLatitude() + " LNG: " + cameraPosition.target.getLongitude());

                                // Create an Icon object for the marker to use
                                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.grey_marker);
                                Icon icon = iconFactory.fromDrawable(iconDrawable);

                                Marker marker =  mapboxMap.addMarker(new MarkerOptions()
                                        .position(cameraPosition.target)
                                        .icon(icon)
                                        .title("Test!")
                                );


                                final TripMarker pickUpMarker = new TripMarker(marker,cameraPosition.target.getLatitude(), cameraPosition.target.getLongitude());




                                new GetAddress(MainActivity.this, cameraPosition.target.getLatitude(),cameraPosition.target.getLongitude(), new AppResultListener(){
                                    @Override
                                    public void onSuccess(String myAddress) {
                                      //  String myAddress = address.street + ", " + address.locality;
                                        pickUpMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.PICKUP, pickUpMarker);

                                        pickUpAddressText.setText(myAddress);

                                        Toast.makeText(MainActivity.this,"Pickup Selected.", Toast.LENGTH_SHORT).show();


                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(Exception error){
                                        //handle error eg. no internet.

                                        String myAddress = "N/A";
                                        pickUpMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.DESTINATION, pickUpMarker);


                                        pickUpAddressText.setText(myAddress);

                                        Toast.makeText(MainActivity.this,"Pickup Selected.", Toast.LENGTH_SHORT).show();



                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }



                                    }
                                }).execute();










                            }
                        });
                    } else {
                        //user already selected a pickup coordinate.

                        final TripMarker tripMarker = tripMarkers.get(Config.MarkerPinType.PICKUP);

                        pickUpAddressText.setHint("Loading...");

                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(MapboxMap mapboxMap) {
                                //lets delete the current pickup marker and add a new one somewhere else..
                                mapboxMap.removeMarker(tripMarker.getMarker());


                                CameraPosition cameraPosition = mapboxMap.getCameraPosition();

                                // Create an Icon object for the marker to use
                                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.grey_marker);
                                Icon icon = iconFactory.fromDrawable(iconDrawable);

                                Marker marker =  mapboxMap.addMarker(new MarkerOptions()
                                        .position(cameraPosition.target)
                                        .icon(icon)
                                        .title("Test!")
                                );


                                final TripMarker pickUpMarker = new TripMarker(marker,cameraPosition.target.getLatitude(), cameraPosition.target.getLongitude());

                              //  tripMarkers.put(Config.MarkerPinType.PICKUP, pickUpMarker);

                                new GetAddress(MainActivity.this, cameraPosition.target.getLatitude(),cameraPosition.target.getLongitude(), new AppResultListener(){
                                    @Override
                                    public void onSuccess(String myAddress) {
                                      //  String myAddress = address.street + ", " + address.locality;
                                        pickUpMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.PICKUP, pickUpMarker);

                                        pickUpAddressText.setText(myAddress);

                                        Toast.makeText(MainActivity.this,"Pickup Selected.", Toast.LENGTH_SHORT).show();



                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(Exception error){
                                        //handle error eg. no internet.

                                        String myAddress = "N/A";
                                        pickUpMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.DESTINATION, pickUpMarker);

                                        pickUpAddressText.setText(myAddress);

                                        Toast.makeText(MainActivity.this,"Pickup Selected.", Toast.LENGTH_SHORT).show();


                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }
                                }).execute();


                            }
                        });


                    }






                } else if (fabButtonState == FabButtonStates.DESTINATION){
                    //set the GO text for the fab button
                    fabBtnText.setText(getResources().getString(R.string.go_text));
                    fabButtonState = FabButtonStates.GO;

                    goFabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    pinActionMessage.setText(getResources().getString(R.string.book_a_ride_message));


                    //set the destination coordinates here


                    if(tripMarkers.get(Config.MarkerPinType.DESTINATION) == null){
                        destAddressText.setHint("Loading..");
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(MapboxMap mapboxMap) {
                                CameraPosition cameraPosition = mapboxMap.getCameraPosition();

                              //  Log.v(Config.APPTAG, "LAT: " + cameraPosition.target.getLatitude() + " LNG: " + cameraPosition.target.getLongitude());

                                // Create an Icon object for the marker to use
                                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.orange_marker);
                                Icon icon = iconFactory.fromDrawable(iconDrawable);

                                Marker marker =  mapboxMap.addMarker(new MarkerOptions()
                                        .position(cameraPosition.target)
                                        .icon(icon)
                                        .title("Test!")
                                );


                                final TripMarker destMarker = new TripMarker(marker,cameraPosition.target.getLatitude(), cameraPosition.target.getLongitude());






                                new GetAddress(MainActivity.this, cameraPosition.target.getLatitude(),cameraPosition.target.getLongitude(), new AppResultListener(){
                                    @Override
                                    public void onSuccess(String myAddress) {
                                       // String myAddress = address.street + ", " + address.locality;
                                        destMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.DESTINATION, destMarker);

                                        destAddressText.setText(myAddress);

                                        Toast.makeText(MainActivity.this,"Destination Selected.", Toast.LENGTH_SHORT).show();



                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(Exception error){
                                        //handle error eg. no internet.

                                        String myAddress = "N/A";
                                        destMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.DESTINATION, destMarker);

                                        Toast.makeText(MainActivity.this,"Destination Selected.", Toast.LENGTH_SHORT).show();


                                        destAddressText.setText(myAddress);

                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }
                                }).execute();




                            }
                        });
                    } else {
                        //user already selected a pickup coordinate.

                        final TripMarker tripMarker = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                        destAddressText.setHint("Loading..");

                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(MapboxMap mapboxMap) {
                                //lets delete the current pickup marker and add a new one somewhere else..
                                mapboxMap.removeMarker(tripMarker.getMarker());


                                CameraPosition cameraPosition = mapboxMap.getCameraPosition();

                                // Create an Icon object for the marker to use
                                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.orange_marker);
                                Icon icon = iconFactory.fromDrawable(iconDrawable);

                                Marker marker =  mapboxMap.addMarker(new MarkerOptions()
                                        .position(cameraPosition.target)
                                        .icon(icon)
                                        .title("Test!")
                                );


                               final TripMarker destMarker = new TripMarker(marker,cameraPosition.target.getLatitude(), cameraPosition.target.getLongitude());

                                tripMarkers.put(Config.MarkerPinType.DESTINATION, destMarker);




                                new GetAddress(MainActivity.this, cameraPosition.target.getLatitude(),cameraPosition.target.getLongitude(), new AppResultListener(){
                                    @Override
                                    public void onSuccess(String myAddress) {
                                       // String myAddress = address.street + ", " + address.locality;
                                        destMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.DESTINATION, destMarker);

                                        destAddressText.setText(myAddress);


                                        Toast.makeText(MainActivity.this,"Destination Selected.", Toast.LENGTH_SHORT).show();


                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(Exception error){
                                        //handle error eg. no internet.

                                        String myAddress = "N/A";
                                        destMarker.setAddress(myAddress);
                                        tripMarkers.put(Config.MarkerPinType.DESTINATION, destMarker);

                                        destAddressText.setText(myAddress);

                                        Toast.makeText(MainActivity.this,"Destination Selected.", Toast.LENGTH_SHORT).show();


                                        TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                                        TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);

                                        if(pickup != null
                                                && dest != null){

                                            Position origin = Position.fromCoordinates(pickup.getLng(), pickup.getLat() );
                                            Position destination = Position.fromCoordinates(dest.getLng(), dest.getLat());

                                            // Get route from API
                                            try {
                                                getRoute(origin, destination);
                                            } catch (ServicesException servicesException) {
                                                servicesException.printStackTrace();
                                            }
                                        }

                                    }
                                }).execute();




                            }
                        });


                    }




                } else if (fabButtonState == FabButtonStates.GO) {


                    TripMarker pickup = tripMarkers.get(Config.MarkerPinType.PICKUP);
                    TripMarker dest = tripMarkers.get(Config.MarkerPinType.DESTINATION);


                    final Trip myTrip = new Trip();
                    myTrip.pickUpInfo = new MyAddress();

                    myTrip.pickUpInfo.address = pickup.getAddress();
                    myTrip.pickUpInfo.lat = pickup.getLat();
                    myTrip.pickUpInfo.lng = pickup.getLng();

                    myTrip.destinationInfo = new MyAddress();
                    myTrip.destinationInfo.address = dest.getAddress();
                    myTrip.destinationInfo.lat = dest.getLat();
                    myTrip.destinationInfo.lng = dest.getLng();

                    myTrip.timeCreated = System.currentTimeMillis();
                    myTrip.distance = currentRoute.getDistance();
                    myTrip.estimatedDuration = currentRoute.getDuration();
                    myTrip.estimatedFareAmount = Config.calculateFareEstimate(currentRoute.getDistance());

                    myTrip.uid = "-ddd"; //TODO - Get this from authenticated user.
                    myTrip.driverID = "";
                    myTrip.completedTime = 0;
                    myTrip.timeAccepted = 0;
                    myTrip.pickUpTime = 0;

                    myTrip.tripID = FireBasePushIdGenerator.generatePushId();

                    myTrip.isDriverEndTrip = false;
                    myTrip.isDriverStartTrip = false;
                    myTrip.isPassengerEndTrip = false;
                    myTrip.isPassengerStartTrip = false;



                    final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                            "Finding a Driver...", true);


                    DatabaseReference tripRef = database.getReference(Config.TRIPS_REF + myTrip.tripID);
                    tripRef.setValue(myTrip, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                //no error
                                Toast.makeText(MainActivity.this,"Trip as been recorded", Toast.LENGTH_SHORT).show();

                                DatabaseReference userTrip = database.getReference(Config.USER_TRIPS_REF + "-ddd/" + myTrip.tripID); // TODO - Replace with real UID

                                HashMap<String, String> data = new HashMap<String, String>();
                                data.put("uid", "-ddd"); //TODO - Replace with real UID
                                data.put("tripID", myTrip.tripID);

                                userTrip.setValue(data);


                            } else {
                                //there was an error, handle it

                                dialog.hide();
                            }
                        }
                    });


                    DatabaseReference tripDriverSelected = database.getReference(Config.TRIPS_REF + myTrip.tripID);

                    tripDriverSelected.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            //Log.v(Config.APPTAG, "On ADD  - Key: " + dataSnapshot.getKey() + " " +  "Value: " + dataSnapshot.getValue());

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            //Log.v(Config.APPTAG, "On Change  - Key: " + dataSnapshot.getKey() + " " +  "Value: " + dataSnapshot.getValue());

                            if(dataSnapshot.getKey().equals("driverID")){
                                //show dialog and info about driver to start trip
                                dialog.hide();

                                String driverID = dataSnapshot.getValue().toString();


                                FragmentManager fm = getSupportFragmentManager();
                                DriverFoundDialog myDialog = DriverFoundDialog.newInstance(myTrip.tripID, driverID);
                                myDialog.setCancelable(false);
                                myDialog.show(fm, "driver_found_dialog");





                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            Log.v(Config.APPTAG, "On Move  - Key: " + dataSnapshot.getKey() + " " +  "Value: " + dataSnapshot.getValue());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });








                }


            }
        });






    }

    public void changeMarkerAction(Config.MarkerPinType pinType){
        if(pinType == Config.MarkerPinType.DESTINATION){
            fabButtonState = FabButtonStates.DESTINATION;
            selectPointMarker.setImageDrawable(getResources().getDrawable(R.drawable.orange_marker));
            pinActionMessage.setText(getResources().getString(R.string.choose_dropoff_message));
            fabBtnText.setText(getResources().getString(R.string.set_text));
            goFabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
           // goFabBtn.setBackgroundColor(getResources().getColor(R.color.primary_light));


        } else if (pinType == Config.MarkerPinType.PICKUP){
            fabButtonState = FabButtonStates.PICKUP;
            selectPointMarker.setImageDrawable(getResources().getDrawable(R.drawable.grey_marker));
            pinActionMessage.setText(getResources().getString(R.string.choose_pickup_message));
            fabBtnText.setText(getResources().getString(R.string.set_text));

            goFabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            //goFabBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        } else {
            pinActionMessage.setText(getResources().getString(R.string.choose_pickup_message));
        }


        selectPointMarker.setVisibility(View.VISIBLE);
        fabBtnLayout.setVisibility(View.VISIBLE);
    }



    private DirectionsRoute getRoute(Position origin, Position destination) throws ServicesException {

        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_DRIVING)
                .setAccessToken(getResources().getString(R.string.map_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(Config.APPTAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(Config.APPTAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(Config.APPTAG, "No routes found");
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(Config.APPTAG, "Distance: " + currentRoute.getDistance());


               /* Toast.makeText(
                        MainActivity.this,
                        "Route is " + currentRoute.getDistance() + " meters long.",
                        Toast.LENGTH_SHORT).show(); */

                // Draw the route on the map
                drawRoute(currentRoute);
                setTripInfoLabels(currentRoute);




            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(Config.APPTAG, "Error: " + throwable.getMessage());
               // Toast.makeText(.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return currentRoute;
    }

    private void setTripInfoLabels(DirectionsRoute route) {
        int fareEstimate = Config.calculateFareEstimate(route.getDistance());
        double distanceInKm =   Math.round((route.getDistance() * 0.001) * 100.0) / 100.0;
        tripDistanceText.setText(Double.toString(distanceInKm) + "km");
        tripFareEstimateText.setText(Integer.toString(fareEstimate) + "NTD");
    }

    private void drawRoute(DirectionsRoute route) {

       final DirectionsRoute myRoute = route;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                // Convert LineString coordinates into LatLng[]
                LineString lineString = LineString.fromPolyline(myRoute.getGeometry(), Constants.OSRM_PRECISION_V5);
                List<Position> coordinates = lineString.getCoordinates();
                LatLng[] points = new LatLng[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    points[i] = new LatLng(
                            coordinates.get(i).getLatitude(),
                            coordinates.get(i).getLongitude());
                }

                //remove the polyline if it already exists.
                if(routeLine != null){
                    mapboxMap.removePolyline(routeLine);
                    routeLine = null;
                }


                // Draw Points on MapView
                routeLine =  mapboxMap.addPolyline(new PolylineOptions()
                        .add(points)
                        .color(Color.parseColor("#009688"))
                        .width(5));



            }
        });


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
