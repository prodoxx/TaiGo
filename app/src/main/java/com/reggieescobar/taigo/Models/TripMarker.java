package com.reggieescobar.taigo.Models;

import com.mapbox.mapboxsdk.annotations.Marker;

/**
 * Created by prodoxx on 03/01/17.
 */

public class TripMarker {

    private Marker _marker;
    private double _lat;
    private double _lng;
    private String _address;

    public TripMarker(Marker marker, double lat, double lng){
        this._lat = lat;
        this._lng = lng;
        this._marker = marker;
    }


    public void setMarker(Marker marker){
        this._marker = marker;
    }

    public void setCoordinates(long lat, long lng){
        this._lat = lat;
        this._lng = lng;
    }


    public void setAddress(String address){
        this._address = address;
    }

    public double  getLat(){
        return this._lat;
    }

    public double  getLng(){
        return this._lng;
    }

    public Marker getMarker(){
        return this._marker;
    }

    public String getAddress(){
        return this._address;
    }

}
