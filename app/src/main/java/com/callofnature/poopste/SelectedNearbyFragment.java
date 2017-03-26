package com.callofnature.poopste;


import android.*;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedNearbyFragment extends Fragment {


    public SelectedNearbyFragment() {
        // Required empty public constructor
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tab_numbers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle("Nearby Thrones");
        View rootView = inflater.inflate(R.layout.fragment_selected_nearby, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs_nearby_place);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_nearby);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return rootView;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Nearby_details_fragment details_fragment = new Nearby_details_fragment();
                    return details_fragment;
                case 1:
                    Nearby_review_fragment review_fragment = new Nearby_review_fragment();
                    return review_fragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            tab_numbers = 2;
            return tab_numbers;
        }

        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Reviews";
            }
            return null;
        }
    }

}
