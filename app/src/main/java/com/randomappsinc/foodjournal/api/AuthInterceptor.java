package com.randomappsinc.foodjournal.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String bearerToken = ApiConstants.BEARER_PREFIX + YelpToken.API_KEY;

        Request authorizedRequest = originalRequest.newBuilder()
                .header(ApiConstants.AUTHORIZATION, bearerToken)
                .build();
        return chain.proceed(authorizedRequest);
    }
}
