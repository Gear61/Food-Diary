package com.randomappsinc.foodjournal.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.randomappsinc.foodjournal.models.Dish;

import java.io.File;
import java.util.List;

public class DishUtils {

    public static int[] getDishIdList(List<Dish> dishes) {
        int[] dishIds = new int[dishes.size()];
        for (int i = 0; i < dishes.size(); i++) {
            dishIds[i] = dishes.get(i).getId();
        }
        return dishIds;
    }

    public static void sharePhotoWithUri(String imageUri, Activity activity) {
        String filePath = imageUri.substring(imageUri.lastIndexOf('/'));
        String completePath = Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/com.randomappsinc.foodjournal/files/Pictures"
                + filePath;
        File imageFile = new File(completePath);
        if (!imageFile.exists()) {
            return;
        }
        Uri cleanImageUri = FileProvider.getUriForFile(
                activity,
                "com.randomappsinc.foodjournal.fileprovider",
                imageFile);

        Intent shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setStream(cleanImageUri)
                .getIntent();
        shareIntent.setData(cleanImageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(shareIntent);
        }
    }
}
