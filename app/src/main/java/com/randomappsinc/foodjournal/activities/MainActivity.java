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
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.DishesFragment;
import com.randomappsinc.foodjournal.fragments.HomepageFragmentController;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.views.BottomNavigationView;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends StandardActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int FILES_PERMISSION_REQUEST = 2;

    // Requesting picture of dish
    public static final int CAMERA_SOURCE = 1;
    public static final int FILES_SOURCE = 2;

    // Opening dish form
    public static final int DISH_FORM_ADD = 3;


    @BindView(R.id.bottom_navigation) View mBottomNavigation;
    @BindString(R.string.choose_image_from) String mChooseImageFrom;

    private final BottomNavigationView.Listener mListener = new BottomNavigationView.Listener() {
        @Override
        public void onNavItemSelected(@IdRes int viewId) {
            mNavigationController.onNavItemSelected(viewId);
        }

        @Override
        public void takePicture() {
            addWithCamera();
        }
    };

    private BottomNavigationView mBottomNavigationView;
    private HomepageFragmentController mNavigationController;
    private File mTakenPhotoFile;
    private Uri mTakenPhotoUri;

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
        mNavigationController.loadHome();

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

    /** Starts the flow to add a dish via the camera */
    private void addWithCamera() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA)) {
            startCameraPage();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.CAMERA,
                    CAMERA_PERMISSION_REQUEST);
        }
    }

    private void startCameraPage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mTakenPhotoFile = PictureUtils.createImageFile();
            if (mTakenPhotoFile != null) {
                mTakenPhotoUri = FileProvider.getUriForFile(this,
                        "com.randomappsinc.foodjournal.fileprovider",
                        mTakenPhotoFile);

                // Grant access to content URI so camera app doesn't crash
                List<ResolveInfo> resolvedIntentActivities = getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    grantUriPermission(
                            packageName,
                            mTakenPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakenPhotoUri);
                startActivityForResult(takePictureIntent, CAMERA_SOURCE);
            } else {
                showToast(R.string.image_file_failed);
            }
        }
    }

    /** Starts the flow to add a dish via uploading from gallery */
    private void addWithGallery() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openFilePicker();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    FILES_PERMISSION_REQUEST);
        }
    }

    private void openFilePicker() {
        Intent getIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Intent chooserIntent = Intent.createChooser(getIntent, mChooseImageFrom);
        startActivityForResult(chooserIntent, FILES_SOURCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_SOURCE:
                    // Revoke external access to content URI
                    revokeUriPermission(
                            mTakenPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent cameraIntent = new Intent(this, DishFormActivity.class);
                    cameraIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
                    cameraIntent.putExtra(DishFormActivity.URI_KEY, mTakenPhotoUri.toString());
                    startActivityForResult(cameraIntent, DISH_FORM_ADD);
                    break;
                case FILES_SOURCE:
                    mTakenPhotoFile = PictureUtils.copyGalleryImage(data);
                    if (mTakenPhotoFile == null) {
                        showToast(R.string.image_file_failed);
                        return;
                    }

                    Uri fileUri = Uri.fromFile(mTakenPhotoFile);
                    String imageUri = fileUri.toString();

                    Intent filesIntent = new Intent(this, DishFormActivity.class);
                    filesIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
                    filesIntent.putExtra(DishFormActivity.URI_KEY, imageUri);
                    startActivityForResult(filesIntent, DISH_FORM_ADD);
                    break;
            }
        } else if (requestCode == DISH_FORM_ADD && resultCode == RESULT_CANCELED) {
            mTakenPhotoFile.delete();
        }

        if (resultCode == DishesFragment.DISH_ADDED) {
            // Tab to dishes fragment and have the list pull in the new dish
            mBottomNavigationView.onHomeClicked();
            mNavigationController.refreshHomepageWithAddedDish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CAMERA_PERMISSION_REQUEST:
                    startCameraPage();
                    break;
                case FILES_PERMISSION_REQUEST:
                    openFilePicker();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    break;
            }
        }
    }
}
