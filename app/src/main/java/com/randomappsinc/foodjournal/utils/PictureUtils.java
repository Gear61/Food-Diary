package com.randomappsinc.foodjournal.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                    imageFileName,
                    ".jpg",
                    storageDir);
        } catch (IOException exception) {
            return null;
        }
        return imageFile;
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
