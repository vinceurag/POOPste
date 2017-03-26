package com.callofnature.poopste.model;

/**
 * Created by wholovesyellow on 3/26/2017.
 */

public class NearbyDetails {

    private String location_name;
    private String distance;
    private float rating;

    public NearbyDetails(String location_name, String distance, float rating){
        this.location_name = location_name;
        this.distance = distance;
        this.rating = rating;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
