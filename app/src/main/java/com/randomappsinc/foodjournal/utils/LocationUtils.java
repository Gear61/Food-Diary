package com.randomappsinc.foodjournal.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.R;

import java.util.ArrayList;
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

    public static String[] getLocationOptions(String location) {
        Context context = MyApplication.getAppContext();
        List<String> options = new ArrayList<>();
        if (!location.equals(PreferencesManager.get().getCurrentLocation())) {
            options.add(context.getString(R.string.set_as_current));
        }
        options.add(context.getString(R.string.edit_location));
        options.add(context.getString(R.string.delete_location));
        return options.toArray(new String[options.size()]);
    }
}
