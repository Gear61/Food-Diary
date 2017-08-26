package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Adapters.LocationsAdapter;
import com.randomappsinc.foodjournal.Persistence.PreferencesManager;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyLocationsActivity extends StandardActivity {

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.location_input) EditText mLocationInput;
    @BindView(R.id.plus_icon) ImageView mPlusIcon;
    @BindView(R.id.locations) ListView mLocations;
    @BindView(R.id.no_locations) View mNoLocations;

    private LocationsAdapter mLocationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_locations);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPlusIcon.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add).colorRes(R.color.white));

        mLocationsAdapter = new LocationsAdapter(this, mNoLocations, mParent);
        mLocations.setAdapter(mLocationsAdapter);
    }

    @OnClick(R.id.add_location)
    public void addLocation() {
        UIUtils.hideKeyboard(this);
        String location = mLocationInput.getText().toString();
        mLocationInput.setText("");
        if (location.isEmpty()) {
            UIUtils.showSnackbar(mParent, getString(R.string.empty_location));
        } else if (PreferencesManager.get().alreadyHasLocation(location)) {
            UIUtils.showSnackbar(mParent, getString(R.string.duplicate_location));
        } else {
            mLocationsAdapter.addLocation(location);
        }
    }

    @OnItemClick(R.id.locations)
    public void showLocationOptions(int position) {
        mLocationsAdapter.showOptionsDialog(position);
    }
}
