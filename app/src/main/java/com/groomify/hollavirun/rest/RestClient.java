package com.groomify.hollavirun.rest;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.groomify.hollavirun.rest.services.GroomifyAPIServices;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Valkyrie1988 on 1/22/2017.
 */

public class RestClient {
    private static final String BASE_URL = "http://groomifyrun-api.herokuapp.com";

    private GroomifyAPIServices apiService;

    public RestClient()
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "application/json; charset=utf8").header("Accept", "*/*"); // <-- this is the important line
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder()
                .withRequestContentType(true)
                .withRequestHeaders(true)
                .withResponseHeaders(true)
                // set desired custom options
                .withAllLogData()
                .build();

        httpClient.addInterceptor(okLogInterceptor);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
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
