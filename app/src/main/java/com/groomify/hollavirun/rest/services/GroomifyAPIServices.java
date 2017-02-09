package com.groomify.hollavirun.rest.services;

import com.groomify.hollavirun.rest.models.LoginRequest;
import com.groomify.hollavirun.rest.models.response.LoginResponse;
import com.groomify.hollavirun.rest.models.Race;
import com.groomify.hollavirun.rest.models.RaceDetail;
import com.groomify.hollavirun.rest.models.response.RaceInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Valkyrie1988 on 1/22/2017.
 */

public interface GroomifyAPIServices {

    @POST("fb_users")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);


    @GET("races")
    Call<List<Race>> races(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken);

    @GET("races/{id}")
    Call<List<RaceDetail>> raceDetail(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);

    @GET("races/{id}/info")
    Call<List<RaceInfo>> raceInfo(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);


    @POST("races/{id}/join")
    Call<Void> joinRace(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);




}
