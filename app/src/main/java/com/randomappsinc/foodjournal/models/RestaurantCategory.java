package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.persistence.models.RestaurantCategoryDO;

public class RestaurantCategory implements Parcelable {

    private String mAlias;
    private String mTitle;

    public RestaurantCategory() {}

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public RestaurantCategoryDO toRestaurantCategoryDO() {
        RestaurantCategoryDO restaurantCategoryDO = new RestaurantCategoryDO();
        restaurantCategoryDO.setAlias(mAlias);
        restaurantCategoryDO.setTitle(mTitle);
        return restaurantCategoryDO;
    }

    protected RestaurantCategory(Parcel in) {
        mAlias = in.readString();
        mTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAlias);
        dest.writeString(mTitle);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RestaurantCategory> CREATOR = new Parcelable.Creator<RestaurantCategory>() {
        @Override
        public RestaurantCategory createFromParcel(Parcel in) {
            return new RestaurantCategory(in);
        }

        @Override
        public RestaurantCategory[] newArray(int size) {
            return new RestaurantCategory[size];
        }
    };
}
