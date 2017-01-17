package com.reggieescobar.taigo.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.reggieescobar.taigo.Helpers.Config;
import com.reggieescobar.taigo.MainActivity;
import com.reggieescobar.taigo.R;
import com.reggieescobar.taigo.TripActivity;

/**
 * Created by prodoxx on 03/01/17.
 */

public class DriverFoundDialog extends DialogFragment {

    private MainActivity parentActivity;
    private String driverID;
    private String tripID;
    private Button viewTripButton;

    public DriverFoundDialog(){

    }


    public static DriverFoundDialog newInstance(String tripID, String driverID) {
        DriverFoundDialog frag = new DriverFoundDialog();
        Bundle args = new Bundle();
        args.putString("driverID",driverID );
        args.putString("tripID", tripID);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (MainActivity) getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_driver_found, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewTripButton  = (Button)view.findViewById(R.id.view_trip_btn);


        viewTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(parentActivity, TripActivity.class);
                i.putExtra("tripID", tripID);




                parentActivity.startActivity(i);
            }
        });




        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.driverID = getArguments().getString("driverID");
        this.tripID = getArguments().getString("tripID");

        Log.v(Config.APPTAG, "Driver ID: " + driverID);
        Log.v(Config.APPTAG, "Trip ID: " + tripID);



    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
