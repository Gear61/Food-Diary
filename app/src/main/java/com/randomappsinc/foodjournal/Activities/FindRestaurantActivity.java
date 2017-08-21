package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.randomappsinc.foodjournal.API.RestClient;
import com.randomappsinc.foodjournal.Models.Restaurant;
import com.randomappsinc.foodjournal.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by alexanderchiou on 8/20/17.
 */

public class FindRestaurantActivity extends StandardActivity implements RestClient.RestaurantResultsHandler {
    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.clear_search) View clearSearch;

    private RestClient mRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_restaurant);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRestClient = RestClient.getInstance();
        mRestClient.registerRestaurantResultsHandler(this);
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        fetchRestaurants(input.toString());
        if (input.length() == 0) {
            clearSearch.setVisibility(View.GONE);
        } else {
            clearSearch.setVisibility(View.VISIBLE);
        }
    }

    private void fetchRestaurants(String searchTerm) {
        mRestClient.fetchRestaurants(searchTerm, "Fremont, CA");
    }

    @Override
    public void processResults(List<Restaurant> results) {

    }
}
