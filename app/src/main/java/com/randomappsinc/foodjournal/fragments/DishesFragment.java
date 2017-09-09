package com.randomappsinc.foodjournal.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.adapters.DishesAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class DishesFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int FILES_PERMISSION_REQUEST = 2;

    // Requesting picture of dish
    public static final int CAMERA_SOURCE = 1;
    public static final int FILES_SOURCE = 2;

    // Opening dish form
    public static final int DISH_FORM_ADD = 3;
    public static final int DISH_FORM_EDIT = 4;

    // Result codes
    public static final int DISH_ADDED = 1;
    public static final int DISH_EDITED = 2;
    public static final int DISH_DELETED = 3;

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.dishes) ListView mDishesList;
    @BindView(R.id.no_dishes) View noDishes;
    @BindView(R.id.pick_source) FloatingActionMenu mSourcePicker;
    @BindView(R.id.from_camera) FloatingActionButton mCameraPicker;
    @BindView(R.id.from_files) FloatingActionButton mFilesPicker;
    @BindString(R.string.choose_image_from) String mChooseImageFrom;

    private Restaurant mRestaurant;
    private File mTakenPhotoFile;
    private Uri mTakenPhotoUri;
    private Unbinder mUnbinder;
    private DishesAdapter mDishesAdapter;

    public static DishesFragment newInstance(String restaurantId) {
        DishesFragment fragment = new DishesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RestaurantsActivity.ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dishes, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        mCameraPicker.setImageDrawable(new IconDrawable(getActivity(),
                IoniconsIcons.ion_android_camera).colorRes(R.color.white));
        mFilesPicker.setImageDrawable(new IconDrawable(getActivity(),
                IoniconsIcons.ion_android_folder).colorRes(R.color.white));

        String restaurantId = getArguments() != null ? getArguments().getString(RestaurantsActivity.ID_KEY) : null;
        if (restaurantId != null) {
            mRestaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(restaurantId);
        }

        mDishesAdapter = new DishesAdapter(getActivity(), noDishes, restaurantId);
        mDishesList.setAdapter(mDishesAdapter);

        return rootView;
    }

    @OnItemClick(R.id.dishes)
    public void onDishSelected(int position) {
        Dish dish = mDishesAdapter.getItem(position);
        Intent editDish = new Intent(getActivity(), DishFormActivity.class);
        editDish.putExtra(DishFormActivity.NEW_DISH_KEY, false);
        editDish.putExtra(DishFormActivity.DISH_KEY, dish);
        startActivityForResult(editDish, DISH_FORM_EDIT);
    }

    @OnClick({R.id.from_camera, R.id.from_files})
    public void addPicture(View view) {
        closeUploadMenu();

        switch (view.getId()) {
            case R.id.from_camera:
                if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA)) {
                    startCameraPage();
                } else {
                    PermissionUtils.requestPermission(
                            this,
                            Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_REQUEST);
                }
                break;
            case R.id.from_files:
                if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openFilePicker();
                } else {
                    PermissionUtils.requestPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            FILES_PERMISSION_REQUEST);
                }
                break;
        }
    }

    public void closeUploadMenu() {
        mSourcePicker.close(true);
    }

    private void startCameraPage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            mTakenPhotoFile = PictureUtils.createImageFile();
            if (mTakenPhotoFile != null) {
                mTakenPhotoUri = FileProvider.getUriForFile(getActivity(),
                        "com.randomappsinc.foodjournal.fileprovider",
                        mTakenPhotoFile);

                // Grant access to content URI so camera app doesn't crash
                List<ResolveInfo> resolvedIntentActivities = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(
                            packageName,
                            mTakenPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakenPhotoUri);
                startActivityForResult(takePictureIntent, CAMERA_SOURCE);
            } else {
                UIUtils.showSnackbar(mParent, getString(R.string.image_file_failed));
            }
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
                    // Revoke external access to URI
                    getActivity().revokeUriPermission(
                            mTakenPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent cameraIntent = new Intent(getActivity(), DishFormActivity.class);
                    cameraIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
                    cameraIntent.putExtra(DishFormActivity.URI_KEY, mTakenPhotoUri.toString());
                    cameraIntent.putExtra(RestaurantsFragment.RESTAURANT_KEY, mRestaurant);
                    startActivityForResult(cameraIntent, DISH_FORM_ADD);
                    break;
                case FILES_SOURCE:
                    mTakenPhotoFile = PictureUtils.copyGalleryImage(data);
                    if (mTakenPhotoFile == null) {
                        UIUtils.showSnackbar(mParent, getString(R.string.image_file_failed));
                        return;
                    }

                    Uri fileUri = Uri.fromFile(mTakenPhotoFile);
                    String imageUri = fileUri.toString();

                    Intent filesIntent = new Intent(getActivity(), DishFormActivity.class);
                    filesIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
                    filesIntent.putExtra(DishFormActivity.URI_KEY, imageUri);
                    filesIntent.putExtra(RestaurantsFragment.RESTAURANT_KEY, mRestaurant);
                    startActivityForResult(filesIntent, DISH_FORM_ADD);
                    break;
            }
        } else if (requestCode == DISH_FORM_ADD && resultCode == Activity.RESULT_CANCELED) {
            mTakenPhotoFile.delete();
        }

        if (resultCode == DISH_ADDED || resultCode == DISH_EDITED || resultCode == DISH_DELETED) {
            mDishesAdapter.resyncWithDB();

            if (resultCode == DISH_ADDED || resultCode == DISH_EDITED) {
                mDishesList.setSelectionAfterHeaderView();
            }
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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        UIUtils.hideKeyboard(getActivity());
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
