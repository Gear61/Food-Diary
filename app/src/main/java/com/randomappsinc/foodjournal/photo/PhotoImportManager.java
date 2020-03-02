package com.randomappsinc.foodjournal.photo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PictureUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Utility class to take photos via a camera intent or pick a photo from storage.
 * Also responsible for the necessary post-processing (undoing rotation).
 */
public class PhotoImportManager {

    public interface Listener {
        void onAddPhotoFailure();

        void onAddPhotoSuccess(Uri takenPhotoUri);
    }

    private Listener listener;
    private Handler backgroundHandler;
    private @Nullable Uri currentPhotoUri;

    public PhotoImportManager(Listener listener) {
        this.listener = listener;
        HandlerThread handlerThread = new HandlerThread("Photo Processor");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    @Nullable
    public Intent getPhotoTakingIntent(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) == null) {
            return null;
        }
        File currentPhotoFile = PictureUtils.createImageFile();
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
                currentPhotoUri = PictureUtils.processImage(context, currentPhotoUri, true);
                if (currentPhotoUri == null) {
                    listener.onAddPhotoFailure();
                } else {
                    listener.onAddPhotoSuccess(currentPhotoUri);
                }
            } catch (IOException exception) {
                listener.onAddPhotoFailure();
            }
        });
    }

    public void processSelectedPhoto(Context context, Intent data) {
        if (data != null && data.getData() != null) {
            backgroundHandler.post(() -> {
                data.getData();

                // Persist ability to read from this file
                int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                context.getContentResolver().takePersistableUriPermission(data.getData(), takeFlags);

                try {
                    currentPhotoUri = PictureUtils.processImage(context, data.getData(), false);
                    if (currentPhotoUri == null) {
                        listener.onAddPhotoFailure();
                    } else {
                        listener.onAddPhotoSuccess(currentPhotoUri);
                    }
                } catch (IOException exception) {
                    listener.onAddPhotoFailure();
                }
            });
        } else {
            listener.onAddPhotoFailure();
        }
    }

    public void deleteLastTakenPhoto() {
        if (currentPhotoUri != null) {
            PictureUtils.deleteFileWithUri(currentPhotoUri.toString());
        }
    }
}
