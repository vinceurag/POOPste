package com.callofnature.poopste.model;

import android.widget.RatingBar;

/**
 * Created by wholovesyellow on 3/13/2017.
 */

public class Nearby {

    private String nearbyName;
    private String distance;
    private float rating;

    public Nearby(String nearbyName, String distance, float rating){
        this.nearbyName = nearbyName;
        this.distance = distance;
        this.rating = rating;
    }

    public String getNearbyName() {
        return nearbyName;
    }

    public void setNearbyName(String nearbyName) {
        this.nearbyName = nearbyName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

}
