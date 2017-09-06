package com.randomappsinc.foodjournal.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.randomappsinc.foodjournal.R;

public class UIUtils {

    public static void showSnackbar(View parent, String message) {
        Context context = MyApplication.getAppContext();
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_red));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void loadBottomNavIcon(BottomNavigationView bottomView, int itemId, Icon icon, Context context) {
        loadMenuIcon(bottomView.getMenu(), itemId, icon, context, R.color.dark_gray);
    }

    public static void loadActionBarIcon(Menu menu, int itemId, Icon icon, Context context) {
        loadMenuIcon(menu, itemId, icon, context, R.color.white);
    }

    public static void loadMenuIcon(Menu menu, int itemId, Icon icon, Context context, @ColorRes int colorId) {
        menu.findItem(itemId).setIcon(
                new IconDrawable(context, icon)
                        .colorRes(colorId)
                        .actionBarSize());
    }
}
