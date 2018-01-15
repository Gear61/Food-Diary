package com.randomappsinc.foodjournal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.YelpSearchResultsAdapter;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.LocationFetcher;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.LocationForm;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class FindRestaurantActivity extends StandardActivity implements RestClient.RestaurantResultsHandler {

    private static final int LOCATION_SERVICES_CODE = 1;

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.search_input) EditText mSearchInput;
    @BindView(R.id.clear_search) View mClearSearch;
    @BindView(R.id.restaurants) ListView mRestaurants;
    @BindView(R.id.loading) View mLoading;
    @BindView(R.id.no_results) View mNoResults;
    @BindView(R.id.set_location) FloatingActionButton mSetLocation;

    private final LocationForm.Listener mLocationFormListener = new LocationForm.Listener() {
        @Override
        public void onLocationEntered(String location) {
            // Cancel current location fetch when location is manually entered
            mLocationFetcher.stop();
            stopFetchingCurrentLocation();

            mCurrentLocation = location;
            fetchRestaurants();
        }
    };

    private RestClient mRestClient;
    private YelpSearchResultsAdapter mAdapter;
    private boolean mLocationFetched;
    private Handler mLocationChecker;
    private Runnable mLocationCheckTask;
    private LocationFetcher mLocationFetcher;
    @Nullable private String mCurrentLocation;
    private boolean mDenialLock;
    private MaterialDialog mLocationDenialDialog;
    private MaterialDialog mLocationPermissionDialog;
    private LocationForm mLocationForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_restaurant);
        ButterKnife.bind(this);

        mRestClient = RestClient.getInstance();
        mRestClient.registerRestaurantResultsHandler(this);

        mAdapter = new YelpSearchResultsAdapter(this);
        mRestaurants.setAdapter(mAdapter);

        mSetLocation.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_map).colorRes(R.color.white));

        mDenialLock = false;
        mLocationFetcher = new LocationFetcher(this);
        mLocationChecker = new Handler();
        mLocationCheckTask = new Runnable() {
            @Override
            public void run() {
                SmartLocation.with(getBaseContext()).location().stop();
                if (!mLocationFetched) {
                    UIUtils.showSnackbar(mParent, getString(R.string.auto_location_fail));
                }
            }
        };

        mLocationForm = new LocationForm(this, mLocationFormListener);
        mLocationDenialDialog = new MaterialDialog.Builder(this)
                .cancelable(false)
                .title(R.string.location_services_needed)
                .content(R.string.location_services_denial)
                .positiveText(R.string.location_services_confirm)
                .negativeText(R.string.enter_location_manually)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mLocationFetcher.askForLocation(LOCATION_SERVICES_CODE);
                        mDenialLock = false;
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mLocationForm.show();
                        mDenialLock = false;
                    }
                })
                .build();

        mLocationPermissionDialog = new MaterialDialog.Builder(this)
                .cancelable(false)
                .title(R.string.location_permission_needed)
                .content(R.string.location_permission_denial)
                .positiveText(R.string.give_location_permission)
                .negativeText(R.string.enter_location_manually)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        requestLocationPermission();
                        mDenialLock = false;
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mLocationForm.show();
                        mDenialLock = false;
                    }
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Run this here instead of onCreate() to cover the case where they return from turning on location
        if (mCurrentLocation == null && !mDenialLock) {
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (SmartLocation.with(this).location().state().locationServicesEnabled()) {
                runLocationFetch();
            } else {
                mLocationFetcher.askForLocation(LOCATION_SERVICES_CODE);
            }
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        PermissionUtils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void runLocationFetch() {
        mLocationFetched = false;
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        mLocationChecker.removeCallbacks(mLocationCheckTask);
                        mLocationFetched = true;
                        mCurrentLocation = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
                        fetchRestaurants();
                    }
                });
        mLocationChecker.postDelayed(mLocationCheckTask, 10000L);
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        mRestaurants.setVisibility(View.GONE);
        mNoResults.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);

        if (mCurrentLocation != null) {
            fetchRestaurants();
        }

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

    /** Fetches restaurants with the current location and search input */
    private void fetchRestaurants() {
        mRestClient.fetchRestaurants(mSearchInput.getText().toString(), mCurrentLocation);
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

    @OnItemClick(R.id.restaurants)
    public void onRestaurantClicked(int position) {
        Restaurant restaurant = mAdapter.getItem(position);
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
        mLocationForm.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            mDenialLock = true;
            mLocationPermissionDialog.show();
        }
    }

    private void stopFetchingCurrentLocation() {
        mLocationChecker.removeCallbacks(mLocationCheckTask);
        SmartLocation.with(this).location().stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SERVICES_CODE) {
            if (resultCode == RESULT_OK) {
                UIUtils.showSnackbar(mParent, getString(R.string.location_services_on));
                runLocationFetch();
            } else {
                mDenialLock = true;
                mLocationDenialDialog.show();
            }
        }
    }

    @OnClick(R.id.back_button)
    public void goBack() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocationFetcher.stop();
        stopFetchingCurrentLocation();

        // Stop listening for restaurant search results
        mRestClient.unregisterRestaurantResultsHandler(this);
        mRestClient.cancelRestaurantFetch();
    }
}
