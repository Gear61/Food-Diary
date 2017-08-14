package com.randomappsinc.foodjournal.API.Callbacks;

import com.randomappsinc.foodjournal.API.ApiConstants;
import com.randomappsinc.foodjournal.API.Models.SearchResults;
import com.randomappsinc.foodjournal.API.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class FetchRestaurantsCallback implements Callback<SearchResults> {
    @Override
    public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {

        } else if (response.code() == ApiConstants.HTTP_STATUS_UNAUTHORIZED) {
            RestClient.getInstance().refreshToken();
        }
    }

    @Override
    public void onFailure(Call<SearchResults> call, Throwable t) {
        RestClient.getInstance().refreshToken();
    }
}
