package com.callofnature.poopste;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;

import com.callofnature.poopste.helpers.RecyclerTouchListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.callofnature.poopste.helpers.NetworkConnection;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.Feed;
import com.callofnature.poopste.model.Nearby;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyThronesFragment extends Fragment implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Handler handler;

    public NearbyThronesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).setActionBarTitle("Nearby Thrones");
        rootView = inflater.inflate(R.layout.fragment_nearby_thrones, container, false);

        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(0.5f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        mayRequestLocation();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        //for recycler view
        nAdapter = new NearbyAdapter(nearbyList);
        icon = (ImageView) rootView.findViewById(R.id.arrowUp);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_nearby);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(nAdapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getContext(), SelectedThroneActivity.class);
                i.putExtra("nearby_id", nearbyList.get(position).getId());
                i.putExtra("nearby_loc", nearbyList.get(position).getLoc());
                i.putExtra("nearby_name", nearbyList.get(position).getNearbyName());
                i.putExtra("nearby_rating", nearbyList.get(position).getRating());
                i.putExtra("nearby_distance", nearbyList.get(position).getDistance());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) { /** DO NOTHING **/}

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    icon.setImageResource(R.drawable.ic_down);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED || newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    icon.setImageResource(R.drawable.ic_up);
                }
            }
        });





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

                if (ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                if (location != null) {
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(bestProvider, 300000, 0, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("HOY", "Calling API...");
                        prepareNearbyData(location.getLatitude(), location.getLongitude());
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(18).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        Log.e("MYLOC", "I am at " + location.getLatitude());
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

        mMapView = (MapView) rootView.findViewById(R.id.mapView);

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
        if(!(NetworkConnection.isConnectedToNetwork(getActivity().getApplicationContext())) && !(NetworkConnection.isLocationServiceEnabled(getActivity().getApplicationContext()))){
            Snackbar mySnackbar = Snackbar
                    .make(getView(), "Not connected to a network and no location services.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            NearbyThronesFragment nearbyThrones = new NearbyThronesFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.add(nearbyThrones, "Nearby Thrones")
                                    .replace(R.id.nearby_thrones, nearbyThrones)
                                    .commit();
                        }
                    });
            mySnackbar.setActionTextColor(getResources().getColor(R.color.white));
            mySnackbar.show();
        }else{
            if (NetworkConnection.isConnectedToNetwork(getActivity().getApplicationContext())) {

                if(NetworkConnection.isLocationServiceEnabled(getActivity().getApplicationContext())){

                }else{
                    Snackbar mySnackbar = Snackbar
                            .make(getView(), "No location service.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    NearbyThronesFragment nearbyThrones = new NearbyThronesFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.add(nearbyThrones, "Nearby Thrones")
                                            .replace(R.id.nearby_thrones, nearbyThrones)
                                            .commit();
                                }
                            });
                    mySnackbar.setActionTextColor(getResources().getColor(R.color.white));
                    mySnackbar.show();
                }
            } else {
                Snackbar mySnackbar = Snackbar
                        .make(getView(), "Not connected to a network.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NearbyThronesFragment nearbyThrones = new NearbyThronesFragment();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.add(nearbyThrones, "Nearby Thrones")
                                        .replace(R.id.nearby_thrones, nearbyThrones)
                                        .commit();
                            }
                        });
                mySnackbar.setActionTextColor(getResources().getColor(R.color.white));
                mySnackbar.show();
            }
        }

    }

    public boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //getActivity().checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&  was removed
        //1 location permission only according to stackoverflow
        if (getActivity().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        //shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) && was removed
        //1 location permission only according to stackoverflow
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(rootView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_LOCATIONS);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATIONS);
        }
        return false;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("onConnected", "onConnected called");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_nearby);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setAdapter(nAdapter);
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

            Log.e("MYLOC", "I am at " + mLastLocation.getLatitude());
            nAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(),"ERROR: no last known location" ,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("FAILED", "onConnectionFailed called");
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
                    nAdapter.clear();
                    Log.d("Success", "API call is successful");
                    try {
                        String response = new String(responseBody, "UTF-8");
                        JSONArray jsonPosts = new JSONArray(response);

                        if (jsonPosts != null) {

                            for (int i = 0; i < jsonPosts.length(); i++) {
                                try {
                                    JSONObject objPost = jsonPosts.getJSONObject(i);

                                    LatLng loc = new LatLng(objPost.getDouble("latitude"), objPost.getDouble("longitude"));

                                    final Nearby nearby = new Nearby(objPost.getString("place_name"), objPost.getString("distance") + "km", (float) objPost.getDouble("rating"), objPost.getInt("id"), loc);
                                    if(nearbyList.add(nearby)) {
                                        Log.e("added", "ADDED to list");
                                    } else {
                                        Log.e("notAdded", "not added to list");
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            nAdapter.notifyItemInserted(nearbyList.size());
                                        }
                                    });
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


}