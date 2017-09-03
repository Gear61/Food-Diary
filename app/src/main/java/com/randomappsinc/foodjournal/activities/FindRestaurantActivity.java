package com.randomappsinc.foodjournal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.RestaurantSearchResultsAdapter;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.SavedLocation;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.LocationsDBManager;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.LocationChooser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class FindRestaurantActivity extends StandardActivity implements RestClient.RestaurantResultsHandler {

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.search_input) EditText mSearchInput;
    @BindView(R.id.clear_search) View mClearSearch;
    @BindView(R.id.restaurants) ListView mRestaurants;
    @BindView(R.id.loading) View mLoading;
    @BindView(R.id.no_results) View mNoResults;
    @BindView(R.id.set_location) FloatingActionButton mSetLocation;

    private final LocationChooser.Callback mLocationChoiceCallback = new LocationChooser.Callback() {
        @Override
        public void onLocationChosen(SavedLocation savedLocation) {
            if (mCurrentLocation.getId() != savedLocation.getId()) {
                mCurrentLocation = savedLocation;
                if (mCurrentLocation.getId() == LocationsDBManager.AUTOMATIC_LOCATION_ID) {
                    fetchCurrentLocation();
                } else {
                    fetchRestaurants();
                }
            }
            UIUtils.showSnackbar(mParent, getString(R.string.current_location_set));
        }
    };

    private RestClient mRestClient;
    private RestaurantSearchResultsAdapter mAdapter;
    private boolean mLocationFetched;
    private Handler mLocationChecker;
    private Runnable mLocationCheckTask;
    private SavedLocation mCurrentLocation;
    private LocationChooser mLocationChooser;
    private MaterialDialog mLocationServicesDialog;

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

        mSetLocation.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_map).colorRes(R.color.white));

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

        mLocationChooser = new LocationChooser(this, mLocationChoiceCallback);
        mCurrentLocation = DatabaseManager.get().getLocationsDBManager().getCurrentLocation();
        mLocationServicesDialog = new MaterialDialog.Builder(this)
                .content(R.string.location_services_needed)
                .negativeText(android.R.string.cancel)
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .build();

        // Automatically do a search if we have a pre-defined location
        if (mCurrentLocation.getId() != LocationsDBManager.AUTOMATIC_LOCATION_ID) {
            fetchRestaurants();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Run this here instead of onCreate() to cover the case where they return from turning on location
        if (mCurrentLocation.getId() == LocationsDBManager.AUTOMATIC_LOCATION_ID) {
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (SmartLocation.with(this).location().state().locationServicesEnabled()) {
                mLocationFetched = false;
                SmartLocation.with(this).location()
                        .oneFix()
                        .start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                mLocationChecker.removeCallbacks(mLocationCheckTask);
                                mLocationFetched = true;
                                mCurrentLocation.setId(0);
                                mCurrentLocation.setAddress(String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
                                fetchRestaurants();
                            }
                        });
                mLocationChecker.postDelayed(mLocationCheckTask, 10000L);
            } else {
                showLocationServicesDialog();
            }
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 1);
        }
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        mRestaurants.setVisibility(View.GONE);
        mNoResults.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);

        if (mCurrentLocation.getId() != LocationsDBManager.AUTOMATIC_LOCATION_ID) {
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
        mRestClient.fetchRestaurants(mSearchInput.getText().toString(), mCurrentLocation.getAddress());
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
        if (DatabaseManager.get().getRestaurantsDBManager().userAlreadyHasRestaurant(restaurant)) {
            UIUtils.showSnackbar(mParent, getString(R.string.restaurant_already_added));
        } else {
            DatabaseManager.get().getRestaurantsDBManager().addRestaurant(restaurant);
            setResult(RESULT_OK);
            finish();
        }
    }

    @OnClick(R.id.set_location)
    public void setLocation() {
        mLocationChooser.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        }
    }

    private void showLocationServicesDialog() {
        if (!mLocationServicesDialog.isShowing()) {
            mLocationServicesDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRestClient.unregisterRestaurantResultsHandler(this);
        mRestClient.cancelRestaurantFetch();
    }
}