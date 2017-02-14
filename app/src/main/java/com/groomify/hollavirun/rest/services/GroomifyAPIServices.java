package com.groomify.hollavirun.rest.services;

import com.groomify.hollavirun.rest.models.request.MissionTransactionRequest;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.rest.models.response.RaceResponse;
import com.groomify.hollavirun.rest.models.request.LoginRequest;
import com.groomify.hollavirun.rest.models.request.PostFacebookTransactionRequest;
import com.groomify.hollavirun.rest.models.request.UpdateRunnerInfoRequest;
import com.groomify.hollavirun.rest.models.request.UpdateUserLocationRequest;
import com.groomify.hollavirun.rest.models.response.JoinRaceResponse;
import com.groomify.hollavirun.rest.models.response.LoginResponse;
import com.groomify.hollavirun.rest.models.response.RaceDetailResponse;
import com.groomify.hollavirun.rest.models.response.MissionSubmissionResponse;
import com.groomify.hollavirun.rest.models.response.PostFacebookTransactionResponse;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.rest.models.response.SearchRunnerLocationResponse;
import com.groomify.hollavirun.rest.models.response.UpdateRunnerInfoResponse;
import com.groomify.hollavirun.rest.models.response.UpdateUserLocationResponse;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Valkyrie1988 on 1/22/2017.
 */

public interface GroomifyAPIServices {

    @POST("fb_users")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/fb_users/info")
    Call<UserInfoResponse> getUser( @Header("fb_id") String facebookId,
                                    @Header("auth_token") String authToken);

    @GET("races")
    Call<List<RaceResponse>> races(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken);

    @GET("races/{id}")
    Call<RaceDetailResponse> raceDetail(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);

    @GET("races/{id}/info")
    Call<List<RaceInfoResponse>> raceInfo(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);


    @POST("races/{id}/join")
    Call<JoinRaceResponse> joinRace(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);


    @POST("fb_transactions")
    Call<PostFacebookTransactionResponse> postFacebookTransaction(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Body PostFacebookTransactionRequest postFacebookTransactionRequest
    );

    @POST("mission_transactions")
    Call<MissionSubmissionResponse> submitMissionTransaction(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Body MissionTransactionRequest missionSubmissionRequest
    );

    @PUT("runners/{id}")
    Call<UpdateRunnerInfoResponse> updateRunnerInfo(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id, //Pending clarification
            @Body UpdateRunnerInfoRequest updateRunnerInfoRequest
    );

    @GET("/search/runner")
    Call<SearchRunnerLocationResponse> searchRunnerLocation(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Query("bib") String bibNo
    );

    @POST("user_locations")
    Call<UpdateUserLocationResponse> updateUserLocation(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Body UpdateUserLocationRequest updateUserLocationRequest
    );
}
