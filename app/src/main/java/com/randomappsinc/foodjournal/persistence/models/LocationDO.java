package com.randomappsinc.foodjournal.persistence.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocationDO extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;
    private String address;
    private boolean isCurrentLocation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }

    public void setIsCurrentLocation(boolean isCurrentLocation) {
        this.isCurrentLocation = isCurrentLocation;
    }
}
