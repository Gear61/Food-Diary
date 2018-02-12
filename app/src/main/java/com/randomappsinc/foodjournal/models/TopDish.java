package com.randomappsinc.foodjournal.models;

import java.util.ArrayList;

public class TopDish {

    private ArrayList<Dish> instances;

    public ArrayList<Dish> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<Dish> instances) {
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
