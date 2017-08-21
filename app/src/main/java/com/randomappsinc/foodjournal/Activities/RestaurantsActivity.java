package com.randomappsinc.foodjournal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Models.Restaurant;
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
        startActivityForResult(new Intent(this, FindRestaurantActivity.class), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Restaurant restaurant = data.getParcelableExtra(FindRestaurantActivity.RESTAURANT_KEY);
        }
    }
}
