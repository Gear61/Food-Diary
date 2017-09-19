package com.randomappsinc.foodjournal.persistence.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CheckInDO extends RealmObject {

    @PrimaryKey
    private int checkInId;

    private String message;
    private long timeAdded;
    private String restaurantId;
    private String restaurantName;
    private RealmList<DishDO> taggedDishes;

    public int getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(int checkInId) {
        this.checkInId = checkInId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public RealmList<DishDO> getTaggedDishes() {
        return taggedDishes;
    }

    public void setTaggedDishes(RealmList<DishDO> taggedDishes) {
        this.taggedDishes = taggedDishes;
    }
}
