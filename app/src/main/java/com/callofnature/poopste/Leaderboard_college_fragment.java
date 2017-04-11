package com.callofnature.poopste;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.callofnature.poopste.adapters.LeaderboardCollegeAdapter;
import com.callofnature.poopste.adapters.NearbyAdapter;
import com.callofnature.poopste.model.LeaderboardCollege;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Leaderboard_college_fragment extends Fragment {

    View rootView;
    //for RecyclerView
    private List<LeaderboardCollege> collegeLeaderboardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LeaderboardCollegeAdapter lAdapter;

    public Leaderboard_college_fragment() {
        // Required empty public constructor
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tab_numbers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_leaderboard_college_fragment, container, false);

        //for recycler view
        lAdapter = new LeaderboardCollegeAdapter(collegeLeaderboardList);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_college);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(lAdapter);

        prepareCollegeData();

        return rootView;
    }


    private void prepareCollegeData() {
        LeaderboardCollege college = new LeaderboardCollege("Institute of Information and Computing Sciences", "100pts","1st");
        collegeLeaderboardList.add(college);

        college = new LeaderboardCollege("Faculty of Engineering", "90pts","2nd");
        collegeLeaderboardList.add(college);

        college = new LeaderboardCollege("College of Nursing", "80pts","3rd");
        collegeLeaderboardList.add(college);

        college = new LeaderboardCollege("Faculty of Arts and Letters", "50pts","4th");
        collegeLeaderboardList.add(college);

        lAdapter.notifyDataSetChanged();
    }


}
