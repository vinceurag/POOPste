package com.callofnature.poopste;


import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.callofnature.poopste.adapters.NearbyAdapter;
import com.callofnature.poopste.adapters.NearbyDetailsAdapter;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.Nearby;
import com.callofnature.poopste.model.NearbyDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nearby_details_fragment extends Fragment implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public Nearby_details_fragment() {
        // Required empty public constructor
    }

    View rootView;
    MapView mMapView;
    private GoogleMap googleMap;
    private List<NearbyDetails> nearbyDetailsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NearbyDetailsAdapter nAdapter;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment((MainActivity)getActivity()).setActionBarTitle("Nearby Thrones");
        rootView = inflater.inflate(R.layout.fragment_nearby_details_fragment, container, false);

        //for recycler view
        nAdapter = new NearbyDetailsAdapter(nearbyDetailsList);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_details);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(nAdapter);

        prepareDetailsData();


        mMapView = (MapView) rootView.findViewById(R.id.mapView_details);
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
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                if (ActivityCompat.checkSelfPermission((Activity) getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, true);
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(bestProvider);
                locationManager.requestLocationUpdates(bestProvider, 300000, 0, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        prepareNearbyData(location.getLatitude(), location.getLongitude());
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(18).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });

            }
        });

        mMapView.onResume(); // needed to get the map to display immediately

        return rootView;
    }

    private void prepareDetailsData() {
        final Bundle args = getArguments();
        NearbyDetails nearby_info = new NearbyDetails(args.getString("nearby_name"), args.getString("nearby_distance"), args.getFloat("nearby_rating"));
        nearbyDetailsList.add(nearby_info);

        nAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ONRESUME", "onResume called");
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLastLocation.getLatitude();
//            mLastLocation.getLongitude();
//
//            Log.d("HOY", "Calling API...");
//            prepareNearbyData(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//
//            // For zooming automatically to the location of the marker
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(18).build();
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//            Log.e("MYLOC", "I am at " + mLastLocation.getLatitude());
//        }
//
//        nAdapter.notifyDataSetChanged();

        mMapView = (MapView) rootView.findViewById(R.id.mapView_details);

    }

    public void prepareNearbyData(double posLat, double posLong) {
        googleMap.clear();
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("lat", posLat);
            jsonParams.put("long", posLong);
            StringEntity entity = new StringEntity(jsonParams.toString());

            PoopsteApi.postWithHeader("thrones/", entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("Success", "API call is successful");
                    try {
                        String response = new String(responseBody, "UTF-8");
                        JSONArray jsonPosts = new JSONArray(response);

                        if (jsonPosts != null) {

                            for (int i = 0; i < jsonPosts.length(); i++) {
                                try {
                                    JSONObject objPost = jsonPosts.getJSONObject(i);

                                    LatLng markPos = new LatLng(Double.parseDouble(objPost.getString("latitude")), Double.parseDouble(objPost.getString("longitude")));
                                    MarkerOptions marker = new MarkerOptions().position(markPos).title(objPost.getString("place_name"));
                                    googleMap.addMarker(marker);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("FAILED", "API call has failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLastLocation.getLatitude();
            mLastLocation.getLongitude();

            Log.e("onConnected", "Latitude: " + mLastLocation.getLatitude());
            Log.e("onConnected", "Longitude: " + mLastLocation.getLongitude());
            prepareNearbyData(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(18).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Toast.makeText(getContext(),"ERROR: no last known location" ,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        prepareNearbyData(location.getLatitude(), location.getLongitude());
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(loc).title("I AM HERE").snippet("current_pos"));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
