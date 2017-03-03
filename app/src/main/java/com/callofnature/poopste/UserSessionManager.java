package com.callofnature.poopste;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by vinceurag on 03/03/2017.
 */

public class UserSessionManager implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    // Context
    private Context _context;

    // Google plus Client
    public GoogleApiClient mGoogleApiClient;

    // Constructor
    public UserSessionManager(Context context){
        this._context = context;

        // Google Sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(_context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("google base class", "onConnected invoked");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("google base class", "onConnectionSuspended invoked");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i("google base class", "onConnectionFailed invoked");

    }


}
