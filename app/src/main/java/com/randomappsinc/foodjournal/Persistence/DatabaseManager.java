package com.randomappsinc.foodjournal.Persistence;

import com.randomappsinc.foodjournal.Models.CheckIn;
import com.randomappsinc.foodjournal.Models.Restaurant;
import com.randomappsinc.foodjournal.Persistence.Models.CheckInDO;
import com.randomappsinc.foodjournal.Persistence.Models.RestaurantDO;
import com.randomappsinc.foodjournal.Utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

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

    public List<Restaurant> getUserRestaurants(String searchTerm) {
        List<Restaurant> locations = new ArrayList<>();
        RealmQuery<RestaurantDO> restaurantQuery = getRealm()
                .where(RestaurantDO.class);
        if (!searchTerm.isEmpty()) {
            restaurantQuery = restaurantQuery.contains("name", searchTerm.toLowerCase(), Case.INSENSITIVE);
        }
        RealmResults<RestaurantDO> restaurantDOs = restaurantQuery.findAllSorted("timeAdded", Sort.DESCENDING);
        for (RestaurantDO restaurantDO : restaurantDOs) {
            locations.add(DBConverter.getRestaurantFromDO(restaurantDO));
        }
        return locations;
    }

    public int getNumUserRestaurants() {
        return getRealm().where(RestaurantDO.class).findAll().size();
    }

    public boolean userAlreadyHasRestaurant(Restaurant restaurant) {
        return getRealm().where(RestaurantDO.class).equalTo("id", restaurant.getId()).findFirst() != null;
    }

    public void addRestaurant(final Restaurant restaurant) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RestaurantDO restaurantDO = restaurant.toRestaurantDO();
                restaurantDO.setTimeAdded(System.currentTimeMillis());
                realm.insert(restaurantDO);
            }
        });
    }

    public void addCheckIn(String restaurantId, final CheckIn checkIn) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                restaurantDO.getCheckIns().add(checkIn.toCheckInDO());
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
}
