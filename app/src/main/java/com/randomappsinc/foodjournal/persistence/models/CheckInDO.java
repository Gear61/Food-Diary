package com.randomappsinc.foodjournal.persistence.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CheckInDO extends RealmObject {
    @PrimaryKey
    private int checkInId;

    private String message;
    private long timeAdded;

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
}
