package com.groomify.hollavirun.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.groomify.hollavirun.rest.services.GroomifyAPIServices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Valkyrie1988 on 1/22/2017.
 */

public class RestClient {
    private static final String BASE_URL = "https://groomifyrun-api.herokuapp.com";

    private GroomifyAPIServices apiService;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


       /* RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();*/
        //apiService = restAdapter.create(GroomifyAPIServices.class);

        apiService = retrofit.create(GroomifyAPIServices.class);


    }

    public GroomifyAPIServices getApiService()
    {
        return apiService;
    }
}
