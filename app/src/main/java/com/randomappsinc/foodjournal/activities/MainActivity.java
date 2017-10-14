package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.HomepageFragmentController;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.views.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends StandardActivity {

    @BindView(R.id.bottom_navigation) View mBottomNavigation;

    private final BottomNavigationView.Listener mListener = new BottomNavigationView.Listener() {
        @Override
        public void onNavItemSelected(@IdRes int viewId) {
            mNavigationController.onNavItemSelected(viewId);
        }

        @Override
        public void takePicture() {
            mNavigationController.takePicture();
        }
    };

    private BottomNavigationView mBottomNavigationView;
    private HomepageFragmentController mNavigationController;

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

        mNavigationController = new HomepageFragmentController(getFragmentManager(), R.id.container);
        mBottomNavigationView = new BottomNavigationView(mBottomNavigation, mListener);

        if (PreferencesManager.get().shouldAskForRating()) {
            showRatingPrompt();
        }
    }

    private void showRatingPrompt() {
        new MaterialDialog.Builder(this)
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                            showToast(R.string.play_store_error);
                            return;
                        }
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void showToast(@StringRes int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mNavigationController.closeAddDishMenu();
        super.startActivityForResult(intent, requestCode);
    }
}
