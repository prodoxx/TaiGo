package com.reggieescobar.taigo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reggieescobar.taigo.Helpers.Config;
import com.reggieescobar.taigo.Helpers.TimeAgo;
import com.reggieescobar.taigo.MainActivity;
import com.reggieescobar.taigo.Models.MyTripsItemViewHolder;
import com.reggieescobar.taigo.Models.Trip;
import com.reggieescobar.taigo.R;
import com.reggieescobar.taigo.TripActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by prodoxx on 05/01/17.
 */

public class MyTripsRecyclerAdapter extends RecyclerView.Adapter<MyTripsItemViewHolder>{
    public ArrayList<Trip> data;
    private Context context;
    private StorageReference mStorageRef;


    public MyTripsRecyclerAdapter(Context _context, ArrayList<Trip> _data){
        this.data = _data;
        this.context = _context;
        mStorageRef = FirebaseStorage.getInstance().getReference().child(Config.STORAGE_PROFILE_IMAGE_PATH);
    }

    public MyTripsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_my_trip_item, parent, false);

        return new MyTripsItemViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public void onBindViewHolder(final MyTripsItemViewHolder holder, int position) {
        final Trip trip = data.get(position);

        try {
            holder.pickupText.setText(trip.pickUpInfo.address);
            holder.destinationText.setText(trip.destinationInfo.address);

            String timeAgo = TimeAgo.toDuration(System.currentTimeMillis() - trip.timeCreated);

            holder.time.setText(timeAgo);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, TripActivity.class);
                    i.putExtra("tripID", trip.tripID);

                    context.startActivity(i);
                }
            });

            String profileImageName  = trip.driverID  + Config.PROFILE_IMAGE_FILE_EXTENSION;


            StorageReference sRef = mStorageRef.child(profileImageName);


            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri).into(holder.avatarImage);
                }
            });


        }

        catch(Exception e){
            e.printStackTrace();
        }

    }

}
