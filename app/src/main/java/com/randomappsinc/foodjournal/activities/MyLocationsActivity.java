package com.randomappsinc.foodjournal.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ListView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.LocationsAdapter;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.LocationAdder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyLocationsActivity extends StandardActivity {

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.locations) ListView mLocations;
    @BindView(R.id.no_locations) View mNoLocations;
    @BindView(R.id.add_location) FloatingActionButton mAddLocation;

    private final LocationAdder.Callback mLocationCallback = new LocationAdder.Callback() {
        @Override
        public void onLocationAdded() {
            mLocationsAdapter.resyncWithDB();
            UIUtils.showSnackbar(mParent, getString(R.string.location_added));
        }
    };

    private LocationsAdapter mLocationsAdapter;
    private LocationAdder mLocationAdder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_locations);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAddLocation.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add).colorRes(R.color.white));

        mLocationsAdapter = new LocationsAdapter(this, mNoLocations, mParent);
        mLocations.setAdapter(mLocationsAdapter);

        mLocationAdder = new LocationAdder(this, mLocationCallback);
    }

    @OnClick(R.id.add_location)
    public void addLocation() {
        mLocationAdder.show();
    }

    @OnItemClick(R.id.locations)
    public void showLocationOptions(int position) {
        mLocationsAdapter.showOptionsDialog(position);
    }
}
