package com.randomappsinc.foodjournal.photo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PictureUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/** Utility class to take photos via a camera intent and do the necessary post-processing */
public class PhotoTakerManager {

    public interface Listener {
        void onTakePhotoFailure();

        void onTakePhotoSuccess(Uri takenPhotoUri);
    }

    private Listener listener;
    private Handler backgroundHandler;
    private @Nullable Uri currentPhotoUri;
    private File currentPhotoFile;

    public PhotoTakerManager(Listener listener) {
        this.listener = listener;
        HandlerThread handlerThread = new HandlerThread("Camera Photos Processor");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    @Nullable
    public Intent getPhotoTakingIntent(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) == null) {
            return null;
        }
        currentPhotoFile = PictureUtils.createImageFile();
        if (currentPhotoFile != null) {
            currentPhotoUri = FileProvider.getUriForFile(
                    context,
                    Constants.FILE_PROVIDER_AUTHORITY,
                    currentPhotoFile);

            // Grant access to content URI so camera app doesn't crash
            List<ResolveInfo> resolvedIntentActivities = context.getPackageManager()
                    .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, currentPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            return takePictureIntent;
        }
        return null;
    }

    public void processTakenPhoto(final Context context) {
        if (currentPhotoUri == null) {
            return;
        }

        backgroundHandler.post(() -> {
            try {
                Bitmap bitmap = PictureUtils.rotateImageIfRequired(context, currentPhotoFile, currentPhotoUri);
                if (bitmap == null) {
                    listener.onTakePhotoFailure();
                } else {
                    try (FileOutputStream out = new FileOutputStream(currentPhotoFile)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (final Exception ignored) {}
                    context.revokeUriPermission(
                            currentPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    listener.onTakePhotoSuccess(currentPhotoUri);
                }
            } catch (IOException exception) {
                listener.onTakePhotoFailure();
            }
        });
    }

    public void deleteLastTakenPhoto() {
        if (currentPhotoUri != null) {
            PictureUtils.deleteFileWithUri(currentPhotoUri.toString());
        }
    }
}
