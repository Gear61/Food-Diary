package com.randomappsinc.foodjournal.utils;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;
import java.util.Locale;

public class LocationUtils {

    public static String getAddressFromLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getAppContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    if (i != 0) {
                        addressText.append(" ");
                    }
                    addressText.append(address.getAddressLine(i));
                }
                return addressText.toString();
            }
        } catch (Exception ignored) {}
        return "";
    }
}
