package com.reggieescobar.taigo.Models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.reggieescobar.taigo.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by prodoxx on 05/01/17.
 */

public class MyTripsItemViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView avatarImage;
    public TextView pickupText;
    public TextView destinationText;
    public TextView time;


    public MyTripsItemViewHolder(View itemView){
        super(itemView);

        avatarImage = (CircleImageView) itemView.findViewById(R.id.avatar);
        pickupText = (TextView) itemView.findViewById(R.id.pickup_text);
        destinationText = (TextView) itemView.findViewById(R.id.destination_text);
        time = (TextView) itemView.findViewById(R.id.time);
    }
}
