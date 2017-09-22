package com.randomappsinc.foodjournal.views;

import android.app.FragmentManager;

import com.randomappsinc.foodjournal.fragments.DatePickerFragment;
import com.randomappsinc.foodjournal.fragments.TimePickerFragment;

import java.util.Calendar;

public class DateTimeAdder {

    public interface Listener {
        void onDateTimeChosen(long timeChosen);
    }

    private FragmentManager mFragmentManager;
    private Calendar mCalendar;
    private Listener mListener;
    private DatePickerFragment mDatePickerFragment;
    private TimePickerFragment mTimePickerFragment;
    private long mCurrentTime;

    private final DatePickerFragment.Listener mDateListener = new DatePickerFragment.Listener() {
        @Override
        public void onDateChosen(int year, int month, int day) {
            mDatePickerFragment.setListener(null);
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);
            mTimePickerFragment.show(mFragmentManager, null);
        }

        @Override
        public long getCurrentTime() {
            return mCurrentTime;
        }
    };

    private final TimePickerFragment.Listener mTimeListener = new TimePickerFragment.Listener() {
        @Override
        public void onTimeChosen(int hourOfDay, int minute) {
            mTimePickerFragment.setListener(null);
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mListener.onDateTimeChosen(mCalendar.getTimeInMillis());
        }

        @Override
        public long getCurrentTime() {
            return mCurrentTime;
        }
    };

    public DateTimeAdder(FragmentManager fragmentManager, Listener listener) {
        mFragmentManager = fragmentManager;
        mCalendar = Calendar.getInstance();

        mListener = listener;

        mDatePickerFragment = new DatePickerFragment();
        mTimePickerFragment = new TimePickerFragment();
    }

    public void show(long currentTime) {
        mCurrentTime = currentTime;
        mDatePickerFragment.setListener(mDateListener);
        mTimePickerFragment.setListener(mTimeListener);
        mDatePickerFragment.show(mFragmentManager, null);
    }
}
