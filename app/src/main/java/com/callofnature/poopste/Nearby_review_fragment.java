package com.callofnature.poopste;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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
import android.widget.TextView;

import com.callofnature.poopste.adapters.NearbyDetailsAdapter;
import com.callofnature.poopste.adapters.NearbyReviewsAdapter;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.NearbyDetails;
import com.callofnature.poopste.model.NearbyReviews;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nearby_review_fragment extends Fragment {


    public Nearby_review_fragment() {
        // Required empty public constructor
    }

    private List<NearbyReviews> reviewsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NearbyReviewsAdapter rAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_nearby_review_fragment, container, false);

        //for recycler view
        rAdapter = new NearbyReviewsAdapter(reviewsList);

        final Bundle args = getArguments();
        final int throne_id = args.getInt("nearby_id");
        final String throne_name = args.getString("nearby_name");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_reviews);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(rAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_review);
        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), NewReviewActivity.class);
                i.putExtra("throne_id", throne_id);
                i.putExtra("throne_name", throne_name);
                Bundle bundle = ActivityOptions.makeCustomAnimation(getContext(), R.anim.pull_up_from_bottom, R.anim.hold).toBundle();
                startActivity(i, bundle);
            }
        });

        prepareReviewData(throne_id);

        return rootView;
    }

    private void prepareReviewData(int throne_id) {

        PoopsteApi.get("reviews/" + throne_id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonReviews = obj.getJSONArray("data");
                    for (int i = 0; i < jsonReviews.length(); i++) {
                        try {
                            JSONObject review = jsonReviews.getJSONObject(i);
//                            String dateStr = objPost.getString("date_created");
//                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
//                            sdfDate = new SimpleDateFormat("MMM. dd, yyyy hh:mm aaa");//dd/MM/yyyy
//                            posting_date = sdfDate.format(date);

                            NearbyReviews reviews = new NearbyReviews(review.getString("fullname"), review.getString("comment"), (float) review.getDouble("rating"));
                            reviewsList.add(reviews);

                            rAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }
}
