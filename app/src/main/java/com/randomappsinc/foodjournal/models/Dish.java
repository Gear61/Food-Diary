package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.persistence.models.DishDO;

public class Dish implements Parcelable {
    private String uriString;
    private String restaurantId;
    private String title;
    private int rating;
    private String description;
    private long timeAdded;

    public Dish() {}

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public DishDO toDishDO() {
        DishDO dishDO = new DishDO();
        dishDO.setUriString(uriString);
        dishDO.setRestaurantId(restaurantId);
        dishDO.setTitle(title);
        dishDO.setRating(rating);
        dishDO.setDescription(description);
        dishDO.setTimeAdded(timeAdded);
        return dishDO;
    }

    protected Dish(Parcel in) {
        uriString = in.readString();
        restaurantId = in.readString();
        title = in.readString();
        rating = in.readInt();
        description = in.readString();
        timeAdded = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uriString);
        dest.writeString(restaurantId);
        dest.writeString(title);
        dest.writeInt(rating);
        dest.writeString(description);
        dest.writeLong(timeAdded);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };
}
