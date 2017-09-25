package com.randomappsinc.foodjournal.persistence.dbmanagers;

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

    public void addCheckIn(final CheckIn checkIn) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", checkIn.getRestaurantId())
                .findFirst();

        if (restaurantDO == null) {
            return;
        }

        Number number = getRealm().where(CheckInDO.class).findAll().max("checkInId");
        final int checkInId = number == null ? 1 : number.intValue() + 1;

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
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
            public void execute(Realm realm) {
                realm.insertOrUpdate(checkIn.toCheckInDO());
            }
        });
    }

    public void deleteCheckIn(final CheckIn checkIn) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(CheckInDO.class)
                        .equalTo("checkInId", checkIn.getCheckInId())
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    public List<CheckIn> getCheckIns(String restaurantId) {
        RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();

        if (restaurantDO != null) {
            RealmResults<CheckInDO> checkInDOs = restaurantDO.getCheckIns().sort("timeAdded", Sort.DESCENDING);

            List<CheckIn> checkIns = new ArrayList<>();
            for (CheckInDO checkInDO : checkInDOs) {
                checkIns.add(DBConverter.getCheckInFromDO(checkInDO));
            }
            return checkIns;
        } else {
            return new ArrayList<>();
        }
    }

    public List<CheckIn> getAllCheckIns() {
        List<CheckInDO> checkInDOs = getRealm()
                .where(CheckInDO.class)
                .findAllSorted("timeAdded", Sort.DESCENDING);

        List<CheckIn> checkIns = new ArrayList<>();
        for (CheckInDO checkInDO : checkInDOs) {
            checkIns.add(DBConverter.getCheckInFromDO(checkInDO));
        }
        return checkIns;
    }

    public Restaurant getAutoFillRestaurant() {
        RealmResults<CheckInDO> checkInDOs = getRealm()
                .where(CheckInDO.class)
                // Before now
                .lessThan("timeAdded", System.currentTimeMillis())
                // Less than 30 minutes ago
                .greaterThanOrEqualTo("timeAdded", System.currentTimeMillis() - TimeUtils.MILLIS_IN_30_MINUTES)
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
                // Before now
                .lessThan("timeAdded", System.currentTimeMillis())
                // Less than 30 minutes ago
                .greaterThanOrEqualTo("timeAdded", System.currentTimeMillis() - TimeUtils.MILLIS_IN_30_MINUTES)
                // Get most recent check-in
                .findAllSorted("timeAdded", Sort.DESCENDING);
        if (checkInDOs.isEmpty()) {
            return null;
        } else {
            return DBConverter.getCheckInFromDO(checkInDOs.first());
        }
    }

    public boolean shouldAutoCreateCheckIn(Dish dish) {
        return getAutoTagCheckIn(dish) == null;
    }
}
