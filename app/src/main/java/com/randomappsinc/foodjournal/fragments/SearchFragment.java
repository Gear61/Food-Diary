package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.CheckInFormActivity;
import com.randomappsinc.foodjournal.activities.DishesFullViewGalleryActivity;
import com.randomappsinc.foodjournal.activities.RestaurantViewActivity;
import com.randomappsinc.foodjournal.adapters.SearchCheckInsAdapter;
import com.randomappsinc.foodjournal.adapters.SearchDishesAdapter;
import com.randomappsinc.foodjournal.adapters.SearchRestaurantsAdapter;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.models.SearchResults;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.SearchResultsDBManager;
import com.randomappsinc.foodjournal.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class SearchFragment extends Fragment implements
        SearchResultsDBManager.Listener, SearchDishesAdapter.Listener,
        SearchRestaurantsAdapter.Listener, SearchCheckInsAdapter.Listener {

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.search_input) EditText searchInput;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.dishes) RecyclerView dishResults;
    @BindView(R.id.restaurants) RecyclerView restaurantResults;
    @BindView(R.id.check_ins) RecyclerView checkInResults;

    private Unbinder unbinder;
    private SearchResultsDBManager searchManager;
    private SearchDishesAdapter dishesAdapter;
    private SearchRestaurantsAdapter restaurantsAdapter;
    private SearchCheckInsAdapter checkInsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        searchManager = DatabaseManager.get().getSearchResultsDBManager();
        searchManager.setListener(this);

        dishesAdapter = new SearchDishesAdapter(this, getActivity());
        dishResults.setAdapter(dishesAdapter);
        restaurantsAdapter = new SearchRestaurantsAdapter(this, getActivity());
        restaurantResults.setAdapter(restaurantsAdapter);
        checkInsAdapter = new SearchCheckInsAdapter(this, getActivity());
        checkInResults.setAdapter(checkInsAdapter);

        searchManager.doSearch("");

        return rootView;
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        searchManager.doSearch(input.toString());
        clearSearch.setVisibility(input.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        searchInput.setText("");
    }

    @Override
    public void onSearchComplete(SearchResults searchResults) {
        dishesAdapter.setDishes(searchResults.getDishes());
        dishResults.scrollToPosition(0);

        restaurantsAdapter.setRestaurants(searchResults.getRestaurants());
        restaurantResults.scrollToPosition(0);

        checkInsAdapter.setCheckIns(searchResults.getCheckIns());
        checkInResults.scrollToPosition(0);
    }

    @Override
    public void onDishClicked(Dish dish) {
        Intent intent = new Intent(getActivity(), DishesFullViewGalleryActivity.class);
        intent.putExtra(DishesFullViewGalleryActivity.DISH_KEY, dish);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), RestaurantViewActivity.class);
        intent.putExtra(Constants.RESTAURANT_KEY, restaurant);
        getActivity().startActivity(intent);
    }

    @Override
    public void onCheckInClicked(CheckIn checkIn) {
        Intent intent = new Intent(getActivity(), CheckInFormActivity.class);
        intent.putExtra(CheckInFormActivity.ADDER_MODE_KEY, false);
        intent.putExtra(CheckInFormActivity.CHECK_IN_KEY, checkIn);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchManager.unregisterListener();
        unbinder.unbind();
    }
}
