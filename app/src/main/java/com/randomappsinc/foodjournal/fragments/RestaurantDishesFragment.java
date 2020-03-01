package com.randomappsinc.foodjournal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishesFullViewGalleryActivity;
import com.randomappsinc.foodjournal.adapters.DishGridAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.DishUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class RestaurantDishesFragment extends Fragment {

    public static RestaurantDishesFragment newInstance(String restaurantId) {
        RestaurantDishesFragment fragment = new RestaurantDishesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESTAURANT_ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.dishes_grid) GridView dishesGrid;
    @BindView(R.id.no_dishes) View noDishes;

    private Unbinder unbinder;
    private DishGridAdapter dishesAdapter;
    private String restaurantId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurant_dishes, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        restaurantId = getArguments().getString(Constants.RESTAURANT_ID_KEY);
        dishesAdapter = new DishGridAdapter(getActivity(), noDishes);
        dishesGrid.setAdapter(dishesAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Dish> dishes = DatabaseManager.get().getDishesDBManager().getDishes(restaurantId);
        dishesAdapter.setDishes(dishes);
    }

    @OnItemClick(R.id.dishes_grid)
    public void onDishClicked(int position) {
        Intent intent = new Intent(getActivity(), DishesFullViewGalleryActivity.class);
        List<Dish> dishes = dishesAdapter.getDishes();
        intent.putExtra(Constants.DISH_IDS_KEY, DishUtils.getDishIdList(dishes));
        intent.putExtra(DishesFullViewGalleryActivity.POSITION_KEY, position);
        intent.putExtra(Constants.FROM_RESTAURANT_KEY, true);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
