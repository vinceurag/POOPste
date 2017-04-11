package com.callofnature.poopste.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.callofnature.poopste.R;
import com.callofnature.poopste.model.LeaderboardStudent;

import java.util.List;

/**
 * Created by wholovesyellow on 4/11/2017.
 */

public class LeaderboardStudentAdapter extends RecyclerView.Adapter<LeaderboardStudentAdapter.MyViewHolder> {

    private List<LeaderboardStudent> studentLeaderboardList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student_name, student_faculty, student_points, student_rank;

        public MyViewHolder(View view) {
            super(view);
            student_name = (TextView) view.findViewById(R.id.leaderboard_student_name);
            student_faculty = (TextView) view.findViewById(R.id.leaderboard_student_faculty);
            student_points = (TextView) view.findViewById(R.id.student_points);
            student_rank = (TextView) view.findViewById(R.id.student_rank);
        }
    }


    public LeaderboardStudentAdapter(List<LeaderboardStudent> studentLeaderboardList) {
        this.studentLeaderboardList = studentLeaderboardList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_student_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LeaderboardStudent student = studentLeaderboardList.get(position);
        holder.student_name.setText(student.getStudent_name());
        holder.student_faculty.setText(student.getStudent_faculty());
        holder.student_points.setText(student.getStudent_points());
        holder.student_rank.setText(student.getStudent_rank());
    }

    @Override
    public int getItemCount() {
        return studentLeaderboardList.size();
    }
}
