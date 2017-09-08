package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.persistence.models.CheckInDO;
import com.randomappsinc.foodjournal.utils.MyApplication;
import com.randomappsinc.foodjournal.utils.TimeUtils;

public class CheckIn implements Parcelable {

    private int mCheckInId;
    private String mMessage;
    private long mTimeAdded;
    private String mRestaurantId;
    private String mRestaurantName;

    public CheckIn() {}

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

    public String getCheckInMessage(boolean onRestaurantPage) {
        if (onRestaurantPage) {
            return String.format(
                    MyApplication.getAppContext().getString(R.string.check_in_time),
                    TimeUtils.getDateText(mTimeAdded));
        } else {
            return String.format(
                    MyApplication.getAppContext().getString(R.string.check_in_message_homepage),
                    mRestaurantName,
                    TimeUtils.getDateText(mTimeAdded));
        }
    }

    public CheckInDO toCheckInDO() {
        CheckInDO checkInDO = new CheckInDO();
        checkInDO.setCheckInId(mCheckInId);
        checkInDO.setMessage(mMessage);
        checkInDO.setTimeAdded(mTimeAdded);
        checkInDO.setRestaurantId(mRestaurantId);
        checkInDO.setRestaurantName(mRestaurantName);
        return checkInDO;
    }

    protected CheckIn(Parcel in) {
        mCheckInId = in.readInt();
        mMessage = in.readString();
        mTimeAdded = in.readLong();
        mRestaurantId = in.readString();
        mRestaurantName = in.readString();
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
