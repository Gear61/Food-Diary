package com.randomappsinc.foodjournal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.randomappsinc.foodjournal.utils.UIUtils;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.DISH_ADDED) {
            refreshWithAddedDish();
        }
    }

    @Override
    public void onRestaurantDeleted(String restaurantId) {
        dishesAdapter.updateWithDeletedRestaurant(restaurantId);
    }

    void refreshWithAddedDish() {
        if (dishesList == null) {
            return;
        }

        dishesList.clearFocus();
        dishesList.post(() -> dishesList.setSelection(0));
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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        UIUtils.loadActionBarIcon(menu, R.id.upload_from_gallery, IoniconsIcons.ion_android_folder, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.upload_from_gallery) {
            Intent cameraIntent = new Intent(getActivity(), DishFormActivity.class)
                    .putExtra(DishFormActivity.NEW_DISH_KEY, true)
                    .putExtra(DishFormActivity.GALLERY_MODE_KEY, true);
            startActivityForResult(cameraIntent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
