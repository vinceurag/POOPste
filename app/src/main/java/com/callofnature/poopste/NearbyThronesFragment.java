package com.callofnature.poopste;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.callofnature.poopste.adapters.NearbyAdapter;
import com.callofnature.poopste.helpers.NetworkConnection;
import com.callofnature.poopste.model.Nearby;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyThronesFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    View rootView;
    private static final int REQUEST_LOCATIONS = 7;
    private SlidingUpPanelLayout mLayout;

    //for RecyclerView
    private List<Nearby> nearbyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NearbyAdapter nAdapter;
    private ImageView icon;

    public NearbyThronesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity)getActivity()).setActionBarTitle("Nearby Thrones");
        rootView = inflater.inflate(R.layout.fragment_nearby_thrones, container, false);

        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(0.5f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        mayRequestLocation();


        //for recycler view
        nAdapter = new NearbyAdapter(nearbyList);
        icon = (ImageView) rootView.findViewById(R.id.arrowUp);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_nearby);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(nAdapter);
        prepareNearbyData();

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) { /** DO NOTHING **/ }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    icon.setImageResource(R.drawable.ic_down);
                } else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED || newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    icon.setImageResource(R.drawable.ic_up);
                }
            }
        });


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng pnoval = new LatLng(14.607724, 120.988953);
                googleMap.addMarker(new MarkerOptions().position(pnoval).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(pnoval).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(NetworkConnection.isConnectedToNetwork(getActivity().getApplicationContext()))
        {
            
        }
        else
        {
            Snackbar mySnackbar = Snackbar
                    .make(getView(),"Not connected to a network.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            NearbyThronesFragment nearbyThrones = new NearbyThronesFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.add(nearbyThrones, "Nearby Thrones")
                                    .replace(R.id.nearby_thrones, nearbyThrones)
                                    .commit();
                        }
                    });
            mySnackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
            mySnackbar.show();
        }
    }

    public boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (getActivity().checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) && shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(rootView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_LOCATIONS);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATIONS);
        }
        return false;
    }

    public void prepareNearbyData(){
        Nearby nearby = new Nearby("P. Noval", "0.5km", 1);
        nearbyList.add(nearby);

        nearby = new Nearby("3rd Floor Engineering", "10km", 4);
        nearbyList.add(nearby);

        nearby = new Nearby("Mcdonalds P. Noval", "5km", 3);
        nearbyList.add(nearby);

        nearby = new Nearby("P. Noval Church", "1km", 5);
        nearbyList.add(nearby);

        nearby = new Nearby("BGPOP", "1km", 5);
        nearbyList.add(nearby);

        nearby = new Nearby("Arki", "1km", 3);
        nearbyList.add(nearby);

        nearby = new Nearby("Commerce", "1km", 3);
        nearbyList.add(nearby);

        nearby = new Nearby("Educ", "1km", 4);
        nearbyList.add(nearby);

        nearby = new Nearby("Arts and Leters", "1km", 3);
        nearbyList.add(nearby);

        nearby = new Nearby("Nursing", "1km", 2);
        nearbyList.add(nearby);
    }

}
