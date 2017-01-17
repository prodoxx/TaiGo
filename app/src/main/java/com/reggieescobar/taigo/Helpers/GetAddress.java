package com.reggieescobar.taigo.Helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import com.mapbox.geocoder.android.AndroidGeocoder;
import com.reggieescobar.taigo.MainActivity;
import com.reggieescobar.taigo.Models.MyAddress;
import com.reggieescobar.taigo.R;

/**
 * Created by rescobar on 03/12/2016.
 */
public class GetAddress extends AsyncTask<String, Integer, Boolean> {
    private Context context;
    private AppResultListener listener;
    private Exception error;
    private double mLat;
    private double mLng;
    private MyAddress address;

    private String myAddress;

    public GetAddress(Context context, double _lat, double _lng, AppResultListener listener){
        this.context = context;
        this.listener = listener;
        this.mLat = _lat;
        this.mLng = _lng;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try{
            return getAddress(this.mLat, this.mLng);
        }

        catch (Exception e){
            Toast.makeText(context,"Network Connection Error. Trying again...", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
            return false;
        }

    }

    private boolean getAddress(double lat, double lng){

        address = new MyAddress();


        try{

           // Geocoder g = new Geocoder(context);
            int max = 1;

            AndroidGeocoder geocoder = new AndroidGeocoder(context, Locale.getDefault());
            geocoder.setAccessToken(context.getResources().getString(R.string.map_access_token));

            List<Address> a = geocoder.getFromLocation(lat,lng,max);




          //  List<Address> a =  g.getFromLocation(lat,lng,max);

            if(a.size() > 0){

                Log.v(Config.APPTAG, a.get(0).toString());

              /*  address.country = a.get(0).getCountryName();
                address.countryCode = a.get(0).getCountryCode();
                address.locality =  a.get(0).getLocality();
                address.street = a.get(0).getFeatureName(); */

                myAddress = a.get(0).getAddressLine(0);


                return true;

            }else{
               /* address.country = "";
                address.countryCode = "";
                address.locality =  "";
                address.street = ""; */

                myAddress = "N/A";

                return false;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            this.error = e;
          /*  address.country = "";
            address.countryCode = "";
            address.locality =  "...";
            address.street = "..."; */

            myAddress = "N/A";



            return false;
        }

        //LocationData.getInstance().setAddress(address);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(aBoolean){
            listener.onSuccess(myAddress);
        }else{
           // Toast.makeText(context,"Failed to get your current address. Trying again..", Toast.LENGTH_SHORT).show();



            listener.onError(error);
        }
        super.onPostExecute(aBoolean);
    }
}