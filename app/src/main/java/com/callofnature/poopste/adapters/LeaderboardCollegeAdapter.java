package com.callofnature.poopste.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.callofnature.poopste.R;
import com.callofnature.poopste.model.LeaderboardCollege;

import java.util.List;

/**
 * Created by wholovesyellow on 4/10/2017.
 */

public class LeaderboardCollegeAdapter extends RecyclerView.Adapter<LeaderboardCollegeAdapter.MyViewHolder> {

    private List<LeaderboardCollege> collegeLeaderboardList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView college_name, college_points, rank;

        public MyViewHolder(View view) {
            super(view);
            college_name = (TextView) view.findViewById(R.id.leaderboard_college_faculty);
            college_points = (TextView) view.findViewById(R.id.college_points);
            rank = (TextView) view.findViewById(R.id.college_rank);
        }
    }


    public LeaderboardCollegeAdapter(List<LeaderboardCollege> collegeLeaderboardList) {
        this.collegeLeaderboardList = collegeLeaderboardList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_college_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LeaderboardCollege college = collegeLeaderboardList.get(position);
        holder.college_name.setText(college.getCollege_name());
        holder.college_points.setText(college.getCollege_points());
        holder.rank.setText(college.getRank());
    }

    @Override
    public int getItemCount() {
        return collegeLeaderboardList.size();
    }

}
