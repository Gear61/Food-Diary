package com.randomappsinc.foodjournal.persistence.dbmanagers;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.SearchResults;
import com.randomappsinc.foodjournal.persistence.DBConverter;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class SearchResultsDBManager {

    public interface Listener {
        void onSearchComplete(SearchResults searchResults);
    }

    private static SearchResultsDBManager instance;

    public static SearchResultsDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized SearchResultsDBManager getSync() {
        if (instance == null) {
            instance = new SearchResultsDBManager();
        }
        return instance;
    }

    private final RealmChangeListener<RealmResults<DishDO>> dishListener =
            new RealmChangeListener<RealmResults<DishDO>>() {
                @Override
                public void onChange(@NonNull RealmResults<DishDO> dishDOs) {
                    List<Dish> dishes = new ArrayList<>();
                    for (DishDO dishDO : dishDOs) {
                        dishes.add(DBConverter.getDishFromDO(dishDO));
                    }
                    searchResults.setDishes(dishes);
                    if (searchResults.isComplete()) {
                        listener.onSearchComplete(searchResults);
                    }
                }
            };

    private final RealmChangeListener<RealmResults<RestaurantDO>> restaurantListener =
            new RealmChangeListener<RealmResults<RestaurantDO>>() {
                @Override
                public void onChange(@NonNull RealmResults<RestaurantDO> restaurantDOs) {
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (RestaurantDO restaurantDO : restaurantDOs) {
                        restaurants.add(DBConverter.getRestaurantFromDO(restaurantDO));
                    }
                    searchResults.setRestaurants(restaurants);
                    if (searchResults.isComplete()) {
                        listener.onSearchComplete(searchResults);
                    }
                }
            };

    private final RealmChangeListener<RealmResults<CheckInDO>> checkInListener =
            new RealmChangeListener<RealmResults<CheckInDO>>() {
                @Override
                public void onChange(@NonNull RealmResults<CheckInDO> checkInDOs) {
                    List<CheckIn> checkIns = new ArrayList<>();
                    for (CheckInDO checkInDO : checkInDOs) {
                        checkIns.add(DBConverter.getCheckInFromDO(checkInDO));
                    }
                    searchResults.setCheckIns(checkIns);
                    if (searchResults.isComplete()) {
                        listener.onSearchComplete(searchResults);
                    }
                }
            };

    private Listener listener;
    private SearchResults searchResults;
    private RealmResults<DishDO> dishQuery;
    private RealmResults<RestaurantDO> restaurantQuery;
    private RealmResults<CheckInDO> checkInQuery;

    private SearchResultsDBManager() {
        searchResults = new SearchResults();
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void unregisterListener() {
        cancelSearches();
        this.listener = null;
    }

    public void doSearch(String searchTerm) {
        cancelSearches();
        searchResults.reset();

        String[] dishFieldsToSort = {"timeAdded", "id"};
        Sort[] dishSorts = {Sort.DESCENDING, Sort.DESCENDING};
        if (searchTerm.isEmpty()) {
            dishQuery = getRealm()
                    .where(DishDO.class)
                    .findAllSortedAsync(dishFieldsToSort, dishSorts);

            restaurantQuery = getRealm()
                    .where(RestaurantDO.class)
                    .findAllSortedAsync("timeAdded", Sort.DESCENDING);

            checkInQuery = getRealm()
                    .where(CheckInDO.class)
                    .findAllSortedAsync("timeAdded", Sort.DESCENDING);
        } else {
            dishQuery = getRealm()
                    .where(DishDO.class)
                    .beginGroup()
                        .contains("title", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("description", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("restaurantName", searchTerm, Case.INSENSITIVE)
                    .endGroup()
                    .findAllSortedAsync(dishFieldsToSort, dishSorts);;

            restaurantQuery = getRealm()
                    .where(RestaurantDO.class)
                    .beginGroup()
                        .contains("name", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("city", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("address", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("categories.title", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("dishes.title", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("dishes.description", searchTerm, Case.INSENSITIVE)
                    .endGroup()
                    .findAllSortedAsync("timeAdded", Sort.DESCENDING);

            checkInQuery = getRealm()
                    .where(CheckInDO.class)
                    .beginGroup()
                        .contains("message", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("restaurantName", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("taggedDishes.title", searchTerm, Case.INSENSITIVE)
                        .or()
                        .contains("taggedDishes.description", searchTerm, Case.INSENSITIVE)
                    .endGroup()
                    .findAllSortedAsync("timeAdded", Sort.DESCENDING);
        }

        dishQuery.addChangeListener(dishListener);
        restaurantQuery.addChangeListener(restaurantListener);
        checkInQuery.addChangeListener(checkInListener);
    }

    private void cancelSearches() {
        if (dishQuery != null) {
            dishQuery.removeAllChangeListeners();
        }
        if (restaurantQuery != null) {
            restaurantQuery.removeAllChangeListeners();
        }
        if (checkInQuery != null) {
            checkInQuery.removeAllChangeListeners();
        }
    }
}
