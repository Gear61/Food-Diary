package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

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
}
