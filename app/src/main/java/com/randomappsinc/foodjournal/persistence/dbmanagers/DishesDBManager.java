package com.randomappsinc.foodjournal.persistence.dbmanagers;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;
import com.randomappsinc.foodjournal.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DishesDBManager {

    public static final int DISHES_PER_PAGE = 10;

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
        dish.setId(dishId);

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DishDO dishDO = dish.toDishDO();
                restaurantDO.getDishes().add(dishDO);
            }
        });
    }

    public ArrayList<Dish> getDishesPage(String restaurantId, @Nullable Dish lastDish) {
        RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();

        if (restaurantDO != null) {
            RealmResults<DishDO> dishDOs;
            String[] fieldNames = {"timeAdded", "id"};
            Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING};

            if (lastDish == null) {
                dishDOs = getRealm()
                        .where(DishDO.class)
                        .equalTo("restaurantId", restaurantId)
                        .findAllSorted(fieldNames, sort);
            } else {
                dishDOs = getRealm()
                        .where(DishDO.class)
                        .equalTo("restaurantId", restaurantId)
                        .beginGroup()
                            .beginGroup()
                                .equalTo("timeAdded", lastDish.getTimeAdded())
                                .lessThan("id", lastDish.getId())
                            .endGroup()
                            .or()
                            .lessThan("timeAdded", lastDish.getTimeAdded())
                        .endGroup()
                        .findAllSorted(fieldNames, sort);
            }

            ArrayList<Dish> dishes = new ArrayList<>();
            for (int i = 0; i < DISHES_PER_PAGE && i < dishDOs.size(); i++) {
                dishes.add(DBConverter.getDishFromDO(dishDOs.get(i)));
            }
            return dishes;
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<Dish> getDishesPage(@Nullable Dish lastDish) {
        RealmResults<DishDO> dishDOs;
        String[] fieldNames = {"timeAdded", "id"};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING};

        if (lastDish == null) {
            dishDOs = getRealm()
                    .where(DishDO.class)
                    .findAllSorted(fieldNames, sort);
        } else {
            dishDOs = getRealm()
                    .where(DishDO.class)
                    .beginGroup()
                        .beginGroup()
                            .equalTo("timeAdded", lastDish.getTimeAdded())
                            .lessThan("id", lastDish.getId())
                        .endGroup()
                        .or()
                        .lessThan("timeAdded", lastDish.getTimeAdded())
                    .endGroup()
                    .findAllSorted(fieldNames, sort);
        }

        ArrayList<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < DISHES_PER_PAGE && i < dishDOs.size(); i++) {
            dishes.add(DBConverter.getDishFromDO(dishDOs.get(i)));
        }
        return dishes;
    }

    public void deleteDish(final Dish dish) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                DishDO dishDO = realm.where(DishDO.class)
                        .equalTo("id", dish.getId())
                        .findFirst();
                if (dishDO != null) {
                    dishDO.deleteFromRealm();
                }
            }
        });

        String filePath = dish.getUriString().substring(dish.getUriString().lastIndexOf('/'));
        String completePath = Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/com.randomappsinc.foodjournal/files/Pictures"
                + filePath;
        File imageFile = new File(completePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
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

    // Return all dishes within 24 hours from the same restaurant alongside those already tagged
    public List<Dish> getTaggingSuggestions(CheckIn checkIn) {
        List<DishDO> dishDOs = getRealm()
                .where(DishDO.class)
                .beginGroup()
                    .beginGroup()
                        .equalTo("restaurantId", checkIn.getRestaurantId())
                        .equalTo("checkInId", 0)
                        .lessThanOrEqualTo("timeAdded", checkIn.getTimeAdded() + TimeUtils.MILLIS_IN_A_DAY)
                        .greaterThanOrEqualTo("timeAdded", checkIn.getTimeAdded() - TimeUtils.MILLIS_IN_A_DAY)
                    .endGroup()
                    .or()
                    // Dishes already tagged to this check-in. Ignore ID 0 for the new check-in case
                    .beginGroup()
                        .equalTo("checkInId", checkIn.getCheckInId())
                        .notEqualTo("checkInId", 0)
                    .endGroup()
                .endGroup()
                .findAllSorted("timeAdded", Sort.DESCENDING);

        List<Dish> dishes = new ArrayList<>();
        for (DishDO dishDO : dishDOs) {
            dishes.add(DBConverter.getDishFromDO(dishDO));
        }
        return dishes;
    }

    public void untagDishes(final List<Dish> dishesToUntag) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Dish taggedDish : dishesToUntag) {
                    DishDO dishDO = getRealm()
                            .where(DishDO.class)
                            .equalTo("id", taggedDish.getId())
                            .findFirst();
                    if (dishDO != null) {
                        dishDO.setCheckInId(0);
                        dishDO.setTimeLastUpdated(System.currentTimeMillis());
                    }
                }
            }
        });
    }

    public void untagDishes(int checkInId) {
        final List<DishDO> dishes = getRealm()
                .where(DishDO.class)
                .equalTo("checkInId", checkInId)
                .findAll();

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (DishDO dishDO : dishes) {
                    dishDO.setCheckInId(0);
                }
            }
        });
    }

    public int getNextDishId() {
        Number number = getRealm().where(DishDO.class).findAll().max("id");
        return number == null ? 1 : number.intValue() + 1;
    }

    public Dish getLastUpdatedDish() {
        DishDO dishDO = getRealm()
                .where(DishDO.class)
                .findAllSorted("timeLastUpdated", Sort.DESCENDING)
                .first();
        return DBConverter.getDishFromDO(dishDO);
    }
}
