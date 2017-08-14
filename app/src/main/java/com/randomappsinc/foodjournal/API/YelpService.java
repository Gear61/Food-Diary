package com.randomappsinc.foodjournal.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public interface YelpService {
    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> fetchToken(@Field("client_id") String clientId,
                                   @Field("client_secret") String clientSecret,
                                   @Field("grant_type") String last);
}
