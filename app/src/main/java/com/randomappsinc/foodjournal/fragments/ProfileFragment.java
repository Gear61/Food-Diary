package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.RestaurantViewActivity;
import com.randomappsinc.foodjournal.activities.SettingsActivity;
import com.randomappsinc.foodjournal.adapters.TopRestaurantsAdapter;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.TotalStats;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment implements TopRestaurantsAdapter.Listener {

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.total_dishes_text) TextView totalDishesText;
    @BindView(R.id.total_restaurants_text) TextView totalRestaurantsText;
    @BindView(R.id.total_check_ins_text) TextView totalCheckInsText;
    @BindView(R.id.total_favorites_text) TextView totalFavoritesText;
    @BindView(R.id.top_restaurants_list) LinearLayout topRestaurantsContainer;

    @BindString(R.string.x_dishes) String xDishesTemplate;
    @BindString(R.string.x_restaurants) String xRestaurantsTemplate;
    @BindString(R.string.x_check_ins_recorded) String xCheckInsTemplate;
    @BindString(R.string.x_favorites) String xFavoritesTemplate;

    private Unbinder unbinder;
    private TopRestaurantsAdapter topRestaurantsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setTitle(R.string.profile);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        topRestaurantsAdapter = new TopRestaurantsAdapter(topRestaurantsContainer, this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTotalStats();
        topRestaurantsAdapter.loadTopRestaurants(getActivity());
    }

    private void loadTotalStats() {
        TotalStats totalStats = DatabaseManager.get().getStatsDBManager().getTotalStats();

        int totalDishes = totalStats.getNumDishes();
        if (totalDishes == 1) {
            totalDishesText.setText(R.string.one_dish);
        } else {
            totalDishesText.setText(String.format(xDishesTemplate, totalDishes));
        }

        int totalRestaurants = totalStats.getNumRestaurants();
        if (totalRestaurants == 1) {
            totalRestaurantsText.setText(R.string.one_restaurant);
        } else {
            totalRestaurantsText.setText(String.format(xRestaurantsTemplate, totalRestaurants));
        }

        int totalCheckIns = totalStats.getNumCheckIns();
        if (totalCheckIns == 1) {
            totalCheckInsText.setText(R.string.one_check_in_recorded);
        } else {
            totalCheckInsText.setText(String.format(xCheckInsTemplate, totalCheckIns));
        }

        int totalFavorites = totalStats.getNumFavorites();
        if (totalFavorites == 1) {
            totalFavoritesText.setText(R.string.one_favorite);
        } else {
            totalFavoritesText.setText(String.format(xFavoritesTemplate, totalFavorites));
        }
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), RestaurantViewActivity.class);
        intent.putExtra(Constants.RESTAURANT_KEY, restaurant);
        getActivity().startActivity(intent);
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
