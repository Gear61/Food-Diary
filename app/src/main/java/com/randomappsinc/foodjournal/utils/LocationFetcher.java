package com.randomappsinc.foodjournal.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.randomappsinc.foodjournal.R;

public class LocationFetcher {

    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsRequest.Builder mLocationBuilder;
    private PendingResult<LocationSettingsResult> mLocationResult;

    public LocationFetcher(Activity activity) {
        mActivity = activity;

        mGoogleApiClient = new GoogleApiClient
                .Builder(mActivity)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationBuilder = new LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest);
        mLocationBuilder.setAlwaysShow(true);
    }

    public void askForLocation(final int requestCode) {
        stop();

        mLocationResult = LocationServices
                .SettingsApi
                .checkLocationSettings(mGoogleApiClient, mLocationBuilder.build());
        mLocationResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show dialog to turn on location services
                            status.startResolutionForResult(mActivity, requestCode);
                        } catch (IntentSender.SendIntentException e) {
                            openLocationSettings();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        openLocationSettings();
                        break;
                }
            }
        });
    }

    // Get location services the old fashioned way
    private void openLocationSettings() {
        Toast.makeText(mActivity, R.string.turn_on_location_services, Toast.LENGTH_LONG).show();
        mActivity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public void stop() {
        if (mLocationResult != null) {
            mLocationResult.cancel();
        }
    }
}
