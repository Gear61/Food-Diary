package com.randomappsinc.foodjournal.persistence.dbmanagers;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.RestaurantCategory;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.RestaurantCategoryDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;

public class RestaurantsDBManager {

    private static RestaurantsDBManager instance;

    public static RestaurantsDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized RestaurantsDBManager getSync() {
        if (instance == null) {
            instance = new RestaurantsDBManager();
        }
        return instance;
    }

    public interface Listener {
        void onRestaurantDeleted(String restaurantId);
    }

    private Set<Listener> listeners = new HashSet<>();

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public boolean userAlreadyHasRestaurant(Restaurant restaurant) {
        return getRealm().where(RestaurantDO.class).equalTo("id", restaurant.getId()).findFirst() != null;
    }

    public void addRestaurant(final Restaurant restaurant) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RestaurantDO restaurantDO = restaurant.toRestaurantDO();
                restaurantDO.setTimeAdded(System.currentTimeMillis());
                realm.insert(restaurantDO);
            }
        });
    }

    public void deleteRestaurant(final Restaurant restaurant) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurant.getId())
                .findFirst();

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                restaurantDO.getDishes().deleteAllFromRealm();
                restaurantDO.getCheckIns().deleteAllFromRealm();
                restaurantDO.deleteFromRealm();

                for (Listener listener : listeners) {
                    listener.onRestaurantDeleted(restaurant.getId());
                }
            }
        });
    }

    public Restaurant getRestaurant(String restaurantId) {
        RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", restaurantId)
                .findFirst();
        return DBConverter.getRestaurantFromDO(restaurantDO);
    }

    void tagDishToRestaurant(final Dish dish) {
        final RestaurantDO restaurantDO = getRealm()
                .where(RestaurantDO.class)
                .equalTo("id", dish.getRestaurantId())
                .findFirst();

        if (restaurantDO == null) {
            return;
        }

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                restaurantDO.getDishes().add(dish.toDishDO());
            }
        });
    }

    public void updateRestaurantInfo(final Restaurant restaurant) {
        getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RestaurantDO restaurantDO = getRealm()
                        .where(RestaurantDO.class)
                        .equalTo("id", restaurant.getId())
                        .findFirst();

                if (restaurantDO == null) {
                    return;
                }

                restaurantDO.setName(restaurant.getName());
                restaurantDO.setImageUrl(restaurant.getImageUrl());
                restaurantDO.setPhoneNumber(restaurant.getPhoneNumber());
                restaurantDO.setCity(restaurant.getCity());
                restaurantDO.setZipCode(restaurant.getZipCode());
                restaurantDO.setState(restaurant.getState());
                restaurantDO.setCountry(restaurant.getCountry());
                restaurantDO.setAddress(restaurant.getAddress());
                restaurantDO.setLatitude(restaurant.getLatitude());
                restaurantDO.setLongitude(restaurant.getLongitude());

                RealmList<RestaurantCategoryDO> categories = new RealmList<>();
                for (RestaurantCategory category : restaurant.getCategories()) {
                    RestaurantCategoryDO categoryDO = realm.copyToRealm(category.toRestaurantCategoryDO());
                    categories.add(categoryDO);
                }
                restaurantDO.setCategories(categories);
            }
        });
    }
}
