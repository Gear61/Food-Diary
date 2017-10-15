package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.adapters.DishesAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DishesFragment extends Fragment implements ListView.OnScrollListener {

    // Result codes
    public static final int DISH_ADDED = 1;
    public static final int DISH_EDITED = 2;

    @BindView(R.id.dishes) ListView mDishesList;
    @BindView(R.id.no_dishes) View noDishes;

    private Unbinder mUnbinder;
    private DishesAdapter mDishesAdapter;
    private int lastIndexToTrigger = 0;

    public static DishesFragment newInstance(String restaurantId) {
        DishesFragment fragment = new DishesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RestaurantsActivity.ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dishes, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        String restaurantId = getArguments() != null ? getArguments().getString(RestaurantsActivity.ID_KEY) : null;
        mDishesAdapter = new DishesAdapter(this, noDishes, restaurantId);
        mDishesList.setAdapter(mDishesAdapter);
        mDishesList.setOnScrollListener(this);

        return rootView;
    }

    public void editDish(Dish dish) {
        Intent intent = new Intent(getActivity(), DishFormActivity.class);
        intent.putExtra(DishFormActivity.NEW_DISH_KEY, false);
        intent.putExtra(DishFormActivity.DISH_KEY, dish);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == DISH_EDITED) {
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
        mUnbinder.unbind();
    }
}
