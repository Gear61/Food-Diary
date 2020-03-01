package com.randomappsinc.foodjournal.persistence;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.persistence.dbmanagers.CheckInsDBManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.DishesDBManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.RestaurantsDBManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.SearchResultsDBManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.StatsDBManager;
import com.randomappsinc.foodjournal.utils.MyApplication;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DatabaseManager {

    private static final int CURRENT_REALM_VERSION = 4;

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

    private CheckInsDBManager checkInsDBManager;
    private RestaurantsDBManager restaurantsDBManager;
    private DishesDBManager dishesDBManager;
    private SearchResultsDBManager searchResultsDBManager;
    private StatsDBManager statsDBManager;

    private DatabaseManager() {
        Realm.init(MyApplication.getAppContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(CURRENT_REALM_VERSION)
                .migration(migration)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        checkInsDBManager = CheckInsDBManager.get();
        restaurantsDBManager = RestaurantsDBManager.get();
        dishesDBManager = DishesDBManager.get();
        searchResultsDBManager = SearchResultsDBManager.get();
        statsDBManager = StatsDBManager.get();
    }

    private RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();

            // Support for dish tagging
            if (oldVersion == 0) {
                RealmObjectSchema checkInSchema = schema.get("CheckInDO");
                RealmObjectSchema dishSchema = schema.get("DishDO");
                if (checkInSchema != null && dishSchema != null) {
                    checkInSchema.addRealmListField("taggedDishes", dishSchema);
                    dishSchema.addField("checkInId", int.class);
                }
                oldVersion++;
            }

            // Support for dish favoriting
            if (oldVersion == 1) {
                RealmObjectSchema dishSchema = schema.get("DishDO");
                if (dishSchema != null) {
                    dishSchema.addField("isFavorited", boolean.class);
                }
                oldVersion++;
            }

            // Drop saved locations feature since it's clunky
            if (oldVersion == 2) {
                schema.remove("SavedLocationDO");
                oldVersion++;
            }

            // Support for restaurant categories
            if (oldVersion == 3) {
                RealmObjectSchema categorySchema = schema.create("RestaurantCategoryDO")
                        .addField("alias", String.class)
                        .addField("title", String.class);

                RealmObjectSchema restaurantSchema = schema.get("RestaurantDO");
                if (restaurantSchema != null) {
                    restaurantSchema.addRealmListField("categories", categorySchema);
                } else {
                    throw new IllegalStateException("RestaurantDO doesn't exist.");
                }
            }
        }
    };

    public CheckInsDBManager getCheckInsDBManager() {
        return checkInsDBManager;
    }

    public RestaurantsDBManager getRestaurantsDBManager() {
        return restaurantsDBManager;
    }

    public DishesDBManager getDishesDBManager() {
        return dishesDBManager;
    }

    public SearchResultsDBManager getSearchResultsDBManager() {
        return searchResultsDBManager;
    }

    public StatsDBManager getStatsDBManager() {
        return statsDBManager;
    }
}
