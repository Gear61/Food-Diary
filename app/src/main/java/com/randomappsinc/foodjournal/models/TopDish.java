package com.randomappsinc.foodjournal.models;

import java.util.List;

public class TopDish {

    private List<Dish> instances;

    public List<Dish> getInstances() {
        return instances;
    }

    public void setInstances(List<Dish> instances) {
        this.instances = instances;
    }

    public String getThumbnail() {
        return instances.get(0).getUriString();
    }

    public String getDishName() {
        return instances.get(0).getTitle();
    }

    public String getRestaurantName() {
        return instances.get(0).getRestaurantName();
    }

    public int getNumInstances() {
        return instances.size();
    }
}
