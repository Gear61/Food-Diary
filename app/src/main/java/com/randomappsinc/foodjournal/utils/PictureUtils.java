package com.randomappsinc.foodjournal.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PictureUtils {

    private static final int MAX_IMAGE_HEIGHT = 1024;
    private static final int MAX_IMAGE_WIDTH = 1024;

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

    public static void deleteFileWithUri(@Nullable String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }

        String filePath = uri.substring(uri.lastIndexOf('/'));
        String completePath = Environment.getExternalStorageDirectory().getPath()
                + Constants.FILE_PROVIDER_PATH
                + filePath;
        File imageFile = new File(completePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

    private static void copyFromUriIntoFile(ContentResolver contentResolver, Uri sourceUri, Uri targetUri) {
        try (InputStream inputStream = contentResolver.openInputStream(sourceUri);
             OutputStream outputStream = contentResolver.openOutputStream(targetUri)) {
            if (inputStream == null || outputStream == null) {
                return;
            }
            byte[] buf = new byte[1024];
            if (inputStream.read(buf) <= 0) {
                return;
            }
            do {
                outputStream.write(buf);
            } while (inputStream.read(buf) != -1);
        } catch (IOException ignored) {}
    }

    @Nullable
    public static Uri processImage(Context context, Uri takenPhotoUri, boolean fromCamera)
            throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream input = contentResolver.openInputStream(takenPhotoUri);
        if (input == null) {
            return null;
        }
        ExifInterface exifInterface = new ExifInterface(input);
        int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Bitmap rotatedBitmap;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(context, takenPhotoUri,90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(context, takenPhotoUri,180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(context, takenPhotoUri,270);
                break;
            default:
                // If photo was taken with camera and no rotation needed, just return it
                if (fromCamera) {
                    return takenPhotoUri;
                }
                // Otherwise, we need to copy the contents of the passed in URI into a file we control
                File photoFile = PictureUtils.createImageFile();
                if (photoFile == null) {
                    return null;
                }
                Uri targetUri = FileProvider.getUriForFile(context, Constants.FILE_PROVIDER_AUTHORITY, photoFile);
                PictureUtils.copyFromUriIntoFile(contentResolver, takenPhotoUri, targetUri);
                return takenPhotoUri;
        }

        // If rotation was necessary, write rotated bitmap into a new file and return the URI for that file
        if (rotatedBitmap != null) {
            File photoFile = PictureUtils.createImageFile();
            if (photoFile == null) {
                return null;
            }
            FileOutputStream out = new FileOutputStream(photoFile);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return FileProvider.getUriForFile(context, Constants.FILE_PROVIDER_AUTHORITY, photoFile);
        }

        return null;
    }

    @Nullable
    private static Bitmap getBitmapFromFileProviderUri(Context context, Uri takenPhotoUri) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream imageStream = contentResolver.openInputStream(takenPhotoUri);
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);

            // Decode bitmap with inSampleSize set, capping it at 1024x1024 and decreasing chances of OOM
            options.inJustDecodeBounds = false;
            imageStream = contentResolver.openInputStream(takenPhotoUri);
            return BitmapFactory.decodeStream(imageStream, null, options);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImage(Context context, Uri takenPhotoUri, int degree) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = getBitmapFromFileProviderUri(context, takenPhotoUri);
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotatedImg;
    }
}
