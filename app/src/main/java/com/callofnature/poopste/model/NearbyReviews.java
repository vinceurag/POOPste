package com.callofnature.poopste.model;

/**
 * Created by wholovesyellow on 3/26/2017.
 */

public class NearbyReviews {

    private String username;
    private String content;
    private String rating;

    public NearbyReviews(String username, String content, String rating){
        this.username = username;
        this.content = content;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

}
