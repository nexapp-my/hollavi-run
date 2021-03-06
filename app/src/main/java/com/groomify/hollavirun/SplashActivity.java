package com.groomify.hollavirun;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.firebase.crash.FirebaseCrash;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.fragment.TermAndConditionDialogFragment;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.CheckAppVersionResponse;
import com.groomify.hollavirun.rest.models.response.RaceDetailResponse;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.ActivityUtils;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.DebugUtils;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.groomify.hollavirun.utils.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Response;

import static com.groomify.hollavirun.utils.ActivityUtils.launchMainScreen;
import static com.groomify.hollavirun.utils.ActivityUtils.launchOnboardingScreen;
import static com.groomify.hollavirun.utils.ActivityUtils.launchRaceSelectionScreen;
import static com.groomify.hollavirun.utils.ActivityUtils.launchTeamSelectionScreen;
import static com.groomify.hollavirun.utils.ActivityUtils.launchWelcomeScreen;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class SplashActivity extends AppCompatActivity implements TermAndConditionDialogFragment.NoticeDialogListener {

    private final static String TAG = SplashActivity.class.getSimpleName();

    private final static long SPLASH_DISPLAY_LENGTH = 0;

    boolean isDebug = false;

    RestClient client = new RestClient();
    GroomifyUser groomifyUserRealmObj;

    int totalAttemp = 0;
    boolean requireRelog = false;

    int currentTask = 0; //0 = check api, 1 = login
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugUtils.printKeyHash(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        //realm = Realm.getInstance(config);

        AppEventsLogger.activateApp(this.getApplication());

        if(isDebug){
            SharedPreferences settings = this.getSharedPreferences(AppConstant.PREFS_NAME, 0);
            Log.i(TAG, "All information refreshed.");
            settings.edit().clear().commit();
        }

        boolean exitApp = false;
       /* if(!AppUtils.isOnline()){
            exitApp = true;
        }*/

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
            /* New Handler to start the Menu-Activity
            * and close this Splash-Screen after some seconds.*/
           if(SharedPreferencesHelper.isTermAndConditionAccepted(this)){
               launchAppVersionCheck();
           }else{
               showTermAndConditionAgreementDialog();
           }
        }
    }

    private void launchApp(){
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
                    new GroomifyGetUserTask().execute();
                }else{
                    launchOnboardingScreen(SplashActivity.this, true);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void launchAppVersionCheck(){
        new GroomifyCheckAppVersionTask().execute();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferencesHelper.setTermAndConditionAccepted(this);
        launchAppVersionCheck();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        System.exit(0);
    }

    private class GroomifyGetUserTask extends AsyncTask<Void, String, UserInfoResponse> {

        @Override
        protected UserInfoResponse doInBackground(Void... params) {
            try {
                String authToken = SharedPreferencesHelper.getAuthToken(SplashActivity.this);
                String fbId = SharedPreferencesHelper.getFbId(SplashActivity.this);

                Response<UserInfoResponse> restResponse = client.getApiService().getUser(fbId, authToken).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling get user api success");
                    return restResponse.body();
                }else{
                    if(restResponse.code() == 401){
                        Log.i(TAG, "Calling API returned with error 401. Possible user auth token expired. Request for new login.");
                        requireRelog = true;
                    }
                    Log.i(TAG, "Calling get user api failed, response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to call get user api.",e);
                FirebaseCrash.logcat(Log.ERROR, TAG, "Unable to call get user api.");
                FirebaseCrash.report(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final UserInfoResponse userInfoResponse) {
            super.onPostExecute(userInfoResponse);

            if(userInfoResponse != null) {
                Log.i(TAG, "API return user info: "+userInfoResponse.toString());
                Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                try {
                    innerRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            GroomifyUser groomifyUser = realm.where(GroomifyUser.class).equalTo("id", new Long(userInfoResponse.getId())).findFirst();

                            if(groomifyUser == null){
                                groomifyUser = new GroomifyUser();
                                groomifyUser.setId(new Long(userInfoResponse.getId())); //Unsafe if user id null.
                            }

                            Log.i(TAG, "Before update user info. GroomifyUser: ("+groomifyUser+")");
                            groomifyUser.setAuthToken(userInfoResponse.getAuthToken());
                            groomifyUser.setCountry(userInfoResponse.getCountry());
                            groomifyUser.setEmail(userInfoResponse.getEmail());
                            groomifyUser.setEmergencyContactName(userInfoResponse.getEmergencyContactPerson());
                            groomifyUser.setEmergencyContactPhoneNo(userInfoResponse.getEmergencyContactPhone());
                            groomifyUser.setFacebookId(userInfoResponse.getFbId());

                            groomifyUser.setLastRank(userInfoResponse.getLastRank());
                            groomifyUser.setName(userInfoResponse.getName());
                            groomifyUser.setPhoneNo(userInfoResponse.getPhoneNo());
                            groomifyUser.setTotalRuns(userInfoResponse.getNumberOfRuns());
                            groomifyUser.setProfilePictureUrl(userInfoResponse.getProfilePicture().getUrl());
                            if(userInfoResponse.getProfilePicture() != null && userInfoResponse.getProfilePicture().getUrl() != null && userInfoResponse.getProfilePicture().getUrl().trim().length() > 0){
                                SharedPreferencesHelper.savePreferences(SplashActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_PROFILE_PIC_UPDATED, true);
                            }

                            groomifyUser.setFacebookId(userInfoResponse.getFbId());

                            if (Profile.getCurrentProfile() != null && Profile.getCurrentProfile().getName() != null) {
                                groomifyUser.setFacebookDisplayName(Profile.getCurrentProfile().getName());
                            }
                            groomifyUserRealmObj = realm.copyToRealmOrUpdate(groomifyUser);
                            Log.i(TAG, "Groomify user in database: "+groomifyUserRealmObj.toString());

                            SharedPreferencesHelper.savePreferences(SplashActivity.this, SharedPreferencesHelper.PreferenceValueType.LONG, AppConstant.PREFS_USER_ID, new Long(userInfoResponse.getId()));

                            launchNextScreen();
                        }
                    });
                }finally {
                    innerRealm.close();
                }

            }else{
                if(requireRelog){
                    launchNextScreen();
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            totalAttemp++;
                            if (totalAttemp <= 3) {
                                new GroomifyGetUserTask().execute();
                            } else {
                                promptRetryLogin();
                            }
                        }}, 5000);
                }
            }
        }
    }

    private class GroomifyCheckAppVersionTask extends AsyncTask<Void, String, CheckAppVersionResponse> {

        @Override
        protected CheckAppVersionResponse doInBackground(Void... params) {

            Response<CheckAppVersionResponse> restResponse = null;
            try {
                restResponse = client.getApiService().checkVersion().execute();
                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling check version api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling check version api failed, response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Calling check app version API failed.", e);
                FirebaseCrash.logcat(Log.ERROR, TAG, "Calling check app version API failed.");
                FirebaseCrash.report(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(CheckAppVersionResponse checkAppVersionResponse) {
            super.onPostExecute(checkAppVersionResponse);
            if(checkAppVersionResponse != null){
                PackageManager manager = SplashActivity.this.getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getPackageName(), 0);
                    String version = info.versionName;
                    Log.i(TAG, "Current app version: "+version+". Minimum app version from API: "+checkAppVersionResponse.getAndroidVersion());
                    Version currentVersion = new Version(version);
                    Version targetVersion = new Version(checkAppVersionResponse.getAndroidVersion());
                    if(currentVersion.compareTo(targetVersion) < 0){
                        promptForceUpdate();
                    }else{
                        launchApp();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Unable to obtain current app version.",e );
                    FirebaseCrash.logcat(Log.ERROR, TAG, "Unable to obtain current app version.");
                    FirebaseCrash.report(e);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            promptRetryCheckAppVersion();
                        }
                    }, 5000);

                }
            }else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        promptRetryCheckAppVersion();
                    }
                }, 5000);
            }



        }
    }

    private void showToast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(SplashActivity.this, "Initializing...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchNextScreen(){
        if(requireRelog){
            launchOnboardingScreen(SplashActivity.this, true);
            return;
        }

        SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);

        boolean profileUpdated = settings.getBoolean(AppConstant.PREFS_PROFILE_PIC_UPDATED, false);
        boolean teamSelected = settings.getBoolean(AppConstant.PREFS_TEAM_SELECTED, false);
        boolean runSelected = settings.getBoolean(AppConstant.PREFS_RUN_SELECTED, false);

        boolean alreadySetup = settings.getBoolean(AppConstant.PREFS_FIRST_TIME_SETUP_COMPLETE, false);

        Log.i(TAG,"profileUpdated: "+profileUpdated+", teamSelected: "+teamSelected+", runSelected: "+runSelected+", alreadySetup: "+alreadySetup);



        if(alreadySetup){
            launchMainScreen(SplashActivity.this, true);
        }else{
            if(!runSelected){
                launchRaceSelectionScreen(SplashActivity.this, true);
            }else if(!profileUpdated){
                launchWelcomeScreen(SplashActivity.this, true);
            }else if(!teamSelected){
                launchTeamSelectionScreen(SplashActivity.this, true);
            }else{
                launchRaceSelectionScreen(SplashActivity.this, true);
                //launchMainScreen(SplashActivity.this, true);
            }
        }

        //TODO check is facebook login success and groomify failed. If failed perform groomify login here.

    }

    private void showTermAndConditionAgreementDialog(){
        Log.i(TAG, "Show term and condition dialog.");
        DialogFragment dialog = new TermAndConditionDialogFragment();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "TermAndConditionDialogFragment");
    }

    private void promptRetryLogin(){

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Unable to access Groomify service now. Please ensure you have internet access.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new GroomifyGetUserTask().execute();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .show();
    }

    private void promptRetryCheckAppVersion(){
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Unable to access Groomify service now. Please ensure you have internet access.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new GroomifyCheckAppVersionTask().execute();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .show();
    }

    private void promptForceUpdate(){

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("App update required")
                .setMessage("This version of Groomify is no longer supported.\nPlease update to latest version.")
                .setPositiveButton("Update", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                }) .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                }
        }).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}