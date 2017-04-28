package com.callofnature.poopste.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.callofnature.poopste.R;
import com.callofnature.poopste.model.Nearby;

import java.util.List;


/**
 * Created by wholovesyellow on 3/13/2017.
 */

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MyViewHolder>{

    private List<Nearby> nearbyList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name, distance;
        public RatingBar rating;

        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.nearby_name);
            distance = (TextView) view.findViewById(R.id.distance);
            rating = (RatingBar) view.findViewById(R.id.ratingBar);

            LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
            // Filled stars
            setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.colorAccent));
            // Half filled stars
            setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            // Empty stars
            setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
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

    public NearbyAdapter(List<Nearby> nearbyList){
        this.nearbyList = nearbyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType ){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Nearby nearby = nearbyList.get(position);
        holder.name.setText(nearby.getNearbyName());
        holder.distance.setText(nearby.getDistance());
        holder.rating.setRating(nearby.getRating());
        Log.e("onBindViewHolder", "onBindViewHolder called");
    }

    @Override
    public int getItemCount(){
        return nearbyList.size();
    }

    public void clear() {
        int size = this.nearbyList.size();
        this.nearbyList.clear();
        notifyItemRangeRemoved(0, size);
    }
}
