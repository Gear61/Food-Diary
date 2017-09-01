package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.models.Location;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.LocationDO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class LocationsDBManager {

    private static LocationsDBManager instance;

    public static LocationsDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized LocationsDBManager getSync() {
        if (instance == null) {
            instance = new LocationsDBManager();
        }
        return instance;
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public void addLocation(final Location location) {
        Number number = getRealm().where(LocationDO.class).findAll().max("id");
        final int locationId = number == null ? 1 : number.intValue() + 1;

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LocationDO locationDO = location.toLocationDO();
                locationDO.setId(locationId);
                getRealm().insert(locationDO);
            }
        });
    }

    public List<Location> getLocations() {
        List<LocationDO> locationDOs = getRealm().where(LocationDO.class).findAll();

        List<Location> locations = new ArrayList<>();
        for (LocationDO locationDO : locationDOs) {
            locations.add(DBConverter.getLocationFromDO(locationDO));
        }
        return locations;
    }

    public void updateLocation(final Location location) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getRealm().insertOrUpdate(location.toLocationDO());
            }
        });
    }

    public void setCurrentLocation(final Location location) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LocationDO oldCurrentLocation = getRealm().where(LocationDO.class)
                        .equalTo("isCurrentLocation", true)
                        .findFirst();

                if (oldCurrentLocation != null) {
                    oldCurrentLocation.setIsCurrentLocation(false);
                }

                getRealm().insertOrUpdate(location.toLocationDO());
            }
        });
    }

    public void deleteLocation(final Location location) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getRealm().where(LocationDO.class)
                        .equalTo("id", location.getId())
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }
}
