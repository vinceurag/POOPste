package com.callofnature.poopste.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.callofnature.poopste.R;
import com.callofnature.poopste.model.NearbyReviews;

import java.util.List;

/**
 * Created by wholovesyellow on 3/26/2017.
 */

public class NearbyReviewsAdapter extends RecyclerView.Adapter<NearbyReviewsAdapter.MyViewHolder> {

    private List<NearbyReviews> reviewsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, content;
        public RatingBar rating;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.reviews_username);
            content = (TextView) view.findViewById(R.id.reviews_content);
            rating = (RatingBar) view.findViewById(R.id.reviews_ratingBar);
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


    public NearbyReviewsAdapter(List<NearbyReviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_reviews_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NearbyReviews reviews = reviewsList.get(position);
        holder.username.setText(reviews.getUsername());
        holder.content.setText(reviews.getContent());
        holder.rating.setRating(reviews.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }


}
