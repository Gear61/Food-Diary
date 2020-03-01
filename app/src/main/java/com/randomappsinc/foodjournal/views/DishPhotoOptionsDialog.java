package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;

public class DishPhotoOptionsDialog {

    public interface Listener {
        void onShowFullPhoto();

        void onRetakePhoto();

        void onReuploadPhoto();
    }

    private @NonNull Listener listener;
    private MaterialDialog dialog;

    public DishPhotoOptionsDialog(@NonNull final Listener listener, Context context) {
        this.listener = listener;
        this.dialog = new MaterialDialog.Builder(context)
                .items(R.array.dish_photo_options)
                .itemsCallback(dishPhotoOptionListener)
                .build();
    }

    private final MaterialDialog.ListCallback dishPhotoOptionListener = new MaterialDialog.ListCallback() {
        @Override
        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
            switch(position) {
                case 0:
                    listener.onShowFullPhoto();
                    break;
                case 1:
                    listener.onRetakePhoto();
                    break;
                case 2:
                    listener.onReuploadPhoto();
                    break;
            }
        }
    };

    public void show() {
        dialog.show();
    }
}
