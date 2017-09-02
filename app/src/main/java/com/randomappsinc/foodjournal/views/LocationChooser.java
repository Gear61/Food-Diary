package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.SavedLocation;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/** Widget for the user to set their current location */
public class LocationChooser {

    public interface Callback {
        void onLocationChosen(SavedLocation savedLocation);
    }

    private Callback mCallback;
    private List<SavedLocation> mLocations;
    private MaterialDialog mChooser;
    private int mCurrentLocation;

    public LocationChooser(Context context, Callback callback) {
        mCallback = callback;
        mLocations = DatabaseManager.get().getLocationsDBManager().getLocationOptions();

        String[] locationNames = new String[mLocations.size()];
        for (int i = 0; i < mLocations.size(); i++) {
            locationNames[i] = mLocations.get(i).getName();

            if (mLocations.get(i).isCurrentLocation()) {
                mCurrentLocation = i;
            }
        }

        mChooser = new MaterialDialog.Builder(context)
                .title(R.string.choose_current_location)
                .content(R.string.current_instructions)
                .items(locationNames)
                .itemsCallbackSingleChoice(mCurrentLocation, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                SavedLocation savedLocation = mLocations.get(which);
                                DatabaseManager.get().getLocationsDBManager().setCurrentLocation(savedLocation);
                                mCurrentLocation = which;
                                mCallback.onLocationChosen(savedLocation);
                                return true;
                            }
                        })
                .positiveText(R.string.choose)
                .negativeText(android.R.string.cancel)
                .build();
    }

    public void show() {
        mChooser.setSelectedIndex(mCurrentLocation);
        mChooser.show();
    }
}
