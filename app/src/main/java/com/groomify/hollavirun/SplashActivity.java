package com.groomify.hollavirun;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class SplashActivity extends AppCompatActivity {

    private final static String TAG = SplashActivity.class.getSimpleName();

    CallbackManager callbackManager;
    private final static long SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(SplashActivity.this);
        printKeyHash();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //TODO another mechanism to determine user authenticated if user is not login with facebook.
                if(AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null){
                    launchWelcomeScreen();
                }else{
                    //launchLoginScreen();
                    launchOnboardingScreen();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void launchOnboardingScreen(){
        Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchLoginScreen(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchWelcomeScreen(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v(TAG, "GraphResponse:" +response.toString());
                        try {
                            String email = object.getString("email");
                            //String birthday = object.getString("birthday"); // 01/31/1980 format
                            Bundle extras = new Bundle();
                            extras.putString("email", email);
                            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            intent.putExtras(extras);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to convert JSON info", e);
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();


    }
    //To add key into Development phone.
    private void printKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.groomify.hollavirun",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,"Failed to print key hash.", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG,"Failed to print key hash.", e);
        }
    }
}