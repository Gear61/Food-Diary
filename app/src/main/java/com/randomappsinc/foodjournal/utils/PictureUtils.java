package com.randomappsinc.foodjournal.utils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PictureUtils {

    public static File createImageFile() {
        File imageFile;
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "FOOD_JOURNAL_" + timeStamp + "_";
            File storageDir = MyApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException exception) {
            return null;
        }
        return imageFile;
    }

    public static File copyGalleryImage(Intent data) {
        Uri selectedImage = data.getData();
        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(
                selectedImage,
                new String[] {android.provider.MediaStore.Images.ImageColumns.DATA},
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String selectedImagePath = cursor.getString(idx);
        cursor.close();

        File originalFile = new File(selectedImagePath);
        File newFile = createImageFile();

        if (newFile == null) {
            return null;
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(originalFile).getChannel();
            destination = new FileOutputStream(newFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } catch (Exception ignored) {}
        }

        return newFile;
    }
}
