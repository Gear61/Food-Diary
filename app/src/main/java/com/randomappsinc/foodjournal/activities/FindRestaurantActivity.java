package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.YelpSearchResultsAdapter;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.location.LocationManager;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class FindRestaurantActivity extends StandardActivity
        implements RestClient.RestaurantResultsHandler, LocationManager.Listener {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.search_input) EditText searchInput;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.restaurants) ListView restaurants;
    @BindView(R.id.loading) View loading;
    @BindView(R.id.no_results) View noResults;
    @BindView(R.id.set_location) FloatingActionButton setLocation;

    private RestClient restClient;
    private YelpSearchResultsAdapter adapter;
    @Nullable private String currentLocation;
    private boolean denialLock;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_restaurant);
        ButterKnife.bind(this);

        restClient = RestClient.getInstance();
        restClient.registerRestaurantResultsHandler(this);

        adapter = new YelpSearchResultsAdapter(this);
        restaurants.setAdapter(adapter);

        setLocation.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_map).colorRes(R.color.white));

        locationManager = new LocationManager(this, this);
        denialLock = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Run this here instead of onCreate() to cover the case where they return from turning on location
        if (currentLocation == null && !denialLock) {
            locationManager.fetchCurrentLocation();
        }
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        restaurants.setVisibility(View.GONE);
        noResults.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        if (currentLocation != null) {
            fetchRestaurants();
        }

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

    /** Fetches restaurants with the current location and search input */
    private void fetchRestaurants() {
        restClient.fetchRestaurants(searchInput.getText().toString(), currentLocation);
    }

    @Override
    public void processResults(List<Restaurant> results) {
        loading.setVisibility(View.GONE);
        if (results.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            adapter.setRestaurants(results);
            restaurants.setVisibility(View.VISIBLE);
        }
    }

    @OnItemClick(R.id.restaurants)
    public void onRestaurantClicked(int position) {
        Restaurant restaurant = adapter.getItem(position);
        if (!DatabaseManager.get().getRestaurantsDBManager().userAlreadyHasRestaurant(restaurant)) {
            DatabaseManager.get().getRestaurantsDBManager().addRestaurant(restaurant);
        }

        Intent returnRestaurant = new Intent();
        returnRestaurant.putExtra(Constants.RESTAURANT_KEY, restaurant);
        setResult(RESULT_OK, returnRestaurant);
        finish();
    }

    @OnClick(R.id.set_location)
    public void setLocation() {
        locationManager.showLocationForm();
    }

    @Override
    public void onServicesOrPermissionChoice() {
        denialLock = false;
    }

    @Override
    public void onLocationFetched(String location) {
        currentLocation = location;
        fetchRestaurants();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode != LocationManager.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        // No need to check if the location permission has been granted because of the onResume() block
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            denialLock = true;
            locationManager.showLocationPermissionDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != LocationManager.LOCATION_SERVICES_CODE) {
            return;
        }
        if (resultCode == RESULT_OK) {
            UIUtils.showLongToast(R.string.location_services_on);
            locationManager.fetchAutomaticLocation();
        } else {
            denialLock = true;
            locationManager.showLocationDenialDialog();
        }
    }

    @OnClick(R.id.back_button)
    public void goBack() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop listening for restaurant search results
        restClient.unregisterRestaurantResultsHandler(this);
        restClient.cancelRestaurantFetch();
    }
}
