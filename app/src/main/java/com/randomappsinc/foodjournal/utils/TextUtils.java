package com.randomappsinc.foodjournal.utils;

public class TextUtils {

    /** Safe way to compare strings which accounts for nulls */
    public static boolean compareStrings(String first, String second) {
        if (first == null) {
            return second == null;
        }
        // Only way it can be true at this point is if second isn't null and they're equal
        return second != null && first.trim().equals(second.trim());
    }
}
