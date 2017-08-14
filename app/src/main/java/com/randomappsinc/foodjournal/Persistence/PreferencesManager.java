package com.randomappsinc.foodjournal.Persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.foodjournal.Utils.MyApplication;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class PreferencesManager {
    private SharedPreferences prefs;

    private static final String BEARER_TOKEN_KEY = "bearerToken";
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
}
