package com.randomappsinc.foodjournal.persistence.dbmanagers;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.SavedLocation;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.persistence.models.SavedLocationDO;
import com.randomappsinc.foodjournal.utils.MyApplication;

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

    private LocationsDBManager() {
        if (PreferencesManager.get().isFirstTime()) {
            SavedLocation automaticSavedLocation = new SavedLocation();
            automaticSavedLocation.setId(-1);
            automaticSavedLocation.setName(MyApplication.getAppContext().getString(R.string.automatic));
            automaticSavedLocation.setAddress(MyApplication.getAppContext().getString(R.string.automatic));
            addLocation(automaticSavedLocation);
        }
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public void addLocation(final SavedLocation savedLocation) {
        Number number = getRealm().where(SavedLocationDO.class).findAll().max("id");
        final int locationId = number == null ? 1 : number.intValue() + 1;

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SavedLocationDO savedLocationDO = savedLocation.toLocationDO();
                savedLocationDO.setId(locationId);
                getRealm().insert(savedLocationDO);
            }
        });
    }

    // Gets the list of user locations for "My Locations" (no automatic)
    public List<SavedLocation> getLocationsList() {
        List<SavedLocationDO> savedLocationDOs = getRealm()
                .where(SavedLocationDO.class)
                .greaterThan("id", 0)
                .findAll();

        List<SavedLocation> savedLocations = new ArrayList<>();
        for (SavedLocationDO savedLocationDO : savedLocationDOs) {
            savedLocations.add(DBConverter.getLocationFromDO(savedLocationDO));
        }
        return savedLocations;
    }

    public void updateLocation(final SavedLocation savedLocation) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getRealm().insertOrUpdate(savedLocation.toLocationDO());
            }
        });
    }

    public void setCurrentLocation(final SavedLocation savedLocation) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SavedLocationDO oldCurrentLocation = getRealm().where(SavedLocationDO.class)
                        .equalTo("isCurrentLocation", true)
                        .findFirst();

                if (oldCurrentLocation != null) {
                    oldCurrentLocation.setIsCurrentLocation(false);
                }

                getRealm().insertOrUpdate(savedLocation.toLocationDO());
            }
        });
    }

    public void deleteLocation(final SavedLocation savedLocation) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getRealm().where(SavedLocationDO.class)
                        .equalTo("id", savedLocation.getId())
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }
}
