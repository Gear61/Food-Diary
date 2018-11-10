package com.randomappsinc.foodjournal.utils;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static void deleteFileWithUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            return;
        }

        String filePath = uri.substring(uri.lastIndexOf('/'));
        String completePath = Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/com.randomappsinc.foodjournal/files/Pictures"
                + filePath;
        File imageFile = new File(completePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

    public static boolean copyFromUriIntoFile(ContentResolver contentResolver, Uri sourceUri, Uri targetUri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = contentResolver.openInputStream(sourceUri);
            outputStream = contentResolver.openOutputStream(targetUri);
            if (inputStream == null || outputStream == null) {
                return false;
            }
            byte[] buf = new byte[1024];
            if (inputStream.read(buf) <= 0) {
                return false;
            }
            do {
                outputStream.write(buf);
            } while (inputStream.read(buf) != -1);
        } catch (IOException ignored) {
            return false;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {}
        }
        return true;
    }
}
