package com.randomappsinc.foodjournal.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.utils.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesManager {

    private SharedPreferences prefs;

    private static final String BEARER_TOKEN_KEY = "bearerToken";
    private static final String DEFAULT_LOCATION_KEY = "defaultLocation";
    private static final String SAVED_LOCATIONS_KEY = "savedLocations";
    private static PreferencesManager instance;

    public static PreferencesManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized PreferencesManager getSync() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private PreferencesManager() {
        Context context = MyApplication.getAppContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getBearerToken() {
        return prefs.getString(BEARER_TOKEN_KEY, "");
    }

    public void setBearerToken(String bearerToken) {
        prefs.edit().putString(BEARER_TOKEN_KEY, bearerToken).apply();
    }

    public String getCurrentLocation() {
        return prefs.getString(DEFAULT_LOCATION_KEY, MyApplication.getAppContext().getString(R.string.automatic));
    }

    public void setCurrentLocation(String defaultLocation) {
        prefs.edit().putString(DEFAULT_LOCATION_KEY, defaultLocation).apply();
    }

    private Set<String> getSavedLocations() {
        return prefs.getStringSet(SAVED_LOCATIONS_KEY, new HashSet<String>());
    }

    public void addSavedLocation(String location) {
        Set<String> savedLocations = getSavedLocations();
        prefs.edit().remove(SAVED_LOCATIONS_KEY).apply();
        savedLocations.add(location);
        prefs.edit().putStringSet(SAVED_LOCATIONS_KEY, savedLocations).apply();
    }

    public void removeSavedLocation(String location) {
        if (location.equals(getCurrentLocation())) {
            prefs.edit().remove(DEFAULT_LOCATION_KEY).apply();
        }

        Set<String> savedLocations = getSavedLocations();
        prefs.edit().remove(SAVED_LOCATIONS_KEY).apply();
        savedLocations.remove(location);
        prefs.edit().putStringSet(SAVED_LOCATIONS_KEY, savedLocations).apply();
    }

    public void changeSavedLocation(String oldLocation, String newLocation) {
        if (oldLocation.equals(getCurrentLocation())) {
            setCurrentLocation(newLocation);
        }
        removeSavedLocation(oldLocation);
        addSavedLocation(newLocation);
    }

    public boolean alreadyHasLocation(String location) {
        return getSavedLocations().contains(location);
    }

    public String[] getLocationsArray() {
        Set<String> savedLocations = getSavedLocations();
        ArrayList<String> locationsList = new ArrayList<>();
        locationsList.add(MyApplication.getAppContext().getString(R.string.automatic));

        ArrayList<String> savedLocationsList = new ArrayList<>();
        for (String location : savedLocations) {
            savedLocationsList.add(location);
        }
        Collections.sort(savedLocationsList);

        locationsList.addAll(savedLocationsList);

        return locationsList.toArray(new String[locationsList.size()]);
    }

    public int getCurrentLocationIndex() {
        String[] savedLocations = getLocationsArray();
        String currentDefault = getCurrentLocation();
        for (int i = 0; i < savedLocations.length; i++) {
            if (savedLocations[i].equals(currentDefault)) {
                return i;
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<String> getUserLocations() {
        List<String> allLocations = new ArrayList(Arrays.asList(getLocationsArray()));
        allLocations.remove(0);
        return allLocations;
    }
}
