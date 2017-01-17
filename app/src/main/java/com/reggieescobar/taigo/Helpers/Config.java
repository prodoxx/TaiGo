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

    public final static String APP_PREF_NAME ="_TAIGO_PREFS_";

    public final static String TRIPS_REF = "trips/";
    public final static String PASSENGER_TRIPS_REF = "passengerTrips/";
    public final static String TRIP_DRIVER_TRACKER_REF = "tripDriverTracking/";
    public final static String PASSENGERS_REF = "passengers/";

    public final static String PREF_UID = "_TAIGO_UID";

    public final static String DRIVERS_BROADCASTING_REF = "driversBroadcastingLocation/";
    public final static String TRIP_REQUEST_BROADCASTING_REF = "tripRequestBroadcastingLocation/";

    public final static double AREA_RADIUS = 10; //in km;

    public final static String STORAGE_PROFILE_IMAGE_PATH = "uploads/profile_images/";
    public final static String PROFILE_IMAGE_FILE_EXTENSION = ".png";



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
