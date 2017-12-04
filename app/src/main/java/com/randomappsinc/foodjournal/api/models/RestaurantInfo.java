package com.randomappsinc.foodjournal.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.RestaurantCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderchiou on 12/3/17.
 */

public class RestaurantInfo {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("display_phone")
    @Expose
    private String phoneNumber;

    @SerializedName("coordinates")
    @Expose
    private Coordinates coordinates;

    class Coordinates {
        @SerializedName("latitude")
        @Expose
        private double latitude;

        @SerializedName("longitude")
        @Expose
        private double longitude;

        double getLatitude() {
            return latitude;
        }

        double getLongitude() {
            return longitude;
        }
    }

    @SerializedName("location")
    @Expose
    private Location location;

    public class Location {
        @SerializedName("city")
        @Expose
        private String city;

        @SerializedName("zip_code")
        @Expose
        private String zipCode;

        @SerializedName("country")
        @Expose
        private String country;

        @SerializedName("state")
        @Expose
        private String state;

        @SerializedName("display_address")
        @Expose
        private List<String> displayAddress;

        public String getCity() {
            return city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCountry() {
            return country;
        }

        public String getState() {
            return state;
        }

        public String getAddress() {
            StringBuilder address = new StringBuilder();
            for (int i = 0; i < displayAddress.size(); i++) {
                if (i > 0) {
                    address.append(", ");
                }
                address.append(displayAddress.get(i));
            }
            return address.toString();
        }
    }

    @SerializedName("categories")
    @Expose
    private List<Category> categories;

    class Category {
        @SerializedName("alias")
        @Expose
        private String alias;

        @SerializedName("title")
        @Expose
        private String title;

        String getAlias() {
            return alias;
        }

        String getTitle() {
            return title;
        }
    }

    public Restaurant getRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setImageUrl(imageUrl);
        restaurant.setPhoneNumber(phoneNumber);
        restaurant.setCity(location.getCity());
        restaurant.setZipCode(location.getZipCode());
        restaurant.setState(location.getState());
        restaurant.setCountry(location.getCountry());
        restaurant.setAddress(location.getAddress());
        restaurant.setLatitude(coordinates.getLatitude());
        restaurant.setLongitude(coordinates.getLongitude());

        List<RestaurantCategory> restaurantCategories = new ArrayList<>();
        for (Category category : categories) {
            RestaurantCategory restaurantCategory = new RestaurantCategory();
            restaurantCategory.setAlias(category.getAlias());
            restaurantCategory.setTitle(category.getTitle());
            restaurantCategories.add(restaurantCategory);
        }
        restaurant.setCategories(restaurantCategories);

        return restaurant;
    }
}
