package com.randomappsinc.foodjournal.persistence;

import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.SavedLocation;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;
import com.randomappsinc.foodjournal.persistence.models.SavedLocationDO;

import java.util.ArrayList;
import java.util.List;

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
        restaurant.setTimeAdded(restaurantDO.getTimeAdded());

        List<Dish> dishes = new ArrayList<>();
        for (DishDO dishDO : restaurantDO.getDishes()) {
            dishes.add(getDishFromDO(dishDO));
        }
        restaurant.setDishes(dishes);

        List<CheckIn> checkInList = new ArrayList<>();
        for (CheckInDO checkInDO : restaurantDO.getCheckIns()) {
            checkInList.add(getCheckInFromDO(checkInDO));
        }
        restaurant.setCheckIns(checkInList);

        return restaurant;
    }

    public static Dish getDishFromDO(DishDO dishDO) {
        Dish dish = new Dish();
        dish.setId(dishDO.getId());
        dish.setUriString(dishDO.getUriString());
        dish.setRestaurantId(dishDO.getRestaurantId());
        dish.setTitle(dishDO.getTitle());
        dish.setRating(dishDO.getRating());
        dish.setDescription(dishDO.getDescription());
        dish.setTimeAdded(dishDO.getTimeAdded());
        return dish;
    }

    public static CheckIn getCheckInFromDO(CheckInDO checkInDO) {
        CheckIn checkIn = new CheckIn();
        checkIn.setCheckInId(checkInDO.getCheckInId());
        checkIn.setMessage(checkInDO.getMessage());
        checkIn.setTimeAdded(checkInDO.getTimeAdded());
        return checkIn;
    }

    public static SavedLocation getLocationFromDO(SavedLocationDO savedLocationDO) {
        SavedLocation savedLocation = new SavedLocation();
        savedLocation.setId(savedLocationDO.getId());
        savedLocation.setName(savedLocationDO.getName());
        savedLocation.setAddress(savedLocationDO.getAddress());
        savedLocation.setIsCurrentLocation(savedLocationDO.isCurrentLocation());
        return savedLocation;
    }
}
