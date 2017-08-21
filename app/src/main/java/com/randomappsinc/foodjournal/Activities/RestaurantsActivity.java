package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 8/14/17.
 */

public class RestaurantsActivity extends StandardActivity {
    @BindView(R.id.add_restaurant) FloatingActionButton addRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addRestaurant.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add).colorRes(R.color.white));
    }

    @OnClick(R.id.add_restaurant)
    public void addRestaurant() {

    }
}
