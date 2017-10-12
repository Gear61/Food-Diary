package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.UIUtils;

public class DishOptionsPresenter {

    public interface Listener {
        void onDishDeleted(Dish dish);

        void editDish(Dish dish);
    }

    private Listener mListener;
    private MaterialDialog mOptionsDialog;
    private MaterialDialog mConfirmDeleteDialog;
    private Dish mDish;

    public DishOptionsPresenter(Listener listener, Context context) {
        mListener = listener;
        mOptionsDialog = new MaterialDialog.Builder(context)
                .items(R.array.dish_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                mListener.editDish(mDish);
                                break;
                            case 1:
                                if (!mDish.isFavorited()) {
                                    mConfirmDeleteDialog.show();
                                } else {
                                    UIUtils.showToast(R.string.favorited_dishes_protected, Toast.LENGTH_LONG);
                                }
                                break;
                        }
                    }
                })
                .build();

        mConfirmDeleteDialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_dish_delete_title)
                .content(R.string.confirm_dish_delete)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getDishesDBManager().deleteDish(mDish);
                        mListener.onDishDeleted(mDish);
                    }
                })
                .build();
    }

    public void showOptions(Dish dish) {
        mDish = dish;
        mOptionsDialog.show();
    }
}
