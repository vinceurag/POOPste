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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.callofnature.poopste.adapters.LeaderboardCollegeAdapter;
import com.callofnature.poopste.adapters.NearbyAdapter;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.LeaderboardCollege;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


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
    ProgressBar prog;



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
        prog = (ProgressBar) rootView.findViewById(R.id.loading_topC);

        recyclerView.setAdapter(lAdapter);

        prepareCollegeData();

        return rootView;
    }


    private void prepareCollegeData() {



        PoopsteApi.get("leaderboard/colleges", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray list = new JSONArray(response);
                    LeaderboardCollege college;
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject obj = list.getJSONObject(i);
                        college = new LeaderboardCollege(obj.getString("college"), obj.getInt("SUM") + "pts", obj.getString("position"));
                        collegeLeaderboardList.add(college);
                        lAdapter.notifyDataSetChanged();
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
