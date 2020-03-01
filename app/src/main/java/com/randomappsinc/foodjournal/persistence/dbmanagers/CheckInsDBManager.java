package com.randomappsinc.foodjournal.persistence.dbmanagers;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;
import com.randomappsinc.foodjournal.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CheckInsDBManager {

    private static CheckInsDBManager instance;

    public static CheckInsDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized CheckInsDBManager getSync() {
        if (instance == null) {
            instance = new CheckInsDBManager();
        }
        return instance;
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public void addCheckIn(final CheckIn checkIn, boolean autoCreate) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", checkIn.getRestaurantId())
                .findFirst();

        if (restaurantDO == null) {
            return;
        }

        Number number = getRealm().where(CheckInDO.class).findAll().max("checkInId");
        final int checkInId = number == null ? 1 : number.intValue() + 1;
        checkIn.setCheckInId(checkInId);
        if (autoCreate) {
            checkIn.getTaggedDishes().get(0).setCheckInId(checkInId);
        }

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                CheckInDO checkInDO = checkIn.toCheckInDO();
                checkInDO.setCheckInId(checkInId);
                checkInDO.setRestaurantName(restaurantDO.getName());
                restaurantDO.getCheckIns().add(checkInDO);
            }
        });
    }

    public void updateCheckIn(final CheckIn checkIn) {
        // Need to update DishDOs because their check in ID could have changed
        for (Dish taggedDish : checkIn.getTaggedDishes()) {
            DatabaseManager.get().getDishesDBManager().updateDish(taggedDish);
        }

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insertOrUpdate(checkIn.toCheckInDO());
            }
        });
    }

    public void deleteCheckIn(final CheckIn checkIn) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(CheckInDO.class)
                        .equalTo("checkInId", checkIn.getCheckInId())
                        .findFirst()
                        .deleteFromRealm();
            }
        });

        DatabaseManager.get().getDishesDBManager().untagDishes(checkIn.getCheckInId());
    }

    public List<CheckIn> getCheckInsForRestaurant(String restaurantId) {
        RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();

        if (restaurantDO != null) {
            RealmResults<CheckInDO> checkInDOs = restaurantDO.getCheckIns()
                    .sort("timeAdded", Sort.DESCENDING);

            List<CheckIn> checkIns = new ArrayList<>();
            for (CheckInDO checkInDO : checkInDOs) {
                checkIns.add(DBConverter.getCheckInFromDO(checkInDO));
            }
            return checkIns;
        } else {
            return new ArrayList<>();
        }
    }

    public Restaurant getAutoFillRestaurant() {
        RealmResults<CheckInDO> checkInDOs = getRealm()
                .where(CheckInDO.class)
                // Before now
                .lessThan("timeAdded", System.currentTimeMillis())
                // Less than 30 minutes ago
                .greaterThanOrEqualTo(
                        "timeAdded",
                        System.currentTimeMillis() - TimeUtils.MILLIS_IN_3_HOURS)
                // Get most recent check-in
                .findAllSorted("timeAdded", Sort.DESCENDING);
        if (checkInDOs.isEmpty()) {
            return null;
        } else {
            CheckInDO checkInDO = checkInDOs.first();
            return DatabaseManager.get().getRestaurantsDBManager().getRestaurant(checkInDO.getRestaurantId());
        }
    }

    public CheckIn getAutoTagCheckIn(Dish dish) {
        RealmResults<CheckInDO> checkInDOs = getRealm()
                .where(CheckInDO.class)
                .equalTo("restaurantId", dish.getRestaurantId())
                // Before the dish was added
                .lessThan("timeAdded", dish.getTimeAdded())
                // Less than 3 hours ago relative to dish time (account for tasting menu)
                .greaterThanOrEqualTo("timeAdded", dish.getTimeAdded() - TimeUtils.MILLIS_IN_3_HOURS)
                // Get most recent check-in
                .findAllSorted("timeAdded", Sort.DESCENDING);
        if (checkInDOs.isEmpty()) {
            return null;
        } else {
            return DBConverter.getCheckInFromDO(checkInDOs.first());
        }
    }

    public void autoCreateCheckIn(Dish dish) {
        CheckIn checkIn = new CheckIn();
        checkIn.setTimeAdded(dish.getTimeAdded());
        checkIn.setRestaurantId(dish.getRestaurantId());
        checkIn.setRestaurantName(dish.getRestaurantName());

        int dishId = DatabaseManager.get().getDishesDBManager().getNextDishId();
        dish.setId(dishId);
        checkIn.addTaggedDish(dish);

        addCheckIn(checkIn, true);
        DatabaseManager.get().getRestaurantsDBManager().tagDishToRestaurant(dish);
    }
}
