package com.randomappsinc.foodjournal.views;

import android.app.FragmentManager;

import com.randomappsinc.foodjournal.fragments.DatePickerFragment;
import com.randomappsinc.foodjournal.fragments.TimePickerFragment;

import java.util.Calendar;

public class DateTimeAdder {

    public interface Listener {
        void onDateTimeChosen(long timeChosen);
    }

    private FragmentManager fragmentManager;
    private Calendar calendar;
    private Listener listener;
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    private long currentTime;

    private final DatePickerFragment.Listener dateListener = new DatePickerFragment.Listener() {
        @Override
        public void onDateChosen(int year, int month, int day) {
            datePickerFragment.setListener(null);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            timePickerFragment.show(fragmentManager, null);
        }

        @Override
        public long getCurrentTime() {
            return currentTime;
        }
    };

    private final TimePickerFragment.Listener timeListener = new TimePickerFragment.Listener() {
        @Override
        public void onTimeChosen(int hourOfDay, int minute) {
            timePickerFragment.setListener(null);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            listener.onDateTimeChosen(calendar.getTimeInMillis());
        }

        @Override
        public long getCurrentTime() {
            return currentTime;
        }
    };

    public DateTimeAdder(FragmentManager fragmentManager, Listener listener) {
        this.fragmentManager = fragmentManager;
        calendar = Calendar.getInstance();

        this.listener = listener;

        datePickerFragment = new DatePickerFragment();
        timePickerFragment = new TimePickerFragment();
    }

    public void show(long currentTime) {
        this.currentTime = currentTime;
        datePickerFragment.setListener(dateListener);
        timePickerFragment.setListener(timeListener);
        datePickerFragment.show(fragmentManager, null);
    }
}
