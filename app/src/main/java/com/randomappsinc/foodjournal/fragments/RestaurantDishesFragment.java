package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.adapters.DishesAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RestaurantDishesFragment extends Fragment implements DishesAdapter.Listener {

    public static RestaurantDishesFragment newInstance(String restaurantId) {
        RestaurantDishesFragment fragment = new RestaurantDishesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESTAURANT_ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.dishes) ListView mDishesList;
    @BindView(R.id.no_dishes) View noDishes;

    private Unbinder mUnbinder;
    private DishesAdapter mDishesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurant_dishes, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        String restaurantId = getArguments().getString(Constants.RESTAURANT_ID_KEY);
        mDishesAdapter = new DishesAdapter(this, getActivity(), noDishes, restaurantId);
        mDishesList.setAdapter(mDishesAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDishesAdapter.resyncWithDb();
    }

    @Override
    public void editDish(Dish dish) {
        Intent intent = new Intent(getActivity(), DishFormActivity.class);
        intent.putExtra(DishFormActivity.NEW_DISH_KEY, false);
        intent.putExtra(DishFormActivity.DISH_KEY, dish);
        startActivityForResult(intent, 1);
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
        mUnbinder.unbind();
    }
}
