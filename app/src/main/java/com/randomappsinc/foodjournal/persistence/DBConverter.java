package com.randomappsinc.foodjournal.persistence;

import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.RestaurantCategory;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantCategoryDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

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
        restaurant.setLatitude(restaurantDO.getLatitude());
        restaurant.setLongitude(restaurantDO.getLongitude());
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

        List<RestaurantCategory> categories = new ArrayList<>();
        for (RestaurantCategoryDO categoryDO : restaurantDO.getCategories()) {
            categories.add(getRestaurantCategoryFromDO(categoryDO));
        }
        restaurant.setCategories(categories);

        return restaurant;
    }

    private static RestaurantCategory getRestaurantCategoryFromDO(RestaurantCategoryDO categoryDO) {
        RestaurantCategory category = new RestaurantCategory();
        category.setAlias(categoryDO.getAlias());
        category.setTitle(categoryDO.getTitle());
        return category;
    }

    public static Dish getDishFromDO(DishDO dishDO) {
        Dish dish = new Dish();
        dish.setId(dishDO.getId());
        dish.setUriString(dishDO.getUriString());
        dish.setTitle(dishDO.getTitle());
        dish.setRating(dishDO.getRating());
        dish.setDescription(dishDO.getDescription());
        dish.setTimeAdded(dishDO.getTimeAdded());
        dish.setTimeLastUpdated(dishDO.getTimeLastUpdated());
        dish.setRestaurantId(dishDO.getRestaurantId());
        dish.setRestaurantName(dishDO.getRestaurantName());
        dish.setCheckInId(dishDO.getCheckInId());
        dish.setIsFavorited(dishDO.isFavorited());
        return dish;
    }

    public static CheckIn getCheckInFromDO(CheckInDO checkInDO) {
        CheckIn checkIn = new CheckIn();
        checkIn.setCheckInId(checkInDO.getCheckInId());
        checkIn.setMessage(checkInDO.getMessage());
        checkIn.setTimeAdded(checkInDO.getTimeAdded());
        checkIn.setRestaurantId(checkInDO.getRestaurantId());
        checkIn.setRestaurantName(checkInDO.getRestaurantName().trim());

        ArrayList<Dish> dishes = new ArrayList<>();
        for (DishDO dishDO : checkInDO.getTaggedDishes()) {
            dishes.add(DBConverter.getDishFromDO(dishDO));
        }
        checkIn.setTaggedDishes(dishes);

        return checkIn;
    }
}
