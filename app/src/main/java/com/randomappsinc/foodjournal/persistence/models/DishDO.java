package com.randomappsinc.foodjournal.persistence.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DishDO extends RealmObject {

    @PrimaryKey
    private int id;

    private String uriString;
    private String title;
    private int rating;
    private String description;
    private long timeAdded;
    private long timeLastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
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

    public long getTimeLastUpdated() {
        return timeLastUpdated;
    }

    public void setTimeLastUpdated(long timeLastUpdated) {
        this.timeLastUpdated = timeLastUpdated;
    }
}
