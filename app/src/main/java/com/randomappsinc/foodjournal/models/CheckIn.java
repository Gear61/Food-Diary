package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.utils.TextUtils;

import java.util.ArrayList;

import io.realm.RealmList;

public class CheckIn implements Parcelable {

    private int mCheckInId;
    private String mMessage = "";
    private long mTimeAdded;
    private String mRestaurantId;
    private String mRestaurantName;
    private ArrayList<Dish> mTaggedDishes = new ArrayList<>();

    public CheckIn() {}

    public CheckIn(CheckIn other) {
        mCheckInId = other.getCheckInId();
        mMessage = other.getMessage();
        mTimeAdded = other.getTimeAdded();
        mRestaurantId = other.getRestaurantId();
        mRestaurantName = other.getRestaurantName();
        mTaggedDishes = other.getTaggedDishes();
    }

    public int getCheckInId() {
        return mCheckInId;
    }

    public void setCheckInId(int checkInId) {
        mCheckInId = checkInId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public long getTimeAdded() {
        return mTimeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        mTimeAdded = timeAdded;
    }

    public String getRestaurantId() {
        return mRestaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        mRestaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        mRestaurantName = restaurantName;
    }

    public ArrayList<Dish> getTaggedDishes() {
        return mTaggedDishes;
    }

    public void setTaggedDishes(ArrayList<Dish> taggedDishes) {
        mTaggedDishes = taggedDishes;
        for (Dish taggedDish : mTaggedDishes) {
            taggedDish.setCheckInId(mCheckInId);
        }
    }

    public void addTaggedDish(Dish dish) {
        if (mTaggedDishes != null) {
            mTaggedDishes.add(dish);
        }
    }

    public CheckInDO toCheckInDO() {
        CheckInDO checkInDO = new CheckInDO();
        checkInDO.setCheckInId(mCheckInId);
        checkInDO.setMessage(mMessage);
        checkInDO.setTimeAdded(mTimeAdded);
        checkInDO.setRestaurantId(mRestaurantId);
        checkInDO.setRestaurantName(mRestaurantName);

        RealmList<DishDO> dishDOs = new RealmList<>();
        for (Dish dish : mTaggedDishes) {
            dishDOs.add(dish.toDishDO());
        }
        checkInDO.setTaggedDishes(dishDOs);

        return checkInDO;
    }

    /** Restaurant, time added, experience, and tagged dishes can change */
    public boolean hasChangedFromForm(CheckIn other) {
        if (!TextUtils.compareStrings(mRestaurantId, other.getRestaurantId())) {
            return true;
        }
        if (mTimeAdded != other.getTimeAdded()) {
            return true;
        }
        if (!TextUtils.compareStrings(mMessage, other.getMessage())) {
            return true;
        }
        if (mTaggedDishes.size() != other.getTaggedDishes().size()) {
            return true;
        }
        for (int i = 0; i < mTaggedDishes.size(); i++) {
            if (mTaggedDishes.get(i).getId() != other.getTaggedDishes().get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    protected CheckIn(Parcel in) {
        mCheckInId = in.readInt();
        mMessage = in.readString();
        mTimeAdded = in.readLong();
        mRestaurantId = in.readString();
        mRestaurantName = in.readString();
        if (in.readByte() == 0x01) {
            mTaggedDishes = new ArrayList<>();
            in.readList(mTaggedDishes, Dish.class.getClassLoader());
        } else {
            mTaggedDishes = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCheckInId);
        dest.writeString(mMessage);
        dest.writeLong(mTimeAdded);
        dest.writeString(mRestaurantId);
        dest.writeString(mRestaurantName);
        if (mTaggedDishes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mTaggedDishes);
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
