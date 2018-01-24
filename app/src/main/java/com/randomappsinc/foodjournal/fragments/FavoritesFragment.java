package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishesFullViewGalleryActivity;
import com.randomappsinc.foodjournal.adapters.FavoritesAdapter;
import com.randomappsinc.foodjournal.models.Dish;

import java.util.ArrayList;

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

    private Unbinder unbinder;
    private FavoritesAdapter favoritesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favorites, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        toolbar.setTitle(R.string.favorites);

        favoritesAdapter = new FavoritesAdapter(getActivity());
        favoritesGrid.setAdapter(favoritesAdapter);
        return rootView;
    }

    @OnItemClick(R.id.favorites_grid)
    public void onFavoriteClicked(int position) {
        Intent intent = new Intent(getActivity(), DishesFullViewGalleryActivity.class);
        ArrayList<Dish> favorites = favoritesAdapter.getFavorites();
        intent.putExtra(DishesFullViewGalleryActivity.DISHES_KEY, favorites);
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
