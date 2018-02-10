package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.TotalStats;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import io.realm.Realm;

public class StatsDBManager {

    private static StatsDBManager instance;

    public static StatsDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized StatsDBManager getSync() {
        if (instance == null) {
            instance = new StatsDBManager();
        }
        return instance;
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public TotalStats getTotalStats() {
        TotalStats totalStats = new TotalStats();
        int numDishes = getRealm().where(DishDO.class).findAll().size();
        totalStats.setNumDishes(numDishes);

        int numFavorites = getRealm()
                .where(DishDO.class)
                .equalTo("isFavorited", true)
                .findAll()
                .size();
        totalStats.setNumFavorites(numFavorites);

        int numRestaurants = getRealm().where(RestaurantDO.class).findAll().size();
        totalStats.setNumRestaurants(numRestaurants);

        int numCheckIns = getRealm().where(CheckInDO.class).findAll().size();
        totalStats.setNumCheckIns(numCheckIns);
        return totalStats;
    }
}
