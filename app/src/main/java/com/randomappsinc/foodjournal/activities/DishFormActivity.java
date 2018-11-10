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
            dish.setTimeAdded(timeChosen);
            dateTimeText.setText(TimeUtils.getDefaultTimeText(timeChosen));
        }
    };

    @BindView(R.id.parent) View parent;
    @BindView(R.id.dish_picture) ImageView dishPicture;
    @BindView(R.id.rating_widget) View ratingLayout;
    @BindView(R.id.dish_name_input) EditText dishNameInput;
    @BindView(R.id.base_restaurant_cell) View restaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView restaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.restaurant_address) TextView restaurantAddress;
    @BindView(R.id.restaurant_categories) TextView restaurantCategories;
    @BindView(R.id.choose_restaurant_prompt) View chooseRestaurantPrompt;
    @BindView(R.id.date_text) TextView dateTimeText;
    @BindView(R.id.dish_description_input) EditText dishDescriptionInput;

    private Dish dish;
    private Dish originalDish;
    private Restaurant restaurant;
    private RatingView ratingView;
    private MaterialDialog leaveDialog;
    private DateTimeAdder dateTimeAdder;
    private boolean newDishMode;
    private DishPhotoOptionsDialog photoOptionsDialog;
    private Uri takenPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ratingView = new RatingView(ratingLayout);
        dateTimeAdder = new DateTimeAdder(getFragmentManager(), mDateTimeListener);
        leaveDialog = new MaterialDialog.Builder(this)
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

        newDishMode = getIntent().getBooleanExtra(NEW_DISH_KEY, false);
        photoOptionsDialog = new DishPhotoOptionsDialog(this, this);

        // Adding a new dish
        if (newDishMode) {
            dish = new Dish();
            dish.setTimeAdded(System.currentTimeMillis());
            dateTimeText.setText(TimeUtils.getDefaultTimeText(dish.getTimeAdded()));

            String pictureUri = getIntent().getStringExtra(URI_KEY);
            dish.setUriString(pictureUri);

            Restaurant autoFill = DatabaseManager.get().getCheckInsDBManager().getAutoFillRestaurant();
            if (autoFill == null) {
                restaurantInfo.setVisibility(View.INVISIBLE);
                chooseRestaurantPrompt.setVisibility(View.VISIBLE);
            } else {
                restaurant = autoFill;
                loadRestaurantInfo();
            }
        }
        // Editing an existing dish
        else {
            dish = getIntent().getParcelableExtra(DISH_KEY);
            restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(dish.getRestaurantId());
            loadDishInfo();
        }

        originalDish = new Dish(dish);
        loadDishPhoto();
    }

    private void loadDishPhoto() {
        Picasso.get()
                .load(dish.getUriString())
                .fit()
                .centerCrop()
                .into(dishPicture);
    }

    @OnClick(R.id.dish_picture)
    public void onDishPhotoClicked() {
        photoOptionsDialog.show();
    }

    @Override
    public void onShowFullPhoto() {
        Intent intent = new Intent(this, PictureFullViewActivity.class);
        intent.putExtra(PictureFullViewActivity.IMAGE_PATH_KEY, dish.getUriString());
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
        ratingView.loadRating(dish.getRating());
        dishNameInput.setText(dish.getTitle());
        loadRestaurantInfo();
        dateTimeText.setText(TimeUtils.getDefaultTimeText(dish.getTimeAdded()));
        dishDescriptionInput.setText(dish.getDescription());
    }

    private void loadRestaurantInfo() {
        dish.setRestaurantId(restaurant.getId());
        dish.setRestaurantName(restaurant.getName());

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!restaurant.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(restaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(restaurantThumbnail);
        } else {
            restaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getAddress());
        if (restaurantInfo.getVisibility() != View.VISIBLE) {
            restaurantInfo.setVisibility(View.VISIBLE);
        }
        if (restaurant.getCategoriesListText().isEmpty()) {
            restaurantCategories.setVisibility(View.GONE);
        } else {
            restaurantCategories.setText(restaurant.getCategoriesListText());
            restaurantCategories.setVisibility(View.VISIBLE);
        }
        if (chooseRestaurantPrompt.getVisibility() != View.GONE) {
            chooseRestaurantPrompt.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.restaurant_info_section)
    public void chooseRestaurant() {
        Intent intent = new Intent(this, FindRestaurantActivity.class);
        startActivityForResult(intent, RESTAURANT_SOURCE_CODE);
    }

    @OnClick(R.id.date_text)
    public void selectDate() {
        dateTimeAdder.show(dish.getTimeAdded());
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
                        takenPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String photoUriString = takenPhotoUri.toString();
                dish.setUriString(photoUriString);
                loadDishPhoto();
                break;
            case GALLERY_SOURCE_CODE:
                deleteOldPhoto();
                File photoFile = PictureUtils.createImageFile();
                if (photoFile == null) {
                    UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
                    return;
                }
                Uri copyUri = FileProvider.getUriForFile(this,
                        "com.randomappsinc.foodjournal.fileprovider",
                        photoFile);
                if (!PictureUtils.copyFromUriIntoFile(getContentResolver(), data.getData(), copyUri)) {
                    UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
                    return;
                }
                dish.setUriString(copyUri.toString());
                loadDishPhoto();
                break;
            case RESTAURANT_SOURCE_CODE:
                restaurant = data.getParcelableExtra(Constants.RESTAURANT_KEY);
                loadRestaurantInfo();
                break;
        }
    }

    private void deleteOldPhoto() {
        if (newDishMode) {
            PictureUtils.deleteFileWithUri(dish.getUriString());
        } else {
            if (!originalDish.getUriString().equals(dish.getUriString())) {
                PictureUtils.deleteFileWithUri(dish.getUriString());
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
        dish.setTitle(dishNameInput.getText().toString().trim());
        dish.setRating(ratingView.getRating());
        dish.setDescription(dishDescriptionInput.getText().toString().trim());
    }

    @OnClick(R.id.save)
    public void saveDish() {
        String title = dishNameInput.getText().toString().trim();

        if (title.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.dish_title_needed));
            return;
        }
        if (restaurant == null) {
            UIUtils.showSnackbar(parent, getString(R.string.dish_restaurant_needed));
            return;
        }

        loadFormIntoDish();

        if (newDishMode) {
            CheckIn checkIn = DatabaseManager.get().getCheckInsDBManager().getAutoTagCheckIn(dish);
            if (checkIn == null) {
                dish.setTimeLastUpdated(System.currentTimeMillis());
                DatabaseManager.get().getCheckInsDBManager().autoCreateCheckIn(dish);
                setResult(Constants.DISH_ADDED);
                finish();
            } else {
                // Auto-tag to recent check-in
                dish.setTimeLastUpdated(System.currentTimeMillis());
                DatabaseManager.get().getDishesDBManager().addDish(dish);
                checkIn.addTaggedDish(dish);
                DatabaseManager.get().getCheckInsDBManager().updateCheckIn(checkIn);
                setResult(Constants.DISH_ADDED);
                finish();
            }
        } else {
            if (!dish.getUriString().equals(originalDish.getUriString())) {
                PictureUtils.deleteFileWithUri(originalDish.getUriString());
            }

            DatabaseManager.get().getDishesDBManager().updateDish(dish);
            setResult(Constants.DISH_EDITED);
            finish();
        }
    }

    /** Return true if the confirm exit dialog is shown */
    public boolean confirmExit() {
        loadFormIntoDish();
        if (newDishMode || dish.hasChangedInForm(originalDish)) {
            leaveDialog.show();
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
