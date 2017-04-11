package com.callofnature.poopste.model;

/**
 * Created by wholovesyellow on 4/11/2017.
 */

public class LeaderboardStudent {

    private String student_name;
    private String student_faculty;
    private String student_points;
    private String student_rank;

    public LeaderboardStudent(String student_name, String student_faculty, String student_points, String student_rank){
        this.student_name = student_name;
        this.student_faculty = student_faculty;
        this.student_points = student_points;
        this.student_rank = student_rank;
    }


    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_faculty() {
        return student_faculty;
    }

    public void setStudent_faculty(String student_faculty) {
        this.student_faculty = student_faculty;
    }

    public String getStudent_points() {
        return student_points;
    }

    public void setStudent_points(String student_points) {
        this.student_points = student_points;
    }

    public String getStudent_rank() {
        return student_rank;
    }

    public void setStudent_rank(String student_rank) {
        this.student_rank = student_rank;
    }


}
