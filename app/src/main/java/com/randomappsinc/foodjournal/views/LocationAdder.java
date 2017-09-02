package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.SavedLocation;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

public class LocationAdder {

    public interface Callback {
        void onLocationAdded();
    }

    private SavedLocation mSavedLocation;
    private Callback mCallback;
    private MaterialDialog mNameDialog;
    private MaterialDialog mAddressDialog;

    public LocationAdder(Context context, Callback callback) {
        mSavedLocation = new SavedLocation();
        mCallback = callback;

        mNameDialog = new MaterialDialog.Builder(context)
                .title(R.string.set_location_name)
                .input(context.getString(R.string.location_name_hint), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean enableNext = input.length() > 0;
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(enableNext);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText(R.string.next)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String name = dialog.getInputEditText().getText().toString().trim();
                        mSavedLocation.setName(name);
                        mAddressDialog.show();
                    }
                })
                .negativeText(android.R.string.no)
                .build();

        mAddressDialog = new MaterialDialog.Builder(context)
                .title(R.string.set_location_address)
                .input(context.getString(R.string.address), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean enableNext = input.length() > 0;
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(enableNext);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText(R.string.done)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String address = dialog.getInputEditText().getText().toString().trim();
                        mSavedLocation.setAddress(address);
                        DatabaseManager.get().getLocationsDBManager().addLocation(mSavedLocation);
                        mCallback.onLocationAdded();
                    }
                })
                .negativeText(android.R.string.no)
                .neutralText(R.string.back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mNameDialog.show();
                    }
                })
                .build();
    }

    public void show() {
        mSavedLocation.setName("");
        mNameDialog.getInputEditText().setText("");
        mSavedLocation.setAddress("");
        mAddressDialog.getInputEditText().setText("");

        mNameDialog.show();
    }
}
