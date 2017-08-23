package com.randomappsinc.foodjournal.Persistence;

import com.randomappsinc.foodjournal.Models.Restaurant;
import com.randomappsinc.foodjournal.Persistence.Models.RestaurantDO;

public class DBConverter {
    public static Restaurant getRestaurantFromDO(RestaurantDO restaurantDO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantDO.getId());
        restaurant.setName(restaurantDO.getName());
        restaurant.setImageUrl(restaurantDO.getImageUrl());
        restaurant.setPhoneNumber(restaurantDO.getPhoneNumber());
        restaurant.setCity(restaurantDO.getCity());
        restaurant.setZipCode(restaurantDO.getZipCode());
        restaurant.setCountry(restaurantDO.getCountry());
        restaurant.setState(restaurantDO.getState());
        restaurant.setAddress(restaurantDO.getAddress());
        return restaurant;
    }
}
