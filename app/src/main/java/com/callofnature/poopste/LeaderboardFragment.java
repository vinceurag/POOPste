package com.callofnature.poopste;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {


    public LeaderboardFragment() {
        // Required empty public constructor
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tab_numbers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle("Leaderboard");
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        // Inflate the layout for this fragment
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
                    Leaderboard_college_fragment college_fragment = new Leaderboard_college_fragment();
                    return college_fragment;
                case 1:
                    Leaderboard_student_fragment student_fragment = new Leaderboard_student_fragment();
                    return student_fragment;
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
                    return "College";
                case 1:
                    return "Student";
            }
            return null;
        }
    }

}
