package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantCategoryDO;
import com.randomappsinc.foodjournal.persistence.models.RestaurantDO;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

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
    private double latitude;
    private double longitude;
    private long timeAdded;
    private List<Dish> dishes = new ArrayList<>();
    private List<CheckIn> checkIns = new ArrayList<>();
    private List<RestaurantCategory> categories = new ArrayList<>();

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

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<CheckIn> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(List<CheckIn> checkIns) {
        this.checkIns = checkIns;
    }

    public List<RestaurantCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<RestaurantCategory> categories) {
        this.categories = categories;
    }

    public String getCategoriesListText() {
        StringBuilder categoriesList = new StringBuilder();
        for (RestaurantCategory placeCategory : categories) {
            if (categoriesList.length() > 0) {
                categoriesList.append(", ");
            }
            categoriesList.append(placeCategory.getTitle());
        }
        return categoriesList.toString();
    }

    public String getSearchText() {
        return name + ", " + city;
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
        restaurantDO.setLatitude(latitude);
        restaurantDO.setLongitude(longitude);
        restaurantDO.setTimeAdded(timeAdded);

        RealmList<DishDO> dishDOs = new RealmList<>();
        for (Dish dish : dishes) {
            dishDOs.add(dish.toDishDO());
        }
        restaurantDO.setDishes(dishDOs);

        RealmList<CheckInDO> checkInDOs = new RealmList<>();
        for (CheckIn checkIn : checkIns) {
            checkInDOs.add(checkIn.toCheckInDO());
        }
        restaurantDO.setCheckIns(checkInDOs);

        RealmList<RestaurantCategoryDO> categoryDOs = new RealmList<>();
        for (RestaurantCategory category : categories) {
            categoryDOs.add(category.toRestaurantCategoryDO());
        }
        restaurantDO.setCategories(categoryDOs);

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
        latitude = in.readDouble();
        longitude = in.readDouble();
        timeAdded = in.readLong();
        if (in.readByte() == 0x01) {
            dishes = new ArrayList<>();
            in.readList(dishes, Dish.class.getClassLoader());
        } else {
            dishes = null;
        }
        if (in.readByte() == 0x01) {
            checkIns = new ArrayList<>();
            in.readList(checkIns, CheckIn.class.getClassLoader());
        } else {
            checkIns = null;
        }
        if (in.readByte() == 0x01) {
            categories = new ArrayList<>();
            in.readList(categories, RestaurantCategory.class.getClassLoader());
        } else {
            categories = null;
        }
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
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(timeAdded);
        if (dishes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(dishes);
        }
        if (checkIns == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(checkIns);
        }
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
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
