package com.randomappsinc.foodjournal.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.randomappsinc.foodjournal.Persistence.PreferencesManager;
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

    public static void showAddedSnackbar(final String location, final View parent, final BaseAdapter adapter) {
        final Context context = MyApplication.getAppContext();
        Snackbar snackbar = Snackbar.make(parent, context.getString(R.string.location_added), Snackbar.LENGTH_INDEFINITE);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_red));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setAction(android.R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.get().setCurrentLocation(location);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                showSnackbar(parent, context.getString(R.string.current_location_set));
            }
        });
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

    public static void loadMenuIcon(Menu menu, int itemId, Icon icon, Context context) {
        menu.findItem(itemId).setIcon(
                new IconDrawable(context, icon)
                        .colorRes(R.color.white)
                        .actionBarSize());
    }
}
