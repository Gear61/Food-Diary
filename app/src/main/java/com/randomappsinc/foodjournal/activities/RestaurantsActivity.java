package com.randomappsinc.foodjournal.activities;

import android.os.Bundle;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.RestaurantsFragment;

public class RestaurantsActivity extends StandardActivity {

    public static final String PICKER_MODE_KEY = "pickerMode";
    public static final String ID_KEY = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants_activity);

        RestaurantsFragment restaurantsFragment = RestaurantsFragment.newInstance(true);
        getFragmentManager().beginTransaction().add(R.id.container, restaurantsFragment).commit();
    }
}
