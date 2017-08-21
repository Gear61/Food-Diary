package com.randomappsinc.foodjournal.API;

import com.randomappsinc.foodjournal.API.Models.RestaurantResults;
import com.randomappsinc.foodjournal.API.Models.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public interface YelpService {
    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> fetchToken(@Field("client_id") String clientId,
                                   @Field("client_secret") String clientSecret,
                                   @Field("grant_type") String last);

    @GET("v3/businesses/search")
    Call<RestaurantResults> fetchRestaurants(@Query("term") String term,
                                             @Query("location") String location,
                                             @Query("limit") int limit);
}
