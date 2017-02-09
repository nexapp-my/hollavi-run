package com.groomify.hollavirun;

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
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.LoginRequest;
import com.groomify.hollavirun.rest.models.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private final static String TAG = OnboardingActivity.class.getSimpleName();

    LoginButton faceLoginButton = null;
    Button loginButton = null;


    CallbackManager callbackManager;
    ProfileTracker mProfileTracker;

    RestClient client = new RestClient();

    boolean loginComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        loginButton = (Button) findViewById(R.id.login_button);
        faceLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);


        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

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

                SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(AppConstant.PREFS_USER_LOGGGED_IN, true);

                // Commit the edits!
                editor.commit();

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
            }

            @Override
            public void onError(FacebookException exception) {
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
        Log.i(TAG, "Login groomify service with id: "+facebookUserId);
        new GroomifyLoginTask().execute(facebookUserId);
    }

    private class GroomifyLoginTask extends AsyncTask<String, String, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {

            Log.i(TAG, "#doInBackground Login to groomify services.");

            LoginRequest loginRequest = new LoginRequest(params[0]);

            try{
                Call<LoginResponse> loginCall = client.getApiService().loginUser(loginRequest);
                Response<LoginResponse> response = loginCall.execute();
                if(response != null && response.code() == 200){

                    Log.i(TAG, "#doInBackground User logged in: "+response.body().toString());
                    return true;
                }else{
                    Log.e(TAG, "#doInBackground API returned HTTP Response code:"+ response.code()+", errors:"+response.errorBody());
                    return false;
                }
            }catch (Exception e){
                Log.e(TAG, "#doInBackground Exception while invoke groomify API service.", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                loginButton.setVisibility(View.GONE);
                launchRaceSelectionScreen();
            }else{
                Toast.makeText(OnboardingActivity.this, "Failed to login to groomify services", Toast.LENGTH_SHORT).show();
            }
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
    /*private void launchSignUpScreen(){
        Intent intent = new Intent(OnboardingActivity.this, SignUpActivity.class);
        startActivity(intent);
        //finish();
    }

    private void launchWelcomeScreen(){
        Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }*/
}
