package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.adapters.DishesAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.RestaurantsDBManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomepageDishesFragment extends Fragment
        implements ListView.OnScrollListener, DishesAdapter.Listener, RestaurantsDBManager.Listener {

    public static HomepageDishesFragment newInstance() {
        HomepageDishesFragment fragment = new HomepageDishesFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.dishes) ListView mDishesList;
    @BindView(R.id.no_dishes) View noDishes;

    private Unbinder mUnbinder;
    private DishesAdapter mDishesAdapter;
    private int lastIndexToTrigger = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mToolbar.setTitle(R.string.app_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mDishesAdapter = new DishesAdapter(this, getActivity(), noDishes, null);
        mDishesList.setAdapter(mDishesAdapter);
        mDishesList.setOnScrollListener(this);

        DatabaseManager.get().getRestaurantsDBManager().registerListener(this);

        return rootView;
    }

    @Override
    public void onRestaurantDeleted(String restaurantId) {
        mDishesAdapter.updateWithDeletedRestaurant(restaurantId);
    }

    public void refreshWithAddedDish() {
        if (mDishesList == null) {
            return;
        }

        mDishesAdapter.updateWithAddedDish();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.DISH_EDITED) {
            mDishesAdapter.updateWithEditedDish();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int bottomIndexSeen = firstVisibleItem + visibleItemCount;

        // If visible last item's position is the size of the list, then we've hit the bottom
        if (mDishesAdapter.canFetchMore() && bottomIndexSeen == totalItemCount) {
            if (lastIndexToTrigger != bottomIndexSeen) {
                lastIndexToTrigger = bottomIndexSeen;
                mDishesAdapter.fetchNextPage();
            }
        }
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
}
