package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

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
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch(which) {
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
                })
                .build();
    }

    public void show() {
        dialog.show();
    }
}
