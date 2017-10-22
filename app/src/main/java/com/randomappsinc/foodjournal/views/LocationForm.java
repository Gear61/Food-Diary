package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;

/** Widget that allows users to enter in their location manually */
public class LocationForm {

    public interface Listener {
        void onLocationEntered(String location);
    }

    @NonNull private Listener mListener;
    private MaterialDialog mLocationDialog;

    public LocationForm(Context context, @NonNull Listener listener) {
        mListener = listener;

        String location = context.getString(R.string.location);
        mLocationDialog = new MaterialDialog.Builder(context)
                .title(R.string.location_form)
                .content(R.string.location_form_prompt)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .alwaysCallInputCallback()
                .input(location, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean inputEnabled = !input.toString().trim().isEmpty();
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(inputEnabled);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String locationInput = dialog.getInputEditText().getText().toString().trim();
                        mListener.onLocationEntered(locationInput);
                    }
                })
                .build();
    }

    public void show() {
        mLocationDialog.show();
    }
}
