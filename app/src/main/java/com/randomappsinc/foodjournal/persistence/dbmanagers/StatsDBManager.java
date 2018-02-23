package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.TopDish;
import com.randomappsinc.foodjournal.models.TotalStats;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class StatsDBManager {

    private static final int NUM_TOP_RESTAURANTS = 5;
    private static final int NUM_TOP_DISHES = 5;

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

    private final Comparator<TopDish> MOST_EATEN_COMPARATOR = new Comparator<TopDish>() {
        @Override
        public int compare(TopDish first, TopDish second) {
            int firstInstances = first.getNumInstances();
            int secondInstances = second.getNumInstances();
            if (firstInstances > secondInstances) {
                return -1;
            } else if (firstInstances < secondInstances) {
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

    public List<Restaurant> getTopRestaurants() {
        List<RestaurantDO> restaurantDOs = getRealm().where(RestaurantDO.class).findAll();
        List<Restaurant> restaurants = new ArrayList<>();
        for (RestaurantDO restaurantDO : restaurantDOs) {
            restaurants.add(DBConverter.getRestaurantFromDO(restaurantDO));
        }

        Collections.sort(restaurants, MOST_VISITED_COMPARATOR);
        List<Restaurant> topRestaurants = new ArrayList<>();
        for (int i = 0; i < NUM_TOP_RESTAURANTS && i < restaurants.size(); i++) {
            topRestaurants.add(restaurants.get(i));
        }
        return topRestaurants;
    }

    public List<TopDish> getTopDishes() {
        List<RestaurantDO> restaurantDOs = getRealm().where(RestaurantDO.class).findAll();

        List<TopDish> candidates = new ArrayList<>();
        for (RestaurantDO restaurantDO : restaurantDOs) {
            Map<String, List<DishDO>> dishesMap = new HashMap<>();
            List<DishDO> dishes = restaurantDO.getDishes();
            for (DishDO dishDO : dishes) {
                String dishTitle = dishDO.getTitle();
                if (dishesMap.containsKey(dishTitle)) {
                    dishesMap.get(dishTitle).add(dishDO);
                } else {
                    List<DishDO> newList = new ArrayList<>();
                    newList.add(dishDO);
                    dishesMap.put(dishTitle, newList);
                }
            }

            for (List<DishDO> restaurantDishesList : dishesMap.values()) {
                TopDish topDish = new TopDish();

                ArrayList<Dish> finalDishes = new ArrayList<>();
                for (DishDO dishDO : restaurantDishesList) {
                    finalDishes.add(DBConverter.getDishFromDO(dishDO));
                }
                topDish.setInstances(finalDishes);

                candidates.add(topDish);
            }
        }

        Collections.sort(candidates, MOST_EATEN_COMPARATOR);
        List<TopDish> topDishes = new ArrayList<>();
        for (int i = 0; i < NUM_TOP_DISHES && i < candidates.size(); i++) {
            topDishes.add(candidates.get(i));
        }
        return topDishes;
    }
}
