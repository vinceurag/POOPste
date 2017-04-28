package com.callofnature.poopste;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.callofnature.poopste.adapters.LeaderboardStudentAdapter;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.LeaderboardCollege;
import com.callofnature.poopste.model.LeaderboardStudent;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class Leaderboard_student_fragment extends Fragment {

    View rootView;
    //for RecyclerView
    private List<LeaderboardStudent> studentLeaderboardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LeaderboardStudentAdapter sAdapter;
    ProgressBar prog;


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
        prog = (ProgressBar) rootView.findViewById(R.id.loading_topC);

        recyclerView.setAdapter(sAdapter);

        prepareStudentData();

        return rootView;
    }


    private void prepareStudentData() {
        PoopsteApi.get("leaderboard/students", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray list = new JSONArray(response);
                    LeaderboardStudent student;
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject obj = list.getJSONObject(i);
                        student = new LeaderboardStudent(obj.getString("fullname"), obj.getString("college"), obj.getInt("points") + "pts", obj.getString("position"));
                        studentLeaderboardList.add(student);
                        sAdapter.notifyDataSetChanged();
                    }
                    prog.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Err", "error");
            }
        });
    }

}
