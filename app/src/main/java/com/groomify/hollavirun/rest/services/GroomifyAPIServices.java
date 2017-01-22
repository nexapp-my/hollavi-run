package com.groomify.hollavirun.rest.services;

import com.groomify.hollavirun.rest.models.LoginRequest;
import com.groomify.hollavirun.rest.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Valkyrie1988 on 1/22/2017.
 */

public interface GroomifyAPIServices {

    @POST("fb_users")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
