package com.randomappsinc.foodjournal.utils;

import android.os.Environment;
import android.view.View;

import com.randomappsinc.foodjournal.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PictureUtils {

    public static File createImageFile(View parent) {
        File imageFile = null;
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
            UIUtils.showSnackbar(parent, MyApplication.getAppContext().getString(R.string.image_file_failed));
        }
        return imageFile;
    }
}
