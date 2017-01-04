package com.reggieescobar.taigo.Helpers;

/**
 * Created by prodoxx on 28/12/16.
 */

public class Config {
    public final static String APPTAG = "TAIGO";
    public  enum MarkerPinType {
        PICKUP,
        DESTINATION
    }

    public final static String TRIPS_REF = "trips/";
    public final static String USER_TRIPS_REF = "userTrips/";
    public final static String TRIP_DRIVER_TRACKER_REF = "tripDriverTracking/";



    public final static int calculateFareEstimate(double distanceInMeters){
        int fare = 75;
        int rate = 20;

        double distanceInKm = distanceInMeters * 0.001;

        if(distanceInKm >= 1){
            fare += rate * distanceInKm;
        }


        return fare;
    }




}
