package com.callofnature.poopste.model;

import android.widget.RatingBar;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wholovesyellow on 3/13/2017.
 */

public class Nearby {

    private String nearbyName;
    private String distance;
    private float rating;
    private int id;
    private LatLng loc;

    public Nearby(String nearbyName, String distance, float rating, int id, LatLng loc){
        this.nearbyName = nearbyName;
        this.distance = distance;
        this.rating = rating;
        this.id = id;
        this.loc = loc;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc() {
        this.loc = loc;
    }


}
