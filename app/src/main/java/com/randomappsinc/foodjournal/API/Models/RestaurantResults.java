package com.randomappsinc.foodjournal.API.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class RestaurantResults {
    @SerializedName("businesses")
    @Expose
    private List<Business> businesses;

    public class Business {
        @SerializedName("id")
        @Expose
        private String businessId;

        @SerializedName("name")
        @Expose
        private String businessName;

        @SerializedName("image_url")
        @Expose
        private String businessImageUrl;

        @SerializedName("display_phone")
        @Expose
        private String phoneNumber;
    }
}
