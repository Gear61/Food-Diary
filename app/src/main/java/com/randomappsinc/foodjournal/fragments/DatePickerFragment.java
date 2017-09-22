package com.randomappsinc.foodjournal.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface Listener {
        void onDateChosen(int year, int month, int day);

        long getCurrentTime();
    }

    private final Listener DUMMY_LISTENER = new Listener() {
        @Override
        public void onDateChosen(int year, int month, int day) {}

        @Override
        public long getCurrentTime() {
            return 0;
        }
    };

    private Listener mListener = DUMMY_LISTENER;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mListener.getCurrentTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setListener(Listener listener) {
        mListener = (listener == null) ? DUMMY_LISTENER : listener;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onDateChosen(year, month, day);
    }
}
