package com.randomappsinc.foodjournal.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DishFullViewFragment extends Fragment {

    public static DishFullViewFragment newInstance(int dishId, boolean fromRestaurant) {
        DishFullViewFragment fragment = new DishFullViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DISH_ID_KEY, dishId);
        bundle.putBoolean(Constants.FROM_RESTAURANT_KEY, fromRestaurant);
        fragment.setArguments(bundle);
        return fragment;
    }

    private final Callback imageLoadingCallback = new Callback() {
        @Override
        public void onSuccess() {
            if (parent != null) {
                parent.animate().alpha(1.0f).setDuration(getResources().getInteger(R.integer.default_anim_length));
            }
        }

        @Override
        public void onError(Exception e) {
            UIUtils.showToast(R.string.image_load_fail, Toast.LENGTH_LONG);
        }
    };

    @BindView(R.id.parent) @Nullable View parent;
    @BindView(R.id.picture) ImageView picture;
    @BindView(R.id.picture_label) TextView pictureLabel;

    private Unbinder unbinder;
    private int dishId;
    private Drawable defaultThumbnail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dish_full_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        defaultThumbnail = new IconDrawable(
                getActivity(),
                IoniconsIcons.ion_image).colorRes(R.color.dark_gray);
        dishId = getArguments().getInt(Constants.DISH_ID_KEY);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Dish dish = DatabaseManager.get().getDishesDBManager().getDish(dishId);
        Picasso.get()
                .load(dish.getUriString())
                .error(defaultThumbnail)
                .fit()
                .centerInside()
                .into(picture, imageLoadingCallback);

        boolean fromRestaurant = getArguments().getBoolean(Constants.FROM_RESTAURANT_KEY);
        pictureLabel.setMovementMethod(LinkMovementMethod.getInstance());
        pictureLabel.setText(Html.fromHtml(dish.getDishInfoText(!fromRestaurant)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.get().cancelRequest(picture);
        unbinder.unbind();
    }
}
