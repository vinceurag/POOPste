package com.callofnature.poopste;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class NewReviewActivity extends AppCompatActivity {

    Boolean disableButtons = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
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

        }
        return super.onOptionsItemSelected(item);
    }
}
