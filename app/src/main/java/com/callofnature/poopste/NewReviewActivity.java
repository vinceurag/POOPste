package com.callofnature.poopste;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.callofnature.poopste.helpers.PoopsteApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewReviewActivity extends AppCompatActivity {

    Boolean disableButtons = false;
    RatingBar ratingBar;
    EditText reviewEditText;
    String review;
    int throne_id;
    float rating;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        TextView place = (TextView) findViewById(R.id.review_place);
        Intent i = getIntent();
        String throne_name = i.getStringExtra("throne_name");
        place.setText(throne_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // POST button has an id of 777
        final MenuItem menuItem = menu.add(Menu.NONE, 111, Menu.NONE, R.string.submit);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.findItem(111).setEnabled(!disableButtons);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if(id == 111) {
            ratingBar = (RatingBar) findViewById(R.id.newRating);
            reviewEditText = (EditText) findViewById(R.id.review);
            rating = ratingBar.getRating();
            review = reviewEditText.getText().toString();

            Intent i = getIntent();
            throne_id = i.getIntExtra("throne_id", 0);
            sendDataToApi();


        }
        return super.onOptionsItemSelected(item);
    }

    public void sendDataToApi() {
        progress = ProgressDialog.show(this, "Sending...",
                "Publishing your review...", true);

        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("throne_id", throne_id);
            jsonParams.put("rating", rating);
            jsonParams.put("comment", review);
            StringEntity entity = new StringEntity(jsonParams.toString());
            PoopsteApi.postWithHeader("reviews", entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Intent intent = new Intent(NewReviewActivity.this, MainActivity.class);
                    intent.putExtra("reviewCreated", "successful");
                    NewReviewActivity.this.startActivity(intent);
                    progress.dismiss();
                    finish();


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    Intent intent = new Intent(NewReviewActivity.this, MainActivity.class);
                    intent.putExtra("reviewCreated", "failed");
                    NewReviewActivity.this.startActivity(intent);
                    progress.dismiss();
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
