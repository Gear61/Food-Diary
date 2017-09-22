package com.randomappsinc.foodjournal.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public interface Listener {
        void onTimeChosen(int hourOfDay, int minute);

        long getCurrentTime();
    }

    private final Listener DUMMY_LISTENER = new Listener() {
        @Override
        public void onTimeChosen(int hourOfDay, int minute) {}

        @Override
        public long getCurrentTime() {
            return 0;
        }
    };

    private TimePickerFragment.Listener mListener = DUMMY_LISTENER;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mListener.getCurrentTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void setListener(Listener listener) {
        mListener = (listener == null) ? DUMMY_LISTENER : listener;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimeChosen(hourOfDay, minute);
    }
}
