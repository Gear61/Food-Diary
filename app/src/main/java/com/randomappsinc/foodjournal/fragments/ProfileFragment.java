package com.randomappsinc.foodjournal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishesFullViewGalleryActivity;
import com.randomappsinc.foodjournal.activities.RestaurantViewActivity;
import com.randomappsinc.foodjournal.activities.SettingsActivity;
import com.randomappsinc.foodjournal.adapters.TopDishesAdapter;
import com.randomappsinc.foodjournal.adapters.TopRestaurantsAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.DishUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.TotalStatsView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment implements TopRestaurantsAdapter.Listener, TopDishesAdapter.Listener {

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.total_stats) View totalStatsRoot;
    @BindView(R.id.top_restaurants_list) LinearLayout topRestaurantsContainer;
    @BindView(R.id.top_dishes_list) LinearLayout topDishesContainer;

    private Unbinder unbinder;
    private TotalStatsView totalStatsView;
    private TopRestaurantsAdapter topRestaurantsAdapter;
    private TopDishesAdapter topDishesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setTitle(R.string.profile);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        totalStatsView = new TotalStatsView(totalStatsRoot);
        topRestaurantsAdapter = new TopRestaurantsAdapter(topRestaurantsContainer, this);
        topDishesAdapter = new TopDishesAdapter(topDishesContainer, this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        totalStatsView.reloadData();
        topRestaurantsAdapter.loadTopRestaurants(getActivity());
        topDishesAdapter.loadTopDishes(getActivity());
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), RestaurantViewActivity.class);
        intent.putExtra(Constants.RESTAURANT_KEY, restaurant);
        getActivity().startActivity(intent);
    }

    @Override
    public void onTopDishClicked(List<Dish> dishes) {
        Intent intent = new Intent(getActivity(), DishesFullViewGalleryActivity.class);
        intent.putExtra(Constants.DISH_IDS_KEY, DishUtils.getDishIdList(dishes));
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        UIUtils.loadActionBarIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
