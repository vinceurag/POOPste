package com.callofnature.poopste;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.callofnature.poopste.adapters.FeedAdapter;
import com.callofnature.poopste.helpers.NetworkConnection;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.Feed;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    //for posts
    private List<Feed> posts = new ArrayList<>();
    private RecyclerView recyclerView;
    private FeedAdapter fAdapter;
    JSONArray jsonPosts;
    Feed post;
    ProgressBar loadingCircle;
    SwipeRefreshLayout swipeRefreshLayout;

    SimpleDateFormat sdfDate;
    String posting_date;
    String strDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFeedFragment newInstance(String param1, String param2) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        prepareFeedData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle("News Feed");
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.accepted));

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        loadingCircle = (ProgressBar) rootView.findViewById(R.id.loading_news_feed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent(rootView);

            }
        });


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void clear() {
        int size = this.posts.size();
        this.posts.clear();
    }

    private void prepareFeedData() {
        posts.clear();
        PoopsteApi.getWithHeader("posts", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Success", "API call is successful");
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);
                    jsonPosts = obj.getJSONArray("data");
                    fAdapter = new FeedAdapter(posts);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(fAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < jsonPosts.length(); i++) {
                    try {
                        JSONObject objPost = jsonPosts.getJSONObject(i);
                        String dateStr = objPost.getString("date_created");
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
                        sdfDate = new SimpleDateFormat("MMM. dd, yyyy hh:mm aaa");//dd/MM/yyyy
                        posting_date = sdfDate.format(date);

                        post = new Feed(objPost.getString("fullname"), objPost.getString("status"), objPost.getString("profile_pic"), objPost.getString("photo"), posting_date);
                        posts.add(post);

                        Log.d("Status", strDate.getClass().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FAILED", "API call has failed");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshContent(final View rootView){
        if(NetworkConnection.isConnectedToNetwork(getActivity().getApplicationContext()))
        {
            prepareFeedData();
        }
        else
        {
            Snackbar mySnackbar = Snackbar
                    .make(rootView,"Not connected to a network.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            refreshContent(rootView);
                        }
                    });
            mySnackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
            mySnackbar.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
