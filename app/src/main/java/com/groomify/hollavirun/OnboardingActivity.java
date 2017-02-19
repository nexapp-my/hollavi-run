package com.groomify.hollavirun;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.FbUser;
import com.groomify.hollavirun.rest.models.request.LoginRequest;
import com.groomify.hollavirun.rest.models.response.LoginResponse;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private final static String TAG = OnboardingActivity.class.getSimpleName();

    LoginButton faceLoginButton = null;
    Button loginButton = null;
    ProgressBar progressBar;


    CallbackManager callbackManager;
    ProfileTracker mProfileTracker;

    RestClient client = new RestClient();

    boolean grommifyLoginInProgress = false;

    Context context;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);


        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        context = this.getApplicationContext();

        loginButton = (Button) findViewById(R.id.login_button);
        faceLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                loginButton.setEnabled(false);
                if(AccessToken.getCurrentAccessToken()!=  null){
                    loginToGroomify(AccessToken.getCurrentAccessToken().getUserId());
                    Log.i(TAG, "User already login to facebook, perform Groomify login.");
                }else{
                    Log.i(TAG, "login button on click");
                    faceLoginButton.performClick();
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        faceLoginButton.setReadPermissions("email");

        faceLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // App code
                Log.i(TAG, "On facebook login success callback. Login status: " +loginResult.getRecentlyGrantedPermissions().toString());
                Toast.makeText(context, "Facebook log in success, logging into groomify now...", Toast.LENGTH_SHORT).show();

                SharedPreferencesHelper.savePreferences(getApplicationContext(), SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_USER_LOGGGED_IN, true);
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                            //launchRaceSelectionScreen();

                            loginToGroomify(AccessToken.getCurrentAccessToken().getUserId());
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    //launchRaceSelectionScreen();
                    loginToGroomify(loginResult.getAccessToken().getUserId());
                }
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "On facebook login cancel callback.");
                loginButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException exception) {
                loginButton.setEnabled(true);
                Log.i(TAG, "On facebook login error callback.", exception);
                Toast.makeText(OnboardingActivity.this, "Failed to login with facebook.\n"+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void launchRaceSelectionScreen(){
        Intent sosIntent = new Intent(this, SelectRaceActivity.class);
        startActivity(sosIntent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit Groomify?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void loginToGroomify(String facebookUserId){
        faceLoginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "Login groomify service with facebook id: "+facebookUserId);
        new GroomifyLoginTask().execute(facebookUserId);
    }

    private class GroomifyLoginTask extends AsyncTask<String, String, UserInfoResponse>{

        @Override
        protected UserInfoResponse doInBackground(String... params) {

            Log.i(TAG, "#doInBackground Login to groomify services.");

            try{
                LoginRequest loginRequest = new LoginRequest();
                FbUser fbUser = new FbUser();
                fbUser.setFbId(Long.parseLong(params[0]));
                loginRequest.setFbUser(fbUser);
                Call<UserInfoResponse> loginCall = client.getApiService().loginUser(loginRequest);
                Response<UserInfoResponse> response = loginCall.execute();

                if(response != null && response.code() == 200){
                    Log.i(TAG, "#doInBackground User logged in: "+response.body().toString());
                    SharedPreferencesHelper.savePreferences(OnboardingActivity.this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_FB_ID, ""+response.body().getFbId());
                    SharedPreferencesHelper.savePreferences(OnboardingActivity.this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_AUTH_TOKEN, ""+response.body().getAuthToken());
                    return response.body();
                }else{
                    Log.e(TAG, "#doInBackground API returned HTTP Response code:"+ response.code()+", errors:"+response.errorBody());
                    return null;
                }
            }catch (Exception e){
                Log.e(TAG, "#doInBackground Exception while invoke groomify API service.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserInfoResponse userInfoResponse) {
            if(userInfoResponse != null){
                loginButton.setVisibility(View.GONE);

                realm.beginTransaction();
                GroomifyUser groomifyUser = new GroomifyUser();
                groomifyUser.setAuthToken(userInfoResponse.getAuthToken());
                groomifyUser.setCountry(userInfoResponse.getCountry());
                groomifyUser.setEmail(userInfoResponse.getEmail());
                groomifyUser.setEmergencyContactName(userInfoResponse.getEmergencyContactPerson());
                groomifyUser.setEmergencyContactPhoneNo(userInfoResponse.getEmergencyContactPhone());
                groomifyUser.setFacebookId(userInfoResponse.getFbId());
                groomifyUser.setId(new Long(userInfoResponse.getId())); //Unsafe if user id null.
                groomifyUser.setLastRank(userInfoResponse.getLastRank());
                groomifyUser.setName(userInfoResponse.getName());
                groomifyUser.setPhoneNo(userInfoResponse.getPhoneNo());
                groomifyUser.setTotalRuns(userInfoResponse.getNumberOfRuns());
                groomifyUser.setProfilePictureUrl(userInfoResponse.getProfilePicture().getUrl());
                groomifyUser.setFacebookId(userInfoResponse.getFbId());
                realm.copyToRealmOrUpdate(groomifyUser);
                realm.commitTransaction();
                Log.i(TAG, "User info saved into realm. "+groomifyUser);
                launchRaceSelectionScreen();
            }else{
                loginButton.setEnabled(true);
                Toast.makeText(OnboardingActivity.this, "Failed to login to groomify services", Toast.LENGTH_SHORT).show();
                loginButton.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }
/*private void launchWelcomeScreen(){
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
                            Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
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
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
