package com.groomify.hollavirun;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.groomify.hollavirun.view.ProfilePictureView;


public class WelcomeActivity extends AppCompatActivity {

    private final static String TAGNAME = "WelcomeActivity";
    public static ProfilePictureView profilePictureView;
    public static String facebookUserId;

    ImageView addProfileImageView;
    TextView usernameTextView;
    TextView proceedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if(profilePictureView == null){
            profilePictureView = (ProfilePictureView) findViewById(R.id.welcome_screen_fb_profile_picture);
            profilePictureView.setVisibility(View.INVISIBLE);
        }

        if(addProfileImageView == null){
            addProfileImageView = (ImageView) findViewById(R.id.image_view_add_profile_pic);

        }

        if(usernameTextView == null){
            usernameTextView = (TextView) findViewById(R.id.welcome_header);
        }

        if(proceedTextView == null){
            proceedTextView = (TextView) findViewById(R.id.welcome_proceed_text);
        }



        addProfileImageView.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(WelcomeActivity.this, "Add profile picture clicked", Toast.LENGTH_SHORT);

                Log.i(TAGNAME, "Add profile picture clicked");

                profilePictureView.setVisibility(View.VISIBLE);
                addProfileImageView.setVisibility(View.INVISIBLE);
                proceedTextView.setText("Next");
            }
        });

        proceedTextView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(proceedTextView.getText().equals("Next")){
                    launchTeamSelectScreen();
                }
            }
        });
        pullFacebookProfilePicture();


    }

    private void pullFacebookProfilePicture(){
        if(AccessToken.getCurrentAccessToken() != null){
            facebookUserId = AccessToken.getCurrentAccessToken().getUserId();
            //(String) savedInstanceState.get("facebookUserId");

            if(facebookUserId != null && facebookUserId.length() > 0){
                if(Profile.getCurrentProfile() != null){

                    Log.i(TAGNAME, "Name: "+Profile.getCurrentProfile().getName());
                    Log.i(TAGNAME, "Email: "+getIntent().getExtras().getString("email"));
                    Log.i(TAGNAME, "Token ID: "+AccessToken.getCurrentAccessToken().getToken());
                    Log.i(TAGNAME, "User ID: "+AccessToken.getCurrentAccessToken().getUserId());
                    Log.i(TAGNAME, "App ID: "+AccessToken.getCurrentAccessToken().getApplicationId());

                    usernameTextView.setAllCaps(true);
                    usernameTextView.setText("HELLO " +Profile.getCurrentProfile().getName());

                    profilePictureView.setProfileId(facebookUserId);
                    profilePictureView.setDrawingCacheEnabled(true);
                    Log.i(TAGNAME, "Profile photo acquired");


                }

            }
        }

        /*profilePictureView = (ProfilePictureView) findViewById(R.id.user_profile_picture);

        TextView usernameTextView;
        usernameTextView = (TextView) findViewById(R.id.welcome_user_name);

        TextView welcomeTextView;
        welcomeTextView = (TextView) findViewById(R.id.welcome_header);

        if(AccessToken.getCurrentAccessToken() != null){
            facebookUserId = AccessToken.getCurrentAccessToken().getUserId();
            //(String) savedInstanceState.get("facebookUserId");

            if(facebookUserId != null && facebookUserId.length() > 0){
                if(Profile.getCurrentProfile() != null){

                    Log.i(TAGNAME, "Name: "+Profile.getCurrentProfile().getName());
                    Log.i(TAGNAME, "Email: "+getIntent().getExtras().getString("email"));
                    Log.i(TAGNAME, "Token ID: "+AccessToken.getCurrentAccessToken().getToken());
                    Log.i(TAGNAME, "User ID: "+AccessToken.getCurrentAccessToken().getUserId());
                    Log.i(TAGNAME, "App ID: "+AccessToken.getCurrentAccessToken().getApplicationId());

                    usernameTextView.setText(Profile.getCurrentProfile().getName());
                    usernameTextView.startAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.slide_in_right));

                    welcomeTextView.startAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.slide_in_left));
                    Log.i(TAGNAME, "Acquiring profile photo: "+Profile.getCurrentProfile().getProfilePictureUri(120, 120));
                    profilePictureView.setProfileId(facebookUserId);
                    profilePictureView.setDrawingCacheEnabled(true);
                    Log.i(TAGNAME, "Profile photo acquired");
                }

            }
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent intent;
                intent = new Intent(WelcomeActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        }, 3000);*/
    }


    private void launchTeamSelectScreen(){
        Log.i(TAGNAME, "Team selection screen launched");
        Intent intent;
        intent = new Intent(WelcomeActivity.this, TeamSelectActivity.class);

        startActivity(intent);
        finish();
    }
}
