package com.reggieescobar.taigo.Models;

import android.content.Context;

import com.reggieescobar.taigo.R;

/**
 * Created by prodoxx on 04/01/17.
 */

public class TripStatus {
    public enum TripStatusCode {
        AWAITING_DRIVER_PICKUP,
        DRIVER_CONFIRMED_PICKUP,
        DRIVER_CONFIRMED_DROUPOUT,
        TRIP_IN_PROGRESS,
        TRIP_HAS_ENDED
    }

    public TripStatus() {

    }


    public static String getTripStatusText(Context context, Trip trip){

        if(trip.isDriverStartTrip){
            if(trip.isPassengerStartTrip){
                if(trip.isDriverEndTrip){
                    if(trip.isPassengerEndTrip){
                        return context.getResources().getString(R.string.trip_has_ended);
                    } else {
                        return context.getResources().getString(R.string.driver_confirm_dropout_text);
                    }
                } else {
                    return context.getResources().getString(R.string.trip_in_progress_text);
                }
            } else {
                return context.getResources().getString(R.string.driver_confirm_pickup_text);
            }
        } else {
            return context.getResources().getString(R.string.awaiting_pickup_text);
        }


    }


    public static TripStatusCode getTripStatusCode(Context context, Trip trip){

        if(trip.isDriverStartTrip){
            if(trip.isPassengerStartTrip){
                if(trip.isDriverEndTrip){
                    if(trip.isPassengerEndTrip){
                        return TripStatusCode.TRIP_HAS_ENDED;
                    } else {
                        return TripStatusCode.DRIVER_CONFIRMED_DROUPOUT;
                    }
                } else {
                    return TripStatusCode.TRIP_IN_PROGRESS;
                }
            } else {
                return TripStatusCode.DRIVER_CONFIRMED_PICKUP;
            }
        } else {
            return TripStatusCode.AWAITING_DRIVER_PICKUP;
        }


    }

}
