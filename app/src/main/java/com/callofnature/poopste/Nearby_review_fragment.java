package com.callofnature.poopste;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callofnature.poopste.adapters.NearbyDetailsAdapter;
import com.callofnature.poopste.model.NearbyDetails;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nearby_review_fragment extends Fragment {


    public Nearby_review_fragment() {
        // Required empty public constructor
    }

    private List<NearbyDetails> nearbyDetailsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NearbyDetailsAdapter nAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_nearby_review_fragment, container, false);

        //for recycler view
        nAdapter = new NearbyDetailsAdapter(nearbyDetailsList);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_reviews);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(nAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_review);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), NewReviewActivity.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(getContext(), R.anim.pull_up_from_bottom, R.anim.hold).toBundle();
                startActivity(i, bundle);
            }
        });

        return rootView;
    }
}
