package com.randomappsinc.foodjournal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
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

public class FavoritesFragment extends Fragment {

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.favorites_grid) GridView favoritesGrid;
    @BindView(R.id.no_results) TextView noResults;

    private Unbinder unbinder;
    private DishGridAdapter favoritesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favorites, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        toolbar.setTitle(R.string.favorites);

        favoritesAdapter = new DishGridAdapter(getActivity(), noResults);
        favoritesGrid.setAdapter(favoritesAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Dish> favorites = DatabaseManager.get().getDishesDBManager().getFavoritedDishes();
        favoritesAdapter.setDishes(favorites);
    }

    @OnItemClick(R.id.favorites_grid)
    public void onFavoriteClicked(int position) {
        Intent intent = new Intent(getActivity(), DishesFullViewGalleryActivity.class);
        List<Dish> favorites = favoritesAdapter.getDishes();
        intent.putExtra(Constants.DISH_IDS_KEY, DishUtils.getDishIdList(favorites));
        intent.putExtra(DishesFullViewGalleryActivity.POSITION_KEY, position);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
