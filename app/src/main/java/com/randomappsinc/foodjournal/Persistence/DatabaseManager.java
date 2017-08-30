package com.randomappsinc.foodjournal.Persistence;

import com.randomappsinc.foodjournal.Persistence.DBManagers.CheckInsDBManager;
import com.randomappsinc.foodjournal.Persistence.DBManagers.RestaurantsDBManager;
import com.randomappsinc.foodjournal.Utils.MyApplication;

import io.realm.Realm;

public class DatabaseManager {

    private static DatabaseManager instance;

    public static DatabaseManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized DatabaseManager getSync() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private CheckInsDBManager mCheckInsDBManager;
    private RestaurantsDBManager mRestaurantsDBManager;

    private DatabaseManager() {
        Realm.init(MyApplication.getAppContext());
        mCheckInsDBManager = CheckInsDBManager.get();
        mRestaurantsDBManager = RestaurantsDBManager.get();
    }

    public CheckInsDBManager getCheckInsDBManager() {
        return mCheckInsDBManager;
    }

    public RestaurantsDBManager getRestaurantsDBManager() {
        return mRestaurantsDBManager;
    }
}
