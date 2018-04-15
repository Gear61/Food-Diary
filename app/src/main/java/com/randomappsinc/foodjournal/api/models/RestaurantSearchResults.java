package com.randomappsinc.foodjournal.api.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.RestaurantCategory;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSearchResults {

    @SerializedName("businesses")
    @Expose
    private List<Business> businesses;

    public class Business {
        @SerializedName("alias")
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

        public class Coordinates {
            @SerializedName("latitude")
            @Expose
            private double latitude;

            @SerializedName("longitude")
            @Expose
            private double longitude;

            public double getLatitude() {
                return latitude;
            }

            public double getLongitude() {
                return longitude;
            }
        }

        @SerializedName("location")
        @Expose
        private Location location;

        public class Location {
            @SerializedName("address1")
            @Expose
            private String address1;

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

            String getCity() {
                return city;
            }

            String getZipCode() {
                return zipCode;
            }

            String getCountry() {
                return country;
            }

            String getState() {
                return state;
            }

            String getAddress() {
                StringBuilder address = new StringBuilder();
                if (!TextUtils.isEmpty(address1)) {
                    address.append(address1).append(", ");
                }
                address.append(city);
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

        Restaurant toRestaurant() {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(id);

            // Trim the name because Yelp API returns it with leading whitespace sometimes for some reason
            restaurant.setName(name.trim());

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

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Business business : businesses) {
            restaurants.add(business.toRestaurant());
        }
        return restaurants;
    }
}
