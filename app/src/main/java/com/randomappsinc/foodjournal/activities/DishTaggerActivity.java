package com.randomappsinc.foodjournal.activities;

import android.os.Bundle;

import com.randomappsinc.foodjournal.R;

import butterknife.ButterKnife;

public class DishTaggerActivity extends StandardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_view);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
