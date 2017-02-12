package com.groomify.hollavirun;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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
import com.groomify.hollavirun.constants.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class SplashActivity extends AppCompatActivity {

    private final static String TAG = SplashActivity.class.getSimpleName();

    CallbackManager callbackManager;
    private final static long SPLASH_DISPLAY_LENGTH = 0;
    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if(!isPermissionGranted()){
            Log.i(TAG, "Permission is not granted.");
            requestPermission();
        }else{
            Log.i(TAG, "Permission granted.");
        }*/


        boolean exitApp = false;
        try {
            if(!isOnline()){
                exitApp = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            exitApp = true;
        }

        if(exitApp){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setTitle("No internet access")
                    .setMessage("No internet access, please ensure you are connected to internet")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }

                    }).show();
        }else{
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(SplashActivity.this);
            printKeyHash();


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
                    boolean userLoggedIn = settings.getBoolean(AppConstant.PREFS_USER_LOGGGED_IN, false);

                    if(!userLoggedIn){
                        LoginManager.getInstance().logOut();
                        AccessToken.setCurrentAccessToken(null);
                    }

                    //TODO another mechanism to determine user authenticated if user is not login with facebook.
                    if(AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null && userLoggedIn){


                        boolean profileUpdated = settings.getBoolean(AppConstant.PREFS_PROFILE_PIC_UPDATED, false);
                        boolean teamSelected = settings.getBoolean(AppConstant.PREFS_TEAM_SELECTED, false);
                        boolean runSelected = settings.getBoolean(AppConstant.PREFS_RUN_SELECTED, false);

                        Log.i(TAG,"profileUpdated: "+profileUpdated+", teamSelected: "+teamSelected+", runSelected: "+runSelected);
                        //launchWelcomeScreen();

                        //TODO check is the race expired, relaunch with race selection again.
                        //TODO check is facebook login success and groomify failed. If failed perform groomify login here.

                        if(profileUpdated && teamSelected && runSelected){
                            launchMainScreen();
                        }else if(!runSelected){
                            launchRaceSelectionScreen();
                        }
                        else if (!teamSelected){
                            launchTeamSelectionScreen();
                        }
                        else{
                            launchWelcomeScreen();
                        }

                    }else{
                        launchOnboardingScreen();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }



    }


    /*private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSIONS_REQUEST);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    recreate();
                }
            } else {
                Toast.makeText(this, "This application needs your permission to proceed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void launchRaceSelectionScreen(){
        Intent sosIntent = new Intent(this, SelectRaceActivity.class);
        startActivity(sosIntent);
        finish();
    }

    private void launchTeamSelectionScreen(){
        Intent intent = new Intent(this, TeamSelectActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchOnboardingScreen(){
        Intent intent = new Intent(this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
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


    public boolean isOnline() throws IOException {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}