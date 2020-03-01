package com.randomappsinc.foodjournal.location;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;

/** Widget that allows users to enter in their location manually */
public class LocationForm {

    public interface Listener {
        void onLocationEntered(String location);
    }

    @NonNull
    protected Listener listener;
    private MaterialDialog locationDialog;

    LocationForm(Context context, @NonNull Listener listener) {
        this.listener = listener;

        String location = context.getString(R.string.location);
        locationDialog = new MaterialDialog.Builder(context)
                .title(R.string.location_form_title)
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
                        LocationForm.this.listener.onLocationEntered(locationInput);
                    }
                })
                .build();
    }

    public void show(@StringRes int contentResId) {
        locationDialog.setContent(contentResId);
        locationDialog.show();
    }
}
