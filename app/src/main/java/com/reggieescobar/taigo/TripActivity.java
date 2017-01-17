package com.reggieescobar.taigo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
import com.reggieescobar.taigo.Helpers.Config;
import com.reggieescobar.taigo.Helpers.TimeAgo;
import com.reggieescobar.taigo.Models.Trip;
import com.reggieescobar.taigo.Models.TripDriverTracker;
import com.reggieescobar.taigo.Models.TripMarker;
import com.reggieescobar.taigo.Models.TripStatus;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripActivity extends AppCompatActivity {

    private String tripID;
    public MapView mapView;
    protected Location userLocation;
    private Trip currentTrip;
    private TripDriverTracker driverCurrentPositionData;
    private Marker driverPositionMarker;
    private FirebaseDatabase database;
    private boolean dataCalledAlready = false;
    private Polyline routeLine;
    private DirectionsRoute currentRoute;
    private HashMap<Config.MarkerPinType, TripMarker> tripMarkers = new HashMap<>();

    private RelativeLayout mainStatusLayout;
    private TextView mainStatusText;
    private LinearLayout tripInfoLayout;
    private TextView tripStatusText;
    private TextView driverLicenseText;
    private TextView tripFareText;
    private TextView tripDurationText;

    private Button startTripButton;
    private Button endTripButton;


   // private Boolean dataCalledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);



        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);




        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trip");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();


        //Init some elements in the layout

        mainStatusLayout = (RelativeLayout) findViewById(R.id.main_status_layout);
        mainStatusText = (TextView) findViewById(R.id.main_status_text);
        tripInfoLayout = (LinearLayout) findViewById(R.id.trip_info_layout);
        tripStatusText = (TextView) findViewById(R.id.trip_status_text);
        driverLicenseText = (TextView) findViewById(R.id.driver_license_text);
        tripFareText = (TextView) findViewById(R.id.trip_fare_text);
        tripDurationText = (TextView) findViewById(R.id.trip_duration_text);

        startTripButton = (Button) findViewById(R.id.start_trip_button);
        endTripButton = (Button) findViewById(R.id.end_trip_button);



        try {
            Intent i = getIntent();

            if(i != null){
                tripID = i.getStringExtra("tripID");


                //TODO - Prepare Map Config

                //Setting up Mapbox MapView on Layout
                mapView = (MapView)findViewById(R.id.mapview);
                mapView.onCreate(savedInstanceState);
                mapView.setStyleUrl(getString(R.string.map_style));



                //Init Map
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final MapboxMap mapboxMap) {

                        try {

                            // Customize map with markers, polylines, etc.
                            mapboxMap.setMyLocationEnabled(true);
                            userLocation = mapboxMap.getMyLocation();



                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .zoom(13)
                                    .target(new LatLng(userLocation))
                                    .build();

                            mapboxMap.setCameraPosition(cameraPosition);



                            //TODO - Display and Track Driver

                            DatabaseReference driverTrackRef = database.getReference(Config.TRIP_DRIVER_TRACKER_REF + tripID);

                            driverTrackRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    driverCurrentPositionData = dataSnapshot.getValue(TripDriverTracker.class);
                                    LatLng dPosition = new LatLng(driverCurrentPositionData.lat, driverCurrentPositionData.lng);


                                   /* if(driverPositionMarker != null){
                                        mapboxMap.removeMarker(driverPositionMarker);
                                    } */

                                    if(driverPositionMarker != null){
                                        //move marker

                                        driverPositionMarker.setPosition(dPosition);


                                    } else {
                                        //create marker

                                        // Create an Icon object for the marker to use
                                        IconFactory iconFactory = IconFactory.getInstance(TripActivity.this);
                                        Drawable iconDrawable = ContextCompat.getDrawable(TripActivity.this, R.drawable.orange_driver_marker);
                                        Icon icon = iconFactory.fromDrawable(iconDrawable);



                                        driverPositionMarker =  mapboxMap.addMarker(new MarkerOptions()
                                                .position(dPosition)
                                                .icon(icon)
                                        );
                                    }





                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                        }

                        catch (Exception e){
                            Log.v(Config.APPTAG,e.getMessage());
                        }
                    }
                });





                //TODO - Get Trip Data from Database

                DatabaseReference tripRef = database.getReference(Config.TRIPS_REF + tripID);

                tripRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        currentTrip = dataSnapshot.getValue(Trip.class);




                        if(TripStatus.getTripStatusCode(TripActivity.this, currentTrip) == TripStatus.TripStatusCode.AWAITING_DRIVER_PICKUP
                            || TripStatus.getTripStatusCode(TripActivity.this, currentTrip) == TripStatus.TripStatusCode.DRIVER_CONFIRMED_PICKUP
                        ) {

                            mainStatusText.setText(TripStatus.getTripStatusText(TripActivity.this, currentTrip));
                            tripInfoLayout.setVisibility(View.GONE);
                            mainStatusLayout.setVisibility(View.VISIBLE); // show the main status layout



                            if(TripStatus.getTripStatusCode(TripActivity.this, currentTrip) == TripStatus.TripStatusCode.DRIVER_CONFIRMED_PICKUP){
                                startTripButton.setVisibility(View.VISIBLE);
                            }



                        } else {
                            mainStatusLayout.setVisibility(View.GONE); // show the main status layout
                            startTripButton.setVisibility(View.GONE);
                            tripInfoLayout.setVisibility(View.VISIBLE);

                            driverLicenseText.setText(currentTrip.driverID);
                            tripStatusText.setText(TripStatus.getTripStatusText(TripActivity.this, currentTrip));



                            if(TripStatus.getTripStatusCode(TripActivity.this, currentTrip) == TripStatus.TripStatusCode.DRIVER_CONFIRMED_DROUPOUT){
                                endTripButton.setVisibility(View.VISIBLE);

                                tripFareText.setText(Double.toString(currentTrip.fareAmount) + "NTD");
                                tripDurationText.setText(TimeAgo.toDuration(currentTrip.completedTime - currentTrip.pickUpTime));

                            } else if (TripStatus.getTripStatusCode(TripActivity.this, currentTrip) == TripStatus.TripStatusCode.TRIP_HAS_ENDED){
                                endTripButton.setVisibility(View.GONE);

                                tripFareText.setText(Double.toString(currentTrip.fareAmount) + "NTD");
                                tripDurationText.setText(TimeAgo.toDuration(currentTrip.completedTime - currentTrip.pickUpTime));
                            } else {
                                endTripButton.setVisibility(View.GONE);
                                startTripButton.setVisibility(View.GONE);
                            }


                        }



                        if (!dataCalledAlready) {
                            //this only happens once.

                            //Init Map
                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final MapboxMap mapboxMap) {

                                    try {

                                        Position origin = Position.fromCoordinates(currentTrip.pickUpInfo.lng, currentTrip.pickUpInfo.lat);
                                        Position dest = Position.fromCoordinates(currentTrip.destinationInfo.lng, currentTrip.destinationInfo.lat);


                                        //Create Pickup Marker

                                        IconFactory iconPickupFactory = IconFactory.getInstance(TripActivity.this);
                                        Drawable iconPickupDrawable = ContextCompat.getDrawable(TripActivity.this, R.drawable.grey_marker);
                                        Icon iconPickup = iconPickupFactory.fromDrawable(iconPickupDrawable);

                                        LatLng pickupPosition = new LatLng(origin.getLatitude(), origin.getLongitude());



                                        Marker pickupMarker =  mapboxMap.addMarker(new MarkerOptions()
                                                .position(pickupPosition)
                                                .icon(iconPickup)
                                                .title("Pickup!")
                                        );


                                        final TripMarker pickupTripMarker = new TripMarker(pickupMarker,origin.getLatitude(), origin.getLongitude());

                                        tripMarkers.put(Config.MarkerPinType.PICKUP, pickupTripMarker);




                                        //Create Destination Marker

                                        IconFactory iconFactory = IconFactory.getInstance(TripActivity.this);
                                        Drawable iconDrawable = ContextCompat.getDrawable(TripActivity.this, R.drawable.orange_marker);
                                        Icon icon = iconFactory.fromDrawable(iconDrawable);

                                        LatLng destPosition = new LatLng(dest.getLatitude(), dest.getLongitude());



                                        Marker destMarker =  mapboxMap.addMarker(new MarkerOptions()
                                                .position(destPosition)
                                                .icon(icon)
                                                .title("Destination!")
                                        );


                                        final TripMarker destTripMarker = new TripMarker(destMarker,origin.getLatitude(), origin.getLongitude());

                                        tripMarkers.put(Config.MarkerPinType.PICKUP, destTripMarker);


                                        getRoute(origin, dest);


                                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                .include(pickupPosition )
                                                .include(destPosition)
                                                .build();

                                        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 130), 5000);





                                    } catch (Exception e) {
                                        Log.v(Config.APPTAG, e.getMessage());
                                    }


                                    dataCalledAlready = true;

                                }
                            });


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


















            } else {
                //handle this error

            }

            final DatabaseReference tripRef = database.getReference(Config.TRIPS_REF + tripID);

            //we assume all the data has been gotten already.
            startTripButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog dialog = ProgressDialog.show(TripActivity.this, "",
                            "Starting Trip...", true);

                    HashMap<String,Object> startTripData = new HashMap<>();
                    startTripData.put("isPassengerStartTrip", true);

                    tripRef.updateChildren(startTripData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                // no error

                                dialog.hide();


                            } else {
                                // some error happened

                                Log.v(Config.APPTAG, databaseError.getMessage());
                            }
                        }
                    });



                }
            });

            //we assume all the data has been gotten already.
            endTripButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog dialog = ProgressDialog.show(TripActivity.this, "",
                            "Ending Trip...", true);

                    HashMap<String,Object> endTripData = new HashMap<>();
                    endTripData.put("isPassengerEndTrip", true);

                    tripRef.updateChildren(endTripData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                // no error

                                dialog.hide();


                            } else {
                                // some error happened

                                Log.v(Config.APPTAG, databaseError.getMessage());
                            }
                        }
                    });



                }
            });



        }

        //Catch Exception if no tripID was passed from the intent
        catch (Exception e){
            e.printStackTrace();
        }





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
             //   setTripInfoLabels(currentRoute);




            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(Config.APPTAG, "Error: " + throwable.getMessage());
                // Toast.makeText(.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return currentRoute;
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




}
