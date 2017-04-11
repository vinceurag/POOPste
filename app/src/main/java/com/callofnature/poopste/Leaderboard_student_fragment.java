package com.callofnature.poopste;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callofnature.poopste.adapters.LeaderboardStudentAdapter;
import com.callofnature.poopste.model.LeaderboardStudent;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Leaderboard_student_fragment extends Fragment {

    View rootView;
    //for RecyclerView
    private List<LeaderboardStudent> studentLeaderboardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LeaderboardStudentAdapter sAdapter;


    public Leaderboard_student_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_leaderboard_student_fragment, container, false);

        //for recycler view
        sAdapter = new LeaderboardStudentAdapter(studentLeaderboardList);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_student);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(sAdapter);

        prepareStudentData();

        return rootView;
    }


    private void prepareStudentData() {
        LeaderboardStudent student = new LeaderboardStudent("Vince Edgar Urag", "Institute of Information and Computing Sciences", "100pts", "1st");
        studentLeaderboardList.add(student);

        student = new LeaderboardStudent("Paul Patrick Lising", "Institute of Information and Computing Sciences", "90pts", "2nd");
        studentLeaderboardList.add(student);
        sAdapter.notifyDataSetChanged();
    }

}
