package com.reggieescobar.taigo.Models;

/**
 * Created by prodoxx on 03/01/17.
 */

public class Trip {

    public String tripID;
    public String uid;
    public MyAddress pickUpInfo;
    public MyAddress destinationInfo;
    public long timeCreated;
    public String driverID;
    public long timeAccepted;
    public long pickUpTime;
    public long completedTime;
    public int estimatedFareAmount;
    public int fareAmount;
    public double estimatedDuration;
    public double distance;
    public boolean isDriverStartTrip;
    public boolean isPassengerStartTrip;
    public boolean isDriverEndTrip;
    public boolean isPassengerEndTrip;


    public Trip(){

    }

}

