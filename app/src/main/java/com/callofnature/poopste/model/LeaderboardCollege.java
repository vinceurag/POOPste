package com.callofnature.poopste.model;

/**
 * Created by wholovesyellow on 4/10/2017.
 */

public class LeaderboardCollege {

    private String college_name;
    private String college_points;
    private String rank;

    public LeaderboardCollege(){

    }

    public LeaderboardCollege(String college_name, String college_points, String rank){
        this.college_name = college_name;
        this.college_points = college_points;
        this.rank = rank;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getCollege_points() {
        return college_points;
    }

    public void setCollege_points(String college_points) {
        this.college_points = college_points;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

}
