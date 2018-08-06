package com.randomappsinc.foodjournal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DateTimeAdder;
import com.randomappsinc.foodjournal.views.DishPhotoOptionsDialog;
import com.randomappsinc.foodjournal.views.RatingView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishFormActivity extends StandardActivity implements DishPhotoOptionsDialog.Listener {

    public static final String NEW_DISH_KEY = "newDish";
    public static final String URI_KEY = "uri";
    public static final String DISH_KEY = "dish";

    // Permission request codes
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int GALLERY_PERMISSION_CODE = 2;

    // Activity request codes
    private static final int CAMERA_SOURCE_CODE = 1;
    private static final int GALLERY_SOURCE_CODE = 2;
    private static final int RESTAURANT_SOURCE_CODE = 3;

    private final DateTimeAdder.Listener mDateTimeListener = new DateTimeAdder.Listener() {
        @Override
        public void onDateTimeChosen(long timeChosen) {
            mDish.setTimeAdded(timeChosen);
            mDateTimeText.setText(TimeUtils.getDefaultTimeText(timeChosen));
        }
    };

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.dish_picture) ImageView mDishPicture;
    @BindView(R.id.rating_widget) View mRatingLayout;
    @BindView(R.id.dish_name_input) EditText mDishNameInput;
    @BindView(R.id.base_restaurant_cell) View mRestaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView mRestaurantName;
    @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
    @BindView(R.id.restaurant_categories) TextView mRestaurantCategories;
    @BindView(R.id.choose_restaurant_prompt) View mChooseRestaurantPrompt;
    @BindView(R.id.date_text) TextView mDateTimeText;
    @BindView(R.id.dish_description_input) EditText mDishDescriptionInput;

    private Dish mDish;
    private Dish mOriginalDish;
    private Restaurant mRestaurant;
    private RatingView mRatingView;
    private MaterialDialog mLeaveDialog;
    private DateTimeAdder mDateTimeAdder;
    private boolean mNewDishMode;
    private DishPhotoOptionsDialog mPhotoOptionsDialog;
    private Uri mTakenPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRatingView = new RatingView(mRatingLayout);
        mDateTimeAdder = new DateTimeAdder(getFragmentManager(), mDateTimeListener);
        mLeaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_dish_exit)
                .content(R.string.confirm_form_exit)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteOldPhoto();
                        finish();
                    }
                })
                .build();

        mNewDishMode = getIntent().getBooleanExtra(NEW_DISH_KEY, false);
        mPhotoOptionsDialog = new DishPhotoOptionsDialog(this, this);

        // Adding a new dish
        if (mNewDishMode) {
            mDish = new Dish();
            mDish.setTimeAdded(System.currentTimeMillis());
            mDateTimeText.setText(TimeUtils.getDefaultTimeText(mDish.getTimeAdded()));

            String pictureUri = getIntent().getStringExtra(URI_KEY);
            mDish.setUriString(pictureUri);

            Restaurant autoFill = DatabaseManager.get().getCheckInsDBManager().getAutoFillRestaurant();
            if (autoFill == null) {
                mRestaurantInfo.setVisibility(View.INVISIBLE);
                mChooseRestaurantPrompt.setVisibility(View.VISIBLE);
            } else {
                mRestaurant = autoFill;
                loadRestaurantInfo();
            }
        }
        // Editing an existing dish
        else {
            mDish = getIntent().getParcelableExtra(DISH_KEY);
            mRestaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(mDish.getRestaurantId());
            loadDishInfo();
        }

        mOriginalDish = new Dish(mDish);
        loadDishPhoto();
    }

    private void loadDishPhoto() {
        Picasso.get()
                .load(mDish.getUriString())
                .fit()
                .centerCrop()
                .into(mDishPicture);
    }

    @OnClick(R.id.dish_picture)
    public void onDishPhotoClicked() {
        mPhotoOptionsDialog.show();
    }

    @Override
    public void onShowFullPhoto() {
        Intent intent = new Intent(this, PictureFullViewActivity.class);
        intent.putExtra(PictureFullViewActivity.IMAGE_PATH_KEY, mDish.getUriString());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void addWithCamera() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA)) {
            startCameraPage();
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        }
    }

    private void startCameraPage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            return;
        }

        File photoFile = PictureUtils.createImageFile();
        if (photoFile != null) {
            mTakenPhotoUri = FileProvider.getUriForFile(this,
                    "com.randomappsinc.foodjournal.fileprovider",
                    photoFile);

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
            startActivityForResult(takePictureIntent, CAMERA_SOURCE_CODE);
        } else {
            UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRetakePhoto() {
        addWithCamera();
    }

    private void addWithGallery() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openFilePicker();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    GALLERY_PERMISSION_CODE);
        }
    }

    private void openFilePicker() {
        Intent getIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_image_from));
        startActivityForResult(chooserIntent, GALLERY_SOURCE_CODE);
    }

    @Override
    public void onReuploadPhoto() {
        addWithGallery();
    }

    private void loadDishInfo() {
        mRatingView.loadRating(mDish.getRating());
        mDishNameInput.setText(mDish.getTitle());
        loadRestaurantInfo();
        mDateTimeText.setText(TimeUtils.getDefaultTimeText(mDish.getTimeAdded()));
        mDishDescriptionInput.setText(mDish.getDescription());
    }

    private void loadRestaurantInfo() {
        mDish.setRestaurantId(mRestaurant.getId());
        mDish.setRestaurantName(mRestaurant.getName());

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!mRestaurant.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(mRestaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(mRestaurantThumbnail);
        } else {
            mRestaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        mRestaurantName.setText(mRestaurant.getName());
        mRestaurantAddress.setText(mRestaurant.getAddress());
        if (mRestaurantInfo.getVisibility() != View.VISIBLE) {
            mRestaurantInfo.setVisibility(View.VISIBLE);
        }
        if (mRestaurant.getCategoriesListText().isEmpty()) {
            mRestaurantCategories.setVisibility(View.GONE);
        } else {
            mRestaurantCategories.setText(mRestaurant.getCategoriesListText());
            mRestaurantCategories.setVisibility(View.VISIBLE);
        }
        if (mChooseRestaurantPrompt.getVisibility() != View.GONE) {
            mChooseRestaurantPrompt.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.restaurant_info_section)
    public void chooseRestaurant() {
        Intent intent = new Intent(this, FindRestaurantActivity.class);
        startActivityForResult(intent, RESTAURANT_SOURCE_CODE);
    }

    @OnClick(R.id.date_text)
    public void selectDate() {
        mDateTimeAdder.show(mDish.getTimeAdded());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case CAMERA_SOURCE_CODE:
                deleteOldPhoto();
                revokeUriPermission(
                        mTakenPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String photoUriString = mTakenPhotoUri.toString();
                mDish.setUriString(photoUriString);
                loadDishPhoto();
                break;
            case GALLERY_SOURCE_CODE:
                deleteOldPhoto();
                File photoFile = PictureUtils.copyGalleryImage(data);
                if (photoFile == null) {
                    UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
                    return;
                }
                String fileUri = Uri.fromFile(photoFile).toString();
                mDish.setUriString(fileUri);
                loadDishPhoto();
                break;
            case RESTAURANT_SOURCE_CODE:
                mRestaurant = data.getParcelableExtra(Constants.RESTAURANT_KEY);
                loadRestaurantInfo();
                break;
        }
    }

    private void deleteOldPhoto() {
        if (mNewDishMode) {
            PictureUtils.deleteFileWithUri(mDish.getUriString());
        } else {
            if (!mOriginalDish.getUriString().equals(mDish.getUriString())) {
                PictureUtils.deleteFileWithUri(mDish.getUriString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                startCameraPage();
                break;
            case GALLERY_PERMISSION_CODE:
                openFilePicker();
                break;
        }
    }

    private void loadFormIntoDish() {
        mDish.setTitle(mDishNameInput.getText().toString().trim());
        mDish.setRating(mRatingView.getRating());
        mDish.setDescription(mDishDescriptionInput.getText().toString().trim());
    }

    @OnClick(R.id.save)
    public void saveDish() {
        String title = mDishNameInput.getText().toString().trim();

        if (title.isEmpty()) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_title_needed));
            return;
        }
        if (mRestaurant == null) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_restaurant_needed));
            return;
        }

        loadFormIntoDish();

        if (mNewDishMode) {
            CheckIn checkIn = DatabaseManager.get().getCheckInsDBManager().getAutoTagCheckIn(mDish);
            if (checkIn == null) {
                String ask = String.format(
                        getString(R.string.auto_create_check_in),
                        mDish.getRestaurantName());
                new MaterialDialog.Builder(this)
                        .cancelable(false)
                        .title(R.string.create_check_in)
                        .content(ask)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mDish.setTimeLastUpdated(System.currentTimeMillis());
                                if (which == DialogAction.POSITIVE) {
                                    DatabaseManager.get().getCheckInsDBManager().autoCreateCheckIn(mDish);
                                } else {
                                    DatabaseManager.get().getDishesDBManager().addDish(mDish);
                                }
                                setResult(Constants.DISH_ADDED);
                                finish();
                            }
                        })
                        .show();
            } else {
                // Auto-tag to recent check-in
                mDish.setTimeLastUpdated(System.currentTimeMillis());
                DatabaseManager.get().getDishesDBManager().addDish(mDish);
                checkIn.addTaggedDish(mDish);
                DatabaseManager.get().getCheckInsDBManager().updateCheckIn(checkIn);
                setResult(Constants.DISH_ADDED);
                finish();
            }
        } else {
            if (!mDish.getUriString().equals(mOriginalDish.getUriString())) {
                PictureUtils.deleteFileWithUri(mOriginalDish.getUriString());
            }

            DatabaseManager.get().getDishesDBManager().updateDish(mDish);
            setResult(Constants.DISH_EDITED);
            finish();
        }
    }

    /** Return true if the confirm exit dialog is shown */
    public boolean confirmExit() {
        loadFormIntoDish();
        if (mNewDishMode || mDish.hasChangedInForm(mOriginalDish)) {
            mLeaveDialog.show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!confirmExit()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (confirmExit()) {
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
