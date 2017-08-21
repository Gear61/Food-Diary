package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.randomappsinc.foodjournal.API.RestClient;
import com.randomappsinc.foodjournal.Adapters.RestaurantSearchResultsAdapter;
import com.randomappsinc.foodjournal.Models.Restaurant;
import com.randomappsinc.foodjournal.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class FindRestaurantActivity extends StandardActivity implements RestClient.RestaurantResultsHandler {
    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.search_input) EditText searchInput;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.restaurants) ListView mRestaurants;

    private RestClient mRestClient;
    private RestaurantSearchResultsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_restaurant);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRestClient = RestClient.getInstance();
        mRestClient.registerRestaurantResultsHandler(this);

        mAdapter = new RestaurantSearchResultsAdapter(this);
        mRestaurants.setAdapter(mAdapter);
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

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        searchInput.setText("");
    }

    private void fetchRestaurants(String searchTerm) {
        mRestClient.fetchRestaurants(searchTerm, "Fremont, CA");
    }

    @Override
    public void processResults(List<Restaurant> results) {
        mAdapter.setRestaurants(results);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRestClient.unregisterRestaurantResultsHandler(this);
        mRestClient.cancelRestaurantFetch();
    }
}
