package com.randomappsinc.foodjournal.views;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.UIUtils;

public class DishOptionsPresenter {

    public interface Listener {
        void shareDish(Dish dish);

        void editDish(Dish dish);

        void onDishDeleted(Dish dish);
    }

    private @NonNull Listener listener;
    private MaterialDialog optionsDialog;
    private MaterialDialog confirmDeletionDialog;
    private Dish dish;

    public DishOptionsPresenter(@NonNull Listener listener, Context context) {
        this.listener = listener;
        this.optionsDialog = new MaterialDialog.Builder(context)
                .items(R.array.dish_options)
                .itemsCallback(dishOptionSelectedCallback)
                .build();

        this.confirmDeletionDialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_dish_delete_title)
                .content(R.string.confirm_dish_delete)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getDishesDBManager().deleteDish(dish);
                        DishOptionsPresenter.this.listener.onDishDeleted(dish);
                    }
                })
                .build();
    }

    private final MaterialDialog.ListCallback dishOptionSelectedCallback = new MaterialDialog.ListCallback() {
        @Override
        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
            switch (position) {
                case 0:
                    listener.shareDish(dish);
                    break;
                case 1:
                    listener.editDish(dish);
                    break;
                case 2:
                    if (!dish.isFavorited()) {
                        confirmDeletionDialog.show();
                    } else {
                        UIUtils.showToast(R.string.favorited_dishes_protected, Toast.LENGTH_LONG);
                    }
                    break;
            }
        }
    };

    public void showOptions(Dish dish) {
        this.dish = dish;
        this.optionsDialog.show();
    }
}
