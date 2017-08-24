package com.randomappsinc.foodjournal.Persistence.Models;

import io.realm.RealmObject;

public class CheckInDO extends RealmObject {
    private String message;
    private long timeAdded;

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
