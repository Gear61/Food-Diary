package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.TotalStats;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;

public class StatsDBManager {

    private static final int NUM_TOP_RESTAURANTS = 3;

    private final Comparator<Restaurant> MOST_VISITED_COMPARATOR = new Comparator<Restaurant>() {
        @Override
        public int compare(Restaurant first, Restaurant second) {
            int firstCheckIns = first.getCheckIns().size();
            int secondCheckIns = second.getCheckIns().size();
            if (firstCheckIns > secondCheckIns) {
                return -1;
            } else if (firstCheckIns < secondCheckIns) {
                return 1;
            } else {
                return 0;
            }
        }
    };

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

    public List<Restaurant> getMostVisitedRestaurants() {
        List<RestaurantDO> restaurantDOs = getRealm().where(RestaurantDO.class).findAll();
        List<Restaurant> restaurants = new ArrayList<>();
        for (RestaurantDO restaurantDO : restaurantDOs) {
            restaurants.add(DBConverter.getRestaurantFromDO(restaurantDO));
        }
        Collections.sort(restaurants, MOST_VISITED_COMPARATOR);

        List<Restaurant> topRestaurants = new ArrayList<>();
        for (int i = 0; i < NUM_TOP_RESTAURANTS; i++) {
            topRestaurants.add(restaurants.get(i));
        }
        return topRestaurants;
    }
}
