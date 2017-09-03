package com.randomappsinc.foodjournal.views;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.DatePickerFragment;
import com.randomappsinc.foodjournal.models.CheckIn;

public class CheckInAdder {

    public interface Callback {
        void onCheckInCreated(CheckIn checkIn);
    }

    private final DatePickerFragment.Listener mDateListener = new DatePickerFragment.Listener() {
        @Override
        public void onDateChosen(long dateTimeInMillis) {
            mCheckInTime = dateTimeInMillis;
            mCheckInMessageDialog.show();
        }

        @Override
        public long getCurrentTime() {
            return mCheckInTime;
        }
    };

    private Activity mActivity;
    private Callback mCallback;
    private MaterialDialog mCheckInMessageDialog;
    private DatePickerFragment mDatePickerFragment;
    private long mCheckInTime;

    public CheckInAdder(Activity activity, Callback callback) {
        mActivity = activity;
        mCallback = callback;

        mCheckInMessageDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.check_in_prompt)
                .input(mActivity.getString(R.string.check_in_prompt), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String message = input.toString().trim();
                        CheckIn checkIn = new CheckIn();
                        checkIn.setTimeAdded(mCheckInTime);
                        checkIn.setMessage(message);
                        mCallback.onCheckInCreated(checkIn);
                    }
                })
                .neutralText(R.string.back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mDatePickerFragment.show(mActivity.getFragmentManager(), "datePicker");
                    }
                })
                .negativeText(android.R.string.no)
                .build();

        mDatePickerFragment = new DatePickerFragment();
        mDatePickerFragment.setListener(mDateListener);
    }

    public void show() {
        mCheckInTime = System.currentTimeMillis();
        if (mCheckInMessageDialog.getInputEditText() != null) {
            mCheckInMessageDialog.getInputEditText().setText("");
        }

        mDatePickerFragment.show(mActivity.getFragmentManager(), "datePicker");
    }
}