package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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

    public void addDish(final Dish dish) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", dish.getRestaurantId())
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

    public List<Dish> getDishes(String restaurantId) {
        RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();

        if (restaurantDO != null) {
            RealmResults<DishDO> dishDOs = restaurantDO.getDishes().sort("timeLastUpdated", Sort.DESCENDING);

            List<Dish> dishes = new ArrayList<>();
            for (DishDO dishDO : dishDOs) {
                dishes.add(DBConverter.getDishFromDO(dishDO));
            }
            return dishes;
        } else {
            return new ArrayList<>();
        }
    }

    public List<Dish> getAllDishes() {
        List<DishDO> dishDOs = getRealm()
                .where(DishDO.class)
                .findAllSorted("timeLastUpdated", Sort.DESCENDING);

        List<Dish> dishes = new ArrayList<>();
        for (DishDO dishDO : dishDOs) {
            dishes.add(DBConverter.getDishFromDO(dishDO));
        }
        return dishes;
    }

    public void deleteDish(final Dish dish) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(DishDO.class)
                        .equalTo("id", dish.getId())
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    public void updateDish(final Dish dish) {
        // Get current version of dish to see if we need to remove it from its current restaurant
        DishDO dishDO = getRealm()
                .where(DishDO.class)
                .equalTo("id", dish.getId())
                .findFirst();

        if (dishDO == null) {
            return;
        }

        final DishDO newDishDO = dish.toDishDO();
        newDishDO.setTimeLastUpdated(System.currentTimeMillis());

        if (!dishDO.getRestaurantId().equals(dish.getRestaurantId())) {
            deleteDish(dish);

            final RestaurantDO restaurantDO = getRealm()
                    .where(RestaurantDO.class)
                    .equalTo("id", dish.getRestaurantId())
                    .findFirst();

            if (restaurantDO == null) {
                return;
            }

            getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    restaurantDO.getDishes().add(newDishDO);
                }
            });
        } else {
            getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(newDishDO);
                }
            });
        }
    }
}
