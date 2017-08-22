package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.search_input) EditText mSearchInput;
    @BindView(R.id.clear_search) View mClearSearch;
    @BindView(R.id.restaurants) ListView mRestaurants;
    @BindView(R.id.loading) View mLoading;
    @BindView(R.id.no_results) View mNoResults;

    private RestClient mRestClient;
    private RestaurantSearchResultsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_restaurant);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRestClient = RestClient.getInstance();
        mRestClient.registerRestaurantResultsHandler(this);

        mAdapter = new RestaurantSearchResultsAdapter(this);
        mRestaurants.setAdapter(mAdapter);
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        mRestaurants.setVisibility(View.GONE);
        mNoResults.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        fetchRestaurants(input.toString());
        if (input.length() == 0) {
            mClearSearch.setVisibility(View.GONE);
        } else {
            mClearSearch.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        mSearchInput.setText("");
    }

    private void fetchRestaurants(String searchTerm) {
        mRestClient.fetchRestaurants(searchTerm, "Fremont, CA");
    }

    @Override
    public void processResults(List<Restaurant> results) {
        mLoading.setVisibility(View.GONE);
        if (results.isEmpty()) {
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mAdapter.setRestaurants(results);
            mRestaurants.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRestClient.unregisterRestaurantResultsHandler(this);
        mRestClient.cancelRestaurantFetch();
    }
}
