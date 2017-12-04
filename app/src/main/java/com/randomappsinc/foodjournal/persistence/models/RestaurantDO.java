package com.randomappsinc.foodjournal.persistence.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RestaurantDO extends RealmObject {

    @PrimaryKey
    private String id;

    private String name;
    private String imageUrl;
    private String phoneNumber;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private String address;
    private double latitude;
    private double longitude;
    private long timeAdded;
    private RealmList<DishDO> dishes;
    private RealmList<CheckInDO> checkIns;
    private RealmList<RestaurantCategoryDO> categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public RealmList<DishDO> getDishes() {
        return dishes;
    }

    public void setDishes(RealmList<DishDO> dishes) {
        this.dishes = dishes;
    }

    public RealmList<CheckInDO> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(RealmList<CheckInDO> checkIns) {
        this.checkIns = checkIns;
    }

    public RealmList<RestaurantCategoryDO> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<RestaurantCategoryDO> categories) {
        this.categories = categories;
    }
}
