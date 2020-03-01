package com.randomappsinc.foodjournal.api.callbacks;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.api.ApiConstants;
import com.randomappsinc.foodjournal.api.models.RestaurantInfo;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchRestaurantInfoCallback implements Callback<RestaurantInfo> {

    @Override
    public void onResponse(@NonNull Call<RestaurantInfo> call, @NonNull Response<RestaurantInfo> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            DatabaseManager.get()
                    .getRestaurantsDBManager()
                    .updateRestaurantInfo(response.body().getRestaurant());
        }
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantInfo> call, @NonNull Throwable t) {}
}
