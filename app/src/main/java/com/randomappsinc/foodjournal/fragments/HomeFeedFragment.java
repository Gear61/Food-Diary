package com.randomappsinc.foodjournal.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.adapters.DishFeedAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.RestaurantsDBManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.DishUtils;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFeedFragment extends Fragment
        implements DishFeedAdapter.Listener, RestaurantsDBManager.Listener {

    public static HomeFeedFragment newInstance() {
        HomeFeedFragment fragment = new HomeFeedFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.dishes) ListView dishesList;
    @BindView(R.id.no_dishes) View noDishes;
    @BindString(R.string.choose_image_from) String chooseImageFrom;

    private Unbinder unbinder;
    private DishFeedAdapter dishesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setTitle(R.string.app_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        dishesAdapter = new DishFeedAdapter(this, getActivity(), noDishes);
        dishesList.setAdapter(dishesAdapter);

        DatabaseManager.get().getRestaurantsDBManager().registerListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        dishesAdapter.resyncWithDb();
    }

    /** Starts the flow to add a dish via uploading from gallery */
    private void addWithGallery() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openFilePicker();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openFilePicker() {
        Intent getIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Intent chooserIntent = Intent.createChooser(getIntent, chooseImageFrom);
        startActivityForResult(chooserIntent, Constants.GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Constants.GALLERY_CODE) {
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            File photoFile = PictureUtils.createImageFile();
            Context context = getContext();
            if (photoFile == null || context == null) {
                UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
                return;
            }
            Uri copyUri = FileProvider.getUriForFile(context,
                    Constants.FILE_PROVIDER_AUTHORITY,
                    photoFile);
            if (!PictureUtils.copyFromUriIntoFile(context.getContentResolver(), data.getData(), copyUri)) {
                UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
                return;
            }
            Intent filesIntent = new Intent(getActivity(), DishFormActivity.class);
            filesIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
            filesIntent.putExtra(DishFormActivity.URI_KEY, copyUri.toString());
            startActivityForResult(filesIntent, 1);
        } else if (resultCode == Constants.DISH_ADDED) {
            refreshWithAddedDish();
        }
    }

    @Override
    public void onRestaurantDeleted(String restaurantId) {
        dishesAdapter.updateWithDeletedRestaurant(restaurantId);
    }

    public void refreshWithAddedDish() {
        if (dishesList == null) {
            return;
        }

        dishesList.clearFocus();
        dishesList.post(new Runnable() {
            @Override
            public void run() {
                dishesList.setSelection(0);
            }
        });
    }

    @Override
    public void shareDish(Dish dish) {
        DishUtils.sharePhotoWithUri(dish.getUriString(), getActivity());
    }

    @Override
    public void editDish(Dish dish) {
        Intent intent = new Intent(getActivity(), DishFormActivity.class);
        intent.putExtra(DishFormActivity.NEW_DISH_KEY, false);
        intent.putExtra(DishFormActivity.DISH_KEY, dish);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // External storage permission granted
        openFilePicker();
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
        DatabaseManager.get().getRestaurantsDBManager().unregisterListener(this);
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        UIUtils.loadActionBarIcon(menu, R.id.upload_from_gallery, IoniconsIcons.ion_android_folder, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_from_gallery:
                addWithGallery();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
