package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Location;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

public class LocationAdder {

    public interface Callback {
        void onLocationAdded();
    }

    private Location mLocation;
    private Callback mCallback;
    private MaterialDialog mNameDialog;
    private MaterialDialog mAddressDialog;

    public LocationAdder(Context context, Callback callback) {
        mLocation = new Location();
        mCallback = callback;

        mNameDialog = new MaterialDialog.Builder(context)
                .title(R.string.set_location_name)
                .input(context.getString(R.string.name), "", new MaterialDialog.InputCallback() {
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
                        mLocation.setName(name);
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
                        mLocation.setAddress(address);
                        DatabaseManager.get().getLocationsDBManager().addLocation(mLocation);
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
        mLocation.setName("");
        mNameDialog.getInputEditText().setText("");
        mLocation.setAddress("");
        mAddressDialog.getInputEditText().setText("");

        mNameDialog.show();
    }
}
