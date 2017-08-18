package com.randomappsinc.foodjournal.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Adapters.IconItemsAdapter;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.PermissionUtils;
import com.randomappsinc.foodjournal.Utils.PictureUtils;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends StandardActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int FILES_PERMISSION_REQUEST_CODE = 2;

    public static final int CAMERA_SOURCE_CODE = 1;
    public static final int FILES_SOURCE_CODE = 2;

    @BindView(R.id.parent) View parent;
    @BindView(R.id.nav_options) ListView mNavOptions;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.pick_source) FloatingActionMenu mSourcePicker;
    @BindView(R.id.from_camera) FloatingActionButton mCameraPicker;
    @BindView(R.id.from_files) FloatingActionButton mFilesPicker;
    @BindString(R.string.choose_image_from) String mChooseImageFrom;

    private Uri mTakenPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavOptions.setAdapter(new IconItemsAdapter(this, R.array.nav_drawer_tabs, R.array.nav_drawer_icons));

        mCameraPicker.setImageDrawable(new IconDrawable(this,
                IoniconsIcons.ion_android_camera).colorRes(R.color.white));
        mFilesPicker.setImageDrawable(new IconDrawable(this,
                IoniconsIcons.ion_android_folder).colorRes(R.color.white));
    }

    @OnItemClick(R.id.nav_options)
    public void onNavOptionClicked(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, DishesActivity.class);
                break;
            case 1:
                intent = new Intent(this, RestaurantsActivity.class);
                break;
            case 2:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        startActivity(intent);
    }

    @OnClick({R.id.from_camera, R.id.from_files})
    public void addPicture(View view) {
        mSourcePicker.close(true);

        switch (view.getId()) {
            case R.id.from_camera:
                if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA)) {
                    startCameraPage();
                } else {
                    PermissionUtils.requestPermission(
                            this,
                            Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_REQUEST_CODE);
                }
                break;
            case R.id.from_files:
                if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openFilePicker();
                } else {
                    PermissionUtils.requestPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            FILES_PERMISSION_REQUEST_CODE);
                }
                break;
        }
    }

    private void startCameraPage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = PictureUtils.createImageFile(parent);
            if (photoFile != null) {
                mTakenPhotoUri = FileProvider.getUriForFile(this,
                        "com.randomappsinc.foodjournal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakenPhotoUri);
                startActivityForResult(takePictureIntent, CAMERA_SOURCE_CODE);
            }
        }
    }

    private void openFilePicker() {
        Intent getIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(getIntent, mChooseImageFrom);
        startActivityForResult(chooserIntent, FILES_SOURCE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_SOURCE_CODE:
                    Intent cameraIntent = new Intent(this, AddOrEditDishActivity.class);
                    cameraIntent.putExtra(AddOrEditDishActivity.NEW_DISH_KEY, true);
                    cameraIntent.putExtra(AddOrEditDishActivity.URI_KEY, mTakenPhotoUri.toString());
                    startActivity(cameraIntent);
                    break;
                case FILES_SOURCE_CODE:
                    String imageUri = data.getDataString();
                    Intent filesIntent = new Intent(this, AddOrEditDishActivity.class);
                    filesIntent.putExtra(AddOrEditDishActivity.NEW_DISH_KEY, true);
                    filesIntent.putExtra(AddOrEditDishActivity.URI_KEY, imageUri);
                    startActivity(filesIntent);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraPage();
                }
                break;
            case FILES_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
