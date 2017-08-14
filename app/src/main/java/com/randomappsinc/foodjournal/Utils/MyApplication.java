package com.randomappsinc.foodjournal.Utils;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.randomappsinc.foodjournal.API.RestClient;
import com.randomappsinc.foodjournal.Persistence.PreferencesManager;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public final class MyApplication extends Application {
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule());
        instance = getApplicationContext();

        if (PreferencesManager.get().getBearerToken().isEmpty()) {
            RestClient.getInstance().refreshToken();
        }
    }

    public static Context getAppContext() {
        return instance;
    }
}
