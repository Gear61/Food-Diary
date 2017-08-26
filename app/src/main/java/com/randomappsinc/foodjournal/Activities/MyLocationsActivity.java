package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;

import com.randomappsinc.foodjournal.R;

import butterknife.ButterKnife;

public class MyLocationsActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_locations);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
