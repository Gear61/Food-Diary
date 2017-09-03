package com.randomappsinc.foodjournal.persistence.dbmanagers;

public class DishesDBManager {

    private static DishesDBManager instance;

    public static DishesDBManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized DishesDBManager getSync() {
        if (instance == null) {
            instance = new DishesDBManager();
        }
        return instance;
    }
}
