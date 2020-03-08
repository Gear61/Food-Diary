package com.randomappsinc.foodjournal.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.randomappsinc.foodjournal.R;

public class UIUtils {

    public static void showSnackbar(View parent, String message) {
        Context context = parent.getContext();
        SpannableStringBuilder spannableString = new SpannableStringBuilder(message);
        spannableString.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                0,
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Snackbar snackbar = Snackbar.make(parent, spannableString, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        rootView.setBackgroundColor(context.getResources().getColor(R.color.app_red));
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

    public static void loadActionBarIcon(Menu menu, int itemId, Icon icon, Context context) {
        loadMenuIcon(menu, itemId, icon, context, R.color.white);
    }

    private static void loadMenuIcon(Menu menu, int itemId, Icon icon, Context context, @ColorRes int colorId) {
        menu.findItem(itemId).setIcon(
                new IconDrawable(context, icon)
                        .colorRes(colorId)
                        .actionBarSize());
    }

    public static void animateFavoriteToggle(final TextView favoriteToggle, final boolean isFavorited) {
        Context context = MyApplication.getAppContext();
        final int animLength = context.getResources().getInteger(R.integer.shorter_anim_length);
        final int lightRed = context.getResources().getColor(R.color.light_red);
        final int darkGray = context.getResources().getColor(R.color.dark_gray);

        if (favoriteToggle.getAnimation() == null || favoriteToggle.getAnimation().hasEnded()) {
            ObjectAnimator animX = ObjectAnimator.ofFloat(favoriteToggle, "scaleX", 0.75f);
            ObjectAnimator animY = ObjectAnimator.ofFloat(favoriteToggle, "scaleY", 0.75f);
            AnimatorSet shrink = new AnimatorSet();
            shrink.playTogether(animX, animY);
            shrink.setDuration(animLength);
            shrink.setInterpolator(new AccelerateInterpolator());
            shrink.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    favoriteToggle.setText(isFavorited ? R.string.heart_filled_icon : R.string.heart_icon);
                    favoriteToggle.setTextColor(isFavorited ? lightRed : darkGray);

                    ObjectAnimator animX = ObjectAnimator.ofFloat(favoriteToggle, "scaleX", 1.0f);
                    ObjectAnimator animY = ObjectAnimator.ofFloat(favoriteToggle, "scaleY", 1.0f);
                    AnimatorSet grow = new AnimatorSet();
                    grow.playTogether(animX, animY);
                    grow.setDuration(animLength);
                    grow.setInterpolator(new AnticipateOvershootInterpolator());
                    grow.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            shrink.start();
        }
    }

    @Deprecated
    public static void showLongToast(@StringRes int stringId) {
        Toast.makeText(MyApplication.getAppContext(), stringId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(@StringRes int stringId, Context context) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
    }

    public static void showToast(@StringRes int stringId, int length) {
        Toast.makeText(MyApplication.getAppContext(), stringId, length).show();
    }
}
