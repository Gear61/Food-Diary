package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;

import com.randomappsinc.foodjournal.R;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 8/14/17.
 */

public class RestaurantsActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
