package com.randomappsinc.foodjournal.utils;

import com.randomappsinc.foodjournal.models.Dish;

import java.util.List;

public class DishUtils {

    public static int[] getDishIdList(List<Dish> dishes) {
        int[] dishIds = new int[dishes.size()];
        for (int i = 0; i < dishes.size(); i++) {
            dishIds[i] = dishes.get(i).getId();
        }
        return dishIds;
    }
}
