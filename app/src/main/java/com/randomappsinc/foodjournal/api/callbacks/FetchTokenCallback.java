package com.randomappsinc.foodjournal.api.callbacks;

import com.randomappsinc.foodjournal.api.ApiConstants;
import com.randomappsinc.foodjournal.api.models.TokenResponse;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class FetchTokenCallback implements Callback<TokenResponse> {
    @Override
    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK &&
            response.body() != null &&
            response.body().getAccessToken() != null) {
            PreferencesManager.get().setBearerToken(response.body().getAccessToken());
        } else {
            RestClient.getInstance().refreshToken();
        }
    }

    @Override
    public void onFailure(Call<TokenResponse> call, Throwable t) {
        RestClient.getInstance().refreshToken();
    }
}
