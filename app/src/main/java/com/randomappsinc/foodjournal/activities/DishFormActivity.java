package com.randomappsinc.foodjournal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.photo.PhotoImportManager;
import com.randomappsinc.foodjournal.speech.SpeechToTextManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.StringUtils;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DateTimeAdder;
import com.randomappsinc.foodjournal.views.DishPhotoOptionsDialog;
import com.randomappsinc.foodjournal.views.RatingView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class DishFormActivity extends StandardActivity
        implements DishPhotoOptionsDialog.Listener, PhotoImportManager.Listener,
        SpeechToTextManager.Listener {

    public static final String CAMERA_MODE_KEY = "cameraMode";
    public static final String GALLERY_MODE_KEY = "galleryMode";
    public static final String NEW_DISH_KEY = "newDish";
    public static final String DISH_KEY = "dish";

    // Permission request codes
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int GALLERY_PERMISSION_CODE = 2;
    private static final int AUDIO_PERMISSION_CODE = 3;

    // Activity request codes
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int RESTAURANT_REQUEST_CODE = 3;

    private final DateTimeAdder.Listener mDateTimeListener = new DateTimeAdder.Listener() {
        @Override
        public void onDateTimeChosen(long timeChosen) {
            dish.setTimeAdded(timeChosen);
            dateTimeText.setText(TimeUtils.getDefaultTimeText(timeChosen));
        }
    };

    @BindView(R.id.dish_picture) ImageView dishPicture;
    @BindView(R.id.rating_widget) View ratingLayout;
    @BindView(R.id.dish_name_input) EditText dishNameInput;
    @BindView(R.id.dish_name_voice_entry) View dishNameVoiceEntry;
    @BindView(R.id.clear_dish_name) View clearDishName;
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
    private PhotoImportManager photoImportManager;
    private SpeechToTextManager speechToTextManager;

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
                .onPositive((dialog, which) -> {
                    deleteOldPhotoIfNecessary();
                    finish();
                })
                .build();
        speechToTextManager = new SpeechToTextManager(this, this);

        newDishMode = getIntent().getBooleanExtra(NEW_DISH_KEY, false);
        photoOptionsDialog = new DishPhotoOptionsDialog(this, this);
        photoImportManager = new PhotoImportManager(this);

        // Adding a new dish
        if (newDishMode) {
            if (getIntent().getBooleanExtra(CAMERA_MODE_KEY, false)) {
                addWithCamera();
            } else if (getIntent().getBooleanExtra(GALLERY_MODE_KEY, false)) {
                addWithGallery();
            }

            dish = new Dish();
            dish.setTimeAdded(System.currentTimeMillis());
            dateTimeText.setText(TimeUtils.getDefaultTimeText(dish.getTimeAdded()));

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

        setDishNameInputAction();
    }

    private void setDishNameInputAction() {
        if (dishNameInput.getText().length() > 0) {
            dishNameVoiceEntry.setVisibility(View.GONE);
            clearDishName.setVisibility(View.VISIBLE);
        } else {
            clearDishName.setVisibility(View.GONE);
            dishNameVoiceEntry.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.dish_name_voice_entry)
    public void enterDishNameWithVoice() {
        speechToTextManager.setListeningPrompt(R.string.dish_name_voice_hint);
        speechToTextManager.setStringFormatter(StringUtils::capitalizeWords);
        if (PermissionUtils.isPermissionGranted(Manifest.permission.RECORD_AUDIO, this)) {
            speechToTextManager.startSpeechToTextFlow();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
        }
    }

    @OnTextChanged(value = R.id.dish_name_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterDishNameChanged(Editable input) {
        setDishNameInputAction();
    }

    @OnClick(R.id.clear_dish_name)
    public void clearDishName() {
        dishNameInput.setText("");
    }

    @Override
    public void onTextSpoken(String spokenText) {
        dishNameInput.setText(spokenText);
        dishNameInput.setSelection(spokenText.length());
    }

    @Override
    public void onAddPhotoFailure() {
        UIUtils.showLongToast(R.string.take_photo_with_camera_failed, this);
    }

    @Override
    public void onAddPhotoSuccess(Uri takenPhotoUri) {
        runOnUiThread(() -> {
            dish.setUriString(takenPhotoUri.toString());
            loadDishPhoto();
        });
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
        if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA, this)) {
            startCameraPage();
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        }
    }

    private void startCameraPage() {
        Intent takePhotoIntent = photoImportManager.getPhotoTakingIntent(this);
        if (takePhotoIntent == null) {
            UIUtils.showLongToast(
                    R.string.take_photo_with_camera_failed);
        } else {
            startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRetakePhoto() {
        addWithCamera();
    }

    private void addWithGallery() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
            openFilePicker();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    GALLERY_PERMISSION_CODE);
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
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
        startActivityForResult(intent, RESTAURANT_REQUEST_CODE);
    }

    @OnClick(R.id.date_text)
    public void selectDate() {
        dateTimeAdder.show(dish.getTimeAdded());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    deleteOldPhotoIfNecessary();
                    photoImportManager.processTakenPhoto(this);
                } else if (resultCode == RESULT_CANCELED) {
                    photoImportManager.deleteLastTakenPhoto();
                }
                loadDishPhoto();
                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    deleteOldPhotoIfNecessary();
                    photoImportManager.processSelectedPhoto(this, data);
                }
                break;
            case RESTAURANT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    restaurant = data.getParcelableExtra(Constants.RESTAURANT_KEY);
                    loadRestaurantInfo();
                }
                break;
        }
    }

    private void deleteOldPhotoIfNecessary() {
        if (newDishMode || !originalDish.getUriString().equals(dish.getUriString())) {
            PictureUtils.deleteFileWithUri(dish.getUriString());
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            case AUDIO_PERMISSION_CODE:
                speechToTextManager.startSpeechToTextFlow();
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
            UIUtils.showLongToast(R.string.dish_title_needed, this);
            return;
        }
        if (restaurant == null) {
            UIUtils.showLongToast(R.string.dish_restaurant_needed, this);
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
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!confirmExit()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (confirmExit()) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
