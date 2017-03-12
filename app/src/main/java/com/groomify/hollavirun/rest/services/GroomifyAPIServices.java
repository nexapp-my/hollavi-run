package com.groomify.hollavirun.rest.services;

import com.groomify.hollavirun.rest.models.request.FirstAidRequest;
import com.groomify.hollavirun.rest.models.request.MissionTransactionRequest;
import com.groomify.hollavirun.rest.models.request.UpdateUserInfoRequest;
import com.groomify.hollavirun.rest.models.response.CheckAppVersionResponse;
import com.groomify.hollavirun.rest.models.response.RaceGalleryResponse;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.rest.models.response.RaceRankingResponse;
import com.groomify.hollavirun.rest.models.response.RaceResponse;
import com.groomify.hollavirun.rest.models.request.LoginRequest;
import com.groomify.hollavirun.rest.models.request.PostFacebookTransactionRequest;
import com.groomify.hollavirun.rest.models.request.UpdateRunnerInfoRequest;
import com.groomify.hollavirun.rest.models.request.UpdateUserLocationRequest;
import com.groomify.hollavirun.rest.models.response.JoinRaceResponse;
import com.groomify.hollavirun.rest.models.response.RaceDetailResponse;
import com.groomify.hollavirun.rest.models.response.MissionSubmissionResponse;
import com.groomify.hollavirun.rest.models.response.PostFacebookTransactionResponse;
import com.groomify.hollavirun.rest.models.response.SearchRunnerLocationResponse;
import com.groomify.hollavirun.rest.models.response.RunnerInfoResponse;
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

    @GET("version")
    Call<CheckAppVersionResponse> checkVersion();

    @POST("fb_users")
    Call<UserInfoResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/fb_users/info")
    Call<UserInfoResponse> getUser( @Header("fb_id") String facebookId,
                                    @Header("auth_token") String authToken);

    @PUT("/fb_users/{id}")
    Call<UserInfoResponse> updateUser(@Header("fb_id") String facebookId,
                                   @Header("auth_token") String authToken,
                                   @Path("id") Long id,
                                   @Body UpdateUserInfoRequest updateUserInfoRequest);
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
    Call<RaceInfoResponse> raceInfo(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id
    );

    @GET("races/{id}/rank")
    Call<RaceRankingResponse> raceRanking(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id
    );

    @GET("races/{id}/gallery")
    Call<RaceGalleryResponse> getRaceGallery(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id,
            @Query("bib") String bibNo,
            @Query("mission") Integer missionId
    );

    @POST("races/{id}/join")
    Call<JoinRaceResponse> joinRace(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id);

    @POST("races/support")
    Call<Void> callFirstAid(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Body  FirstAidRequest firstAidRequest);


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
    Call<RunnerInfoResponse> updateRunnerInfo(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id, //Pending clarification
            @Body UpdateRunnerInfoRequest updateRunnerInfoRequest
    );

    @GET("runners/{id}")
    Call<RunnerInfoResponse> getRunnerInfo(
            @Header("fb_id") String facebookId,
            @Header("auth_token") String authToken,
            @Path("id") String id
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
