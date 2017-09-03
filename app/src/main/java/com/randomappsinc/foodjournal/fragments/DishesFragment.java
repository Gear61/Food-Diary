package com.randomappsinc.foodjournal.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DishesFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int FILES_PERMISSION_REQUEST = 2;

    public static final int CAMERA_SOURCE = 1;
    public static final int FILES_SOURCE = 2;

    @BindView(R.id.parent) View parent;
    @BindView(R.id.pick_source) FloatingActionMenu mSourcePicker;
    @BindView(R.id.from_camera) FloatingActionButton mCameraPicker;
    @BindView(R.id.from_files) FloatingActionButton mFilesPicker;
    @BindString(R.string.choose_image_from) String mChooseImageFrom;

    private String mRestaurantId;
    private File mTakenPhotoFile;
    private Uri mTakenPhotoUri;
    private Unbinder mUnbinder;

    public static DishesFragment newInstance(String restaurantId) {
        DishesFragment fragment = new DishesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RestaurantsActivity.ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dishes, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        if (getArguments() != null) {
            mRestaurantId = getArguments().getString(RestaurantsActivity.ID_KEY);
        }
        mCameraPicker.setImageDrawable(new IconDrawable(getActivity(),
                IoniconsIcons.ion_android_camera).colorRes(R.color.white));
        mFilesPicker.setImageDrawable(new IconDrawable(getActivity(),
                IoniconsIcons.ion_android_folder).colorRes(R.color.white));

        return rootView;
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
            mTakenPhotoFile = PictureUtils.createImageFile(parent);
            if (mTakenPhotoFile != null) {
                mTakenPhotoUri = FileProvider.getUriForFile(getActivity(),
                        "com.randomappsinc.foodjournal.fileprovider",
                        mTakenPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakenPhotoUri);
                startActivityForResult(takePictureIntent, CAMERA_SOURCE);
            }
        }
    }

    private void openFilePicker() {
        Intent getIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(getIntent, mChooseImageFrom);
        startActivityForResult(chooserIntent, FILES_SOURCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_SOURCE:
                    Intent cameraIntent = new Intent(getActivity(), DishFormActivity.class);
                    cameraIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
                    cameraIntent.putExtra(DishFormActivity.URI_KEY, mTakenPhotoUri.toString());
                    startActivityForResult(cameraIntent, CAMERA_SOURCE);
                    break;
                case FILES_SOURCE:
                    String imageUri = data.getDataString();
                    Intent filesIntent = new Intent(getActivity(), DishFormActivity.class);
                    filesIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
                    filesIntent.putExtra(DishFormActivity.URI_KEY, imageUri);
                    startActivityForResult(filesIntent, FILES_SOURCE);
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == CAMERA_SOURCE) {
            mTakenPhotoFile.delete();
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
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
