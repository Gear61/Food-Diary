package com.randomappsinc.foodjournal.utils;

import android.text.TextUtils;

public class StringUtils {

    /** Safe way to compare strings which accounts for nulls */
    public static boolean compareStrings(String first, String second) {
        if (first == null) {
            return second == null;
        }
        // Only way it can be true at this point is if second isn't null and they're equal
        return second != null && first.trim().equals(second.trim());
    }

    public static String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder capitalizedVersion = new StringBuilder();
        for (String word : words) {
            if (TextUtils.isEmpty(word)) {
                continue;
            }

            String first = word.substring(0, 1);
            String restOfWord = word.substring(1);
            if (capitalizedVersion.length() > 0) {
                capitalizedVersion.append(" ");
            }
            capitalizedVersion
                    .append(first.toUpperCase())
                    .append(restOfWord);
        }
        return capitalizedVersion.toString();
    }
}
