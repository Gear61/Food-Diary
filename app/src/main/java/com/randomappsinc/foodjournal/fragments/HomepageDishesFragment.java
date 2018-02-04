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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.adapters.DishesAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.RestaurantsDBManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PermissionUtils;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomepageDishesFragment extends Fragment
        implements DishesAdapter.Listener, RestaurantsDBManager.Listener {

    public static HomepageDishesFragment newInstance() {
        HomepageDishesFragment fragment = new HomepageDishesFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.dishes) ListView mDishesList;
    @BindView(R.id.no_dishes) View noDishes;
    @BindString(R.string.choose_image_from) String mChooseImageFrom;

    private Unbinder mUnbinder;
    private DishesAdapter mDishesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mToolbar.setTitle(R.string.app_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        mDishesAdapter = new DishesAdapter(this, getActivity(), noDishes, null);
        mDishesList.setAdapter(mDishesAdapter);

        DatabaseManager.get().getRestaurantsDBManager().registerListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDishesAdapter.resyncWithDb();
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
        Intent chooserIntent = Intent.createChooser(getIntent, mChooseImageFrom);
        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File photoFile = PictureUtils.copyGalleryImage(data);
            if (photoFile == null) {
                UIUtils.showToast(R.string.image_file_failed, Toast.LENGTH_LONG);
                return;
            }

            Uri fileUri = Uri.fromFile(photoFile);
            String imageUri = fileUri.toString();

            Intent filesIntent = new Intent(getActivity(), DishFormActivity.class);
            filesIntent.putExtra(DishFormActivity.NEW_DISH_KEY, true);
            filesIntent.putExtra(DishFormActivity.URI_KEY, imageUri);
            startActivityForResult(filesIntent, 1);
        } else if (resultCode == Constants.DISH_ADDED) {
            refreshWithAddedDish();
        }
    }

    @Override
    public void onRestaurantDeleted(String restaurantId) {
        mDishesAdapter.updateWithDeletedRestaurant(restaurantId);
    }

    public void refreshWithAddedDish() {
        if (mDishesList == null) {
            return;
        }

        mDishesList.clearFocus();
        mDishesList.post(new Runnable() {
            @Override
            public void run() {
                mDishesList.setSelection(0);
            }
        });
    }

    @Override
    public void editDish(Dish dish) {
        Intent intent = new Intent(getActivity(), DishFormActivity.class);
        intent.putExtra(DishFormActivity.NEW_DISH_KEY, false);
        intent.putExtra(DishFormActivity.DISH_KEY, dish);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
        mUnbinder.unbind();
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
