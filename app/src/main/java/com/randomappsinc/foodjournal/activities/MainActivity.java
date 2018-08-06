package com.randomappsinc.foodjournal.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.HomepageFragmentController;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.BottomNavigationView;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends StandardActivity {

    @BindView(R.id.bottom_navigation) View bottomNavigation;
    @BindString(R.string.choose_image_from) String chooseImageFrom;

    private final BottomNavigationView.Listener bottomNavListener = new BottomNavigationView.Listener() {
        @Override
        public void onNavItemSelected(@IdRes int viewId) {
            UIUtils.hideKeyboard(MainActivity.this);
            navigationController.onNavItemSelected(viewId);
        }

        @Override
        public void takePicture() {
            addWithCamera();
        }
    };

    private BottomNavigationView bottomNavigationView;
    private HomepageFragmentController navigationController;
    private Uri takenPhotoUri;

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
        bottomNavigationView = new BottomNavigationView(bottomNavigation, bottomNavListener);
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                            UIUtils.showToast(R.string.play_store_error, Toast.LENGTH_LONG);
                            return;
                        }
                        startActivity(intent);
                    }
                })
                .show();
    }

    /** Starts the flow to add a dish via the camera */
    private void addWithCamera() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA)) {
            startCameraPage();
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.CAMERA);
        }
    }

    private void startCameraPage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            return;
        }

        File photoFile = PictureUtils.createImageFile();
        if (photoFile != null) {
            takenPhotoUri = FileProvider.getUriForFile(this,
                    "com.randomappsinc.foodjournal.fileprovider",
                    photoFile);

            // Grant access to content URI so camera app doesn't crash
            List<ResolveInfo> resolvedIntentActivities = getPackageManager()
                    .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                grantUriPermission(
                        packageName,
                        takenPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takenPhotoUri);
            startActivityForResult(takePictureIntent, Constants.CAMERA_CODE);
        } else {
            UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Constants.CAMERA_CODE) {
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            // Returning from picture taking
            revokeUriPermission(
                    takenPhotoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent cameraIntent = new Intent(this, DishFormActivity.class);
            cameraIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
            cameraIntent.putExtra(DishFormActivity.URI_KEY, takenPhotoUri.toString());
            startActivityForResult(cameraIntent, 1);
        }

        if (resultCode == Constants.DISH_ADDED) {
            // Tab to dishes fragment and have the list pull in the new dish
            bottomNavigationView.onHomeClicked();
            navigationController.refreshHomepageWithAddedDish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Camera permission granted
        startCameraPage();
    }
}
