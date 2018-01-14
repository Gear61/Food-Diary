package com.randomappsinc.foodjournal.models;

import java.util.List;

public class SearchResults {

    private List<Dish> dishes;
    private List<Restaurant> restaurants;
    private List<CheckIn> checkIns;

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public List<CheckIn> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(List<CheckIn> checkIns) {
        this.checkIns = checkIns;
    }

    public boolean isComplete() {
        return dishes != null && restaurants != null && checkIns != null;
    }

    public void reset() {
        dishes = null;
        restaurants = null;
        checkIns = null;
    }
}
