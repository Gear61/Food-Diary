package com.randomappsinc.foodjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.foodjournal.persistence.models.CheckInDO;

public class CheckIn implements Parcelable {

    private int checkInId;
    private String message;
    private long timeAdded;

    public CheckIn() {}

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

    public CheckInDO toCheckInDO() {
        CheckInDO checkInDO = new CheckInDO();
        checkInDO.setCheckInId(checkInId);
        checkInDO.setMessage(message);
        checkInDO.setTimeAdded(timeAdded);
        return checkInDO;
    }

    protected CheckIn(Parcel in) {
        checkInId = in.readInt();
        message = in.readString();
        timeAdded = in.readLong();
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
