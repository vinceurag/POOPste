package com.callofnature.poopste;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.callofnature.poopste.model.Model;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    UserSessionManager session;
    TextView name;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.getHeaderView(0);

        name = (TextView) navHeaderView.findViewById(R.id.fullname);
        profilePic = (ImageView) navHeaderView.findViewById(R.id.profilePic);
        name.setText(Model.getFullName());
        Picasso.with(this)
                .load(Model.getProfilePic())
                .fit()
                .centerCrop()
                .into(profilePic);
        Log.d("Name", Model.getFullName());

        Intent caller = getIntent();
        Bundle extras = caller.getExtras();
        if (extras != null) {
            if (extras.containsKey("userRegistered")) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.content_main),
                        "Thank you for registering! Have a nice day!", Snackbar.LENGTH_LONG);
                mySnackbar.show();
            } else if (extras.containsKey("postCreated")) {
                if(caller.getStringExtra("postCreated").equals("successful")) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.content_main),
                            "New status has been posted!", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.content_main),
                            "An error occured while posting your status. :(", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }else if (extras.containsKey("failed_post")) {

            }
        }

        session = new UserSessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NewPostActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.hold);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NewsFeedFragment newsFeed = new NewsFeedFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, newsFeed, "DEFAULT_FRAG");
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news_feed) {
            NewsFeedFragment newsFeed = new NewsFeedFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrame, newsFeed);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_nearby) {
            NearbyThronesFragment nearbyThrones = new NearbyThronesFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrame, nearbyThrones);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_leaderboard) {
            LeaderboardFragment leaderboard = new LeaderboardFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrame, leaderboard);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_report) {

        }else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle("");
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut() {
        Log.d("TAG", "Signout clicked");

        if(session.mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(session.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                            if(status.isSuccess()) {
                                Toast.makeText(getApplicationContext(),"Logged Out - with Google" ,Toast.LENGTH_LONG).show();
                                Model.setToken(null);
                                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(),"Failed to Logout" ,Toast.LENGTH_LONG).show();

                            }

                        }
                    });
            finish();
        } else {
            Toast.makeText(getApplicationContext(),"Logged Out - not google" ,Toast.LENGTH_LONG).show();
            Intent i=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
    }
}
