package com.randomappsinc.foodjournal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.randomappsinc.foodjournal.utils.DishUtils;

import java.util.List;

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
    private boolean typedSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        searchManager = DatabaseManager.get().getSearchResultsDBManager();

        dishesAdapter = new SearchDishesAdapter(this, getActivity());
        dishResults.setAdapter(dishesAdapter);
        restaurantsAdapter = new SearchRestaurantsAdapter(this, getActivity());
        restaurantResults.setAdapter(restaurantsAdapter);
        checkInsAdapter = new SearchCheckInsAdapter(this, getActivity());
        checkInResults.setAdapter(checkInsAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchManager.setListener(this);
        searchManager.doSearch(searchInput.getText().toString());
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        typedSearch = true;
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
        restaurantsAdapter.setRestaurants(searchResults.getRestaurants());
        checkInsAdapter.setCheckIns(searchResults.getCheckIns());

        // Only scroll lists back to the beginning if the user is doing a new search
        if (typedSearch) {
            dishResults.scrollToPosition(0);
            restaurantResults.scrollToPosition(0);
            checkInResults.scrollToPosition(0);
        }

        typedSearch = false;
    }

    @Override
    public void onDishClicked(List<Dish> dishes, int position) {
        Intent intent = new Intent(getActivity(), DishesFullViewGalleryActivity.class);
        intent.putExtra(Constants.DISH_IDS_KEY, DishUtils.getDishIdList(dishes));
        intent.putExtra(DishesFullViewGalleryActivity.POSITION_KEY, position);
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
