package com.randomappsinc.foodjournal.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static final long MILLIS_IN_A_DAY = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
    public static final long MILLIS_IN_3_HOURS = TimeUnit.MILLISECONDS.convert(3, TimeUnit.HOURS);

    private static final String DATE_FORMAT = "EEEE, MMMM d, yyyy - h:mm a";
    private static final String CHECK_IN_SEARCH_FORMAT = "MM/dd/yy";

    public static String getDefaultTimeText(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getCheckInSearchTimeText(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CHECK_IN_SEARCH_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}
