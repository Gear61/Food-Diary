package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.HomepageFragmentController;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.BottomNavigationView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends StandardActivity implements BottomNavigationView.Listener {

    @BindView(R.id.bottom_navigation) View bottomNavigation;
    @BindString(R.string.choose_image_from) String chooseImageFrom;

    private BottomNavigationView bottomNavigationView;
    private HomepageFragmentController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kill activity if it's above an existing stack due to launcher bug
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigationController = new HomepageFragmentController(getSupportFragmentManager(), R.id.container);
        bottomNavigationView = new BottomNavigationView(bottomNavigation, this);
        navigationController.loadHome();

        if (PreferencesManager.get().shouldAskForRating()) {
            showRatingPrompt();
        }
    }

    private void showRatingPrompt() {
        new MaterialDialog.Builder(this)
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive((dialog, which) -> {
                    Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                        UIUtils.showToast(R.string.play_store_error, Toast.LENGTH_LONG);
                        return;
                    }
                    startActivity(intent);
                })
                .show();
    }

    @Override
    public void onNavItemSelected(int viewId) {
        UIUtils.hideKeyboard(this);
        navigationController.onNavItemSelected(viewId);
    }

    @Override
    public void takePicture() {
        Intent cameraIntent = new Intent(this, DishFormActivity.class)
                .putExtra(DishFormActivity.NEW_DISH_KEY, true)
                .putExtra(DishFormActivity.CAMERA_MODE_KEY, true);
        startActivityForResult(cameraIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.DISH_ADDED) {
            // Tab to dishes fragment and have the list pull in the new dish
            bottomNavigationView.onHomeClicked();
            navigationController.refreshHomepageWithAddedDish();
        }
    }
}
