package com.randomappsinc.foodjournal.Persistence;

import com.randomappsinc.foodjournal.Models.Restaurant;
import com.randomappsinc.foodjournal.Persistence.Models.RestaurantDO;
import com.randomappsinc.foodjournal.Utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

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

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    private DatabaseManager() {
        Realm.init(MyApplication.getAppContext());
    }

    public List<Restaurant> getUserRestaurants() {
        List<Restaurant> locations = new ArrayList<>();
        List<RestaurantDO> restaurantDOs = getRealm()
                .where(RestaurantDO.class)
                .findAll();
        for (RestaurantDO restaurantDO : restaurantDOs) {
            locations.add(DBConverter.getRestaurantFromDO(restaurantDO));
        }
        return locations;
    }

    public void addRestaurant(final Restaurant restaurant) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(restaurant.toRestaurantDO());
            }
        });
    }
}
