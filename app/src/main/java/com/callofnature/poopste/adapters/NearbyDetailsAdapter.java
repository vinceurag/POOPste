package com.callofnature.poopste.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.callofnature.poopste.MainActivity;
import com.callofnature.poopste.Nearby_details_fragment;
import com.callofnature.poopste.R;
import com.callofnature.poopste.model.NearbyDetails;

import java.util.List;

import static com.callofnature.poopste.R.id.ratingBar;

/**
 * Created by wholovesyellow on 3/26/2017.
 */

public class NearbyDetailsAdapter extends RecyclerView.Adapter<NearbyDetailsAdapter.MyViewHolder>{

    private List<NearbyDetails> nearbyDetailsList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name, distance;
        public RatingBar rating;
        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.nearby_name);
            distance = (TextView) view.findViewById(R.id.distance);
            rating = (RatingBar) view.findViewById(ratingBar);
            LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
            // Filled stars
            setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.white));
            // Half filled stars
            setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            // Empty stars
            setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));

//            ImageButton been_here_image = (ImageButton) view.findViewById(R.id.been_here);
//            been_here_image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    new AlertDialog.Builder(view.getContext())
//                            .setTitle("You've been here?")
//                            .setMessage("Are you sure you've pooped here?")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //action here
//                                }
//                            })
//                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            })
//                            .setIcon(R.drawable.details_name_logo)
//                            .show();
//                }
//            });
        }
    }

    private void setRatingStarColor(Drawable drawable, @ColorInt int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            DrawableCompat.setTint(drawable, color);
        }
        else
        {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public NearbyDetailsAdapter(List<NearbyDetails> nearbyDetailsList){
        this.nearbyDetailsList = nearbyDetailsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType ){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_details_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        NearbyDetails nearbyDetails = nearbyDetailsList.get(position);
        holder.name.setText(nearbyDetails.getLocation_name());
        holder.distance.setText(nearbyDetails.getDistance());
        holder.rating.setRating(nearbyDetails.getRating());
    }

    @Override
    public int getItemCount(){
        return nearbyDetailsList.size();
    }

    public void clear() {
        int size = this.nearbyDetailsList.size();
        this.nearbyDetailsList.clear();
        notifyItemRangeRemoved(0, size);
    }
}
