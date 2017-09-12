package com.randomappsinc.foodjournal.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    private static final String DATE_FORMAT = "MMMM d, yyyy - h:mm a";

    public static String getTimeText(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}
