package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.utils.StringUtils;
import com.randomappsinc.foodjournal.utils.TimeUtils;

import java.util.ArrayList;

import io.realm.RealmList;

public class CheckIn implements Parcelable {

    private int checkInId;
    private String message = "";
    private long timeAdded;
    private String restaurantId;
    private String restaurantName;
    private ArrayList<Dish> taggedDishes = new ArrayList<>();

    public CheckIn() {}

    public CheckIn(CheckIn other) {
        checkInId = other.getCheckInId();
        message = other.getMessage();
        timeAdded = other.getTimeAdded();
        restaurantId = other.getRestaurantId();
        restaurantName = other.getRestaurantName();
        taggedDishes = other.getTaggedDishes();
    }

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

    public ArrayList<Dish> getTaggedDishes() {
        return taggedDishes;
    }

    public void setTaggedDishes(ArrayList<Dish> taggedDishes) {
        this.taggedDishes = taggedDishes;
        for (Dish taggedDish : this.taggedDishes) {
            taggedDish.setCheckInId(checkInId);
        }
    }

    public void addTaggedDish(Dish dish) {
        if (taggedDishes != null) {
            taggedDishes.add(dish);
        }
    }

    public String getSearchText() {
        return restaurantName + ", " + TimeUtils.getCheckInSearchTimeText(timeAdded);
    }

    public CheckInDO toCheckInDO() {
        CheckInDO checkInDO = new CheckInDO();
        checkInDO.setCheckInId(checkInId);
        checkInDO.setMessage(message);
        checkInDO.setTimeAdded(timeAdded);
        checkInDO.setRestaurantId(restaurantId);
        checkInDO.setRestaurantName(restaurantName);

        RealmList<DishDO> dishDOs = new RealmList<>();
        for (Dish dish : taggedDishes) {
            dishDOs.add(dish.toDishDO());
        }
        checkInDO.setTaggedDishes(dishDOs);

        return checkInDO;
    }

    /** Restaurant, time added, experience, and tagged dishes can change */
    public boolean hasChangedFromForm(CheckIn other) {
        if (!StringUtils.compareStrings(restaurantId, other.getRestaurantId())) {
            return true;
        }
        if (timeAdded != other.getTimeAdded()) {
            return true;
        }
        if (!StringUtils.compareStrings(message, other.getMessage())) {
            return true;
        }
        if (taggedDishes.size() != other.getTaggedDishes().size()) {
            return true;
        }
        for (int i = 0; i < taggedDishes.size(); i++) {
            if (taggedDishes.get(i).getId() != other.getTaggedDishes().get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    protected CheckIn(Parcel in) {
        checkInId = in.readInt();
        message = in.readString();
        timeAdded = in.readLong();
        restaurantId = in.readString();
        restaurantName = in.readString();
        if (in.readByte() == 0x01) {
            taggedDishes = new ArrayList<>();
            in.readList(taggedDishes, Dish.class.getClassLoader());
        } else {
            taggedDishes = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(checkInId);
        dest.writeString(message);
        dest.writeLong(timeAdded);
        dest.writeString(restaurantId);
        dest.writeString(restaurantName);
        if (taggedDishes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(taggedDishes);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CheckIn> CREATOR = new Parcelable.Creator<CheckIn>() {
        @Override
        public CheckIn createFromParcel(Parcel in) {
            return new CheckIn(in);
        }

        @Override
        public CheckIn[] newArray(int size) {
            return new CheckIn[size];
        }
    };
}
