package com.randomappsinc.foodjournal.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.Persistence.Models.RestaurantDO;

/**
 * Created by alexanderchiou on 8/20/17.
 */

public class Restaurant implements Parcelable {
    private String id;
    private String name;
    private String imageUrl;
    private String phoneNumber;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private String address;

    public Restaurant() {}

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

    public RestaurantDO toRestaurantDO() {
        RestaurantDO restaurantDO = new RestaurantDO();
        restaurantDO.setId(id);
        restaurantDO.setName(name);
        restaurantDO.setImageUrl(imageUrl);
        restaurantDO.setPhoneNumber(phoneNumber);
        restaurantDO.setCity(city);
        restaurantDO.setZipCode(zipCode);
        restaurantDO.setCountry(country);
        restaurantDO.setState(state);
        restaurantDO.setAddress(address);
        return restaurantDO;
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        phoneNumber = in.readString();
        city = in.readString();
        zipCode = in.readString();
        country = in.readString();
        state = in.readString();
        address = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(phoneNumber);
        dest.writeString(city);
        dest.writeString(zipCode);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(address);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
