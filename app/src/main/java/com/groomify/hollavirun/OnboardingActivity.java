package com.groomify.hollavirun;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class OnboardingActivity extends AppCompatActivity {

    private final static String TAG = OnboardingActivity.class.getSimpleName();

    LoginButton faceLoginButton = null;
    Button loginButton = null;


    CallbackManager callbackManager;
    ProfileTracker mProfileTracker;

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
                Log.i(TAG, "login button on click");
                faceLoginButton.performClick();
            }
        });

        callbackManager = CallbackManager.Factory.create();

        faceLoginButton.setReadPermissions("email");

        faceLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);
                // App code
                Log.i(TAG, "On facebook login success callback. Login status: " +loginResult.getRecentlyGrantedPermissions().toString());

                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                            launchWelcomeScreen();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    launchWelcomeScreen();
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


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

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
