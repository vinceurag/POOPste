package com.callofnature.poopste.model;

/**
 * Created by vinceurag on 09/03/2017.
 */

public class Feed {

    private String name;
    private String status;
    private String profile_pic;
    private String photo_url;

    public Feed (String name, String status, String profile_pic, String photo_url) {
        this.name = name;
        this.status = status;
        this.profile_pic = profile_pic;
        this.photo_url = photo_url;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    private String post_date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

}
