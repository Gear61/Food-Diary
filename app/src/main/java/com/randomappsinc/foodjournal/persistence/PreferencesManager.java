package com.randomappsinc.foodjournal.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.foodjournal.utils.MyApplication;

public class PreferencesManager {

    private SharedPreferences prefs;

    private static final String BEARER_TOKEN_KEY = "bearerToken";
    private static final String FIRST_TIME_KEY = "firstTime";
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

    public boolean isFirstTime() {
        boolean isFirstTime = prefs.getBoolean(FIRST_TIME_KEY, true);
        if (isFirstTime) {
            prefs.edit().putBoolean(FIRST_TIME_KEY, false).apply();
        }
        return isFirstTime;
    }
}
