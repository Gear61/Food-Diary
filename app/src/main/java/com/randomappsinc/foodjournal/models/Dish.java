package com.randomappsinc.foodjournal.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.persistence.models.DishDO;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.MyApplication;
import com.randomappsinc.foodjournal.utils.StringUtils;

public class Dish implements Parcelable {

    private int mId;
    private String mUriString;
    private String mTitle;
    private int mRating;
    private String mDescription;
    private long mTimeAdded;
    private long mTimeLastUpdated;
    private String mRestaurantId;
    private String mRestaurantName;
    private int mCheckInId;
    private boolean mIsFavorited;

    public Dish() {}

    public Dish(Dish other) {
        mId = other.getId();
        mUriString = other.getUriString();
        mTitle = other.getTitle();
        mRating = other.getRating();
        mDescription = other.getDescription();
        mTimeAdded = other.getTimeAdded();
        mTimeLastUpdated = other.getTimeLastUpdated();
        mRestaurantId = other.getRestaurantId();
        mRestaurantName = other.getRestaurantName();
        mCheckInId = other.getCheckInId();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUriString() {
        return mUriString;
    }

    public void setUriString(String uriString) {
        mUriString = uriString;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getTimeAdded() {
        return mTimeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        mTimeAdded = timeAdded;
    }

    public long getTimeLastUpdated() {
        return mTimeLastUpdated;
    }

    public void setTimeLastUpdated(long timeLastUpdated) {
        mTimeLastUpdated = timeLastUpdated;
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

    public int getCheckInId() {
        return mCheckInId;
    }

    public void setCheckInId(int checkInId) {
        mCheckInId = checkInId;
    }

    public boolean isFavorited() {
        return mIsFavorited;
    }

    public void setIsFavorited(boolean isFavorited) {
        mIsFavorited = isFavorited;
    }

    public String getDishInfoText(boolean showRestaurantLink) {
        String template = MyApplication.getAppContext().getString(R.string.dish_title);
        if (showRestaurantLink) {
            String restaurantLink = "<a href=\"" + Constants.RESTAURANT_VIEW_INTENT + mRestaurantId + "\">"
                    + mRestaurantName + "</a>";
            System.out.println("Link: " + restaurantLink);
            return String.format(template, mTitle, restaurantLink);
        } else {
            return String.format(template, mTitle, mRestaurantName);
        }
    }

    public String getRatingText() {
        Context context = MyApplication.getAppContext();
        StringBuilder ratingText = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < mRating) {
                ratingText.append(context.getString(R.string.red_filled_star));
            } else {
                ratingText.append(context.getString(R.string.blank_star));
            }
        }
        return ratingText.toString();
    }

    public String getFeedDescription() {
        return "\"" + mDescription + "\"";
    }

    public DishDO toDishDO() {
        DishDO dishDO = new DishDO();
        dishDO.setId(mId);
        dishDO.setUriString(mUriString);
        dishDO.setTitle(mTitle);
        dishDO.setRating(mRating);
        dishDO.setDescription(mDescription);
        dishDO.setTimeAdded(mTimeAdded);
        dishDO.setTimeLastUpdated(mTimeLastUpdated);
        dishDO.setRestaurantId(mRestaurantId);
        dishDO.setRestaurantName(mRestaurantName);
        dishDO.setCheckInId(mCheckInId);
        dishDO.setIsFavorited(mIsFavorited);
        return dishDO;
    }

    /** As of now, title, rating, restaurant, time added, and description can change. */
    public boolean hasChangedInForm(Dish other) {
        return !(StringUtils.compareStrings(mTitle, other.getTitle())
                && mRating == other.getRating()
                && StringUtils.compareStrings(mRestaurantId, other.getRestaurantId())
                && mTimeAdded == other.getTimeAdded()
                && StringUtils.compareStrings(mDescription, other.getDescription())
                && StringUtils.compareStrings(mUriString, other.getUriString()));
    }

    @Override
    public boolean equals(Object dish) {
        if (dish == null || !(dish instanceof Dish)) {
            return false;
        }
        if (dish == this) {
            return true;
        }
        Dish otherDish = (Dish) dish;
        return mId == otherDish.getId();
    }

    protected Dish(Parcel in) {
        mId = in.readInt();
        mUriString = in.readString();
        mTitle = in.readString();
        mRating = in.readInt();
        mDescription = in.readString();
        mTimeAdded = in.readLong();
        mTimeLastUpdated = in.readLong();
        mRestaurantId = in.readString();
        mRestaurantName = in.readString();
        mCheckInId = in.readInt();
        mIsFavorited = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mUriString);
        dest.writeString(mTitle);
        dest.writeInt(mRating);
        dest.writeString(mDescription);
        dest.writeLong(mTimeAdded);
        dest.writeLong(mTimeLastUpdated);
        dest.writeString(mRestaurantId);
        dest.writeString(mRestaurantName);
        dest.writeInt(mCheckInId);
        dest.writeByte((byte) (mIsFavorited ? 0x01 : 0x00));
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
