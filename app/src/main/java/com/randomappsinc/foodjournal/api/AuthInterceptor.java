package com.randomappsinc.foodjournal.api;

import com.randomappsinc.foodjournal.persistence.PreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (!PreferencesManager.get().getBearerToken().isEmpty()) {
            String bearerToken = ApiConstants.BEARER_PREFIX + PreferencesManager.get().getBearerToken();

            Request authorizedRequest = originalRequest.newBuilder()
                    .header(ApiConstants.AUTHORIZATION, bearerToken)
                    .build();
            return chain.proceed(authorizedRequest);
        } else {
            return chain.proceed(originalRequest);
        }
    }
}
