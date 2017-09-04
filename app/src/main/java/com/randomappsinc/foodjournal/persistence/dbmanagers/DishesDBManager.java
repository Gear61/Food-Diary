package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DishesDBManager {

    private static DishesDBManager instance;

    public static DishesDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized DishesDBManager getSync() {
        if (instance == null) {
            instance = new DishesDBManager();
        }
        return instance;
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public void addDish(final Dish dish, String restaurantId) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();

        if (restaurantDO == null) {
            return;
        }

        Number number = getRealm().where(DishDO.class).findAll().max("id");
        final int dishId = number == null ? 1 : number.intValue() + 1;

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DishDO dishDO = dish.toDishDO();
                dishDO.setId(dishId);
                dishDO.setTimeLastUpdated(System.currentTimeMillis());
                restaurantDO.getDishes().add(dishDO);
            }
        });
    }

    /** Fetches all dishes attached to a restaurant. Pass in null to fetch all dishes. */
    public List<Dish> getDishes(String restaurantId) {
        List<Dish> dishes = new ArrayList<>();
        return dishes;
    }
}
