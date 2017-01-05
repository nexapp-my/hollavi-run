package com.groomify.hollavirun;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.utils.ProfileImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProfileActivity extends AppCompatActivity {
    private ImageView pictureView;
    private TextView userDisplayNameTextView;
    private ImageView closeProfileButton;

    private final String TAG = ProfileActivity.class.getSimpleName();

    View logoutTouch = null;

    View editProfileTouch = null;


    public static final int RESULT_REQUIRE_LOGOUT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        if(userDisplayNameTextView == null){
            userDisplayNameTextView = (TextView) findViewById(R.id.user_display_name);
        }

        if(pictureView == null)
            pictureView = (ImageView) findViewById(R.id.profile_picture_image_view);

        if(logoutTouch == null){
            logoutTouch = findViewById(R.id.select_logout);
        }

        if(Profile.getCurrentProfile() != null){
            String name = Profile.getCurrentProfile().getName();
            userDisplayNameTextView.setText(name);
            //pictureView.setProfileId(Profile.getCurrentProfile().getId());
            //pictureView.setDrawingCacheEnabled(true);
        }

        if(closeProfileButton == null){
            closeProfileButton = (ImageView) findViewById(R.id.close_profile_button);
        }
        closeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        logoutTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
                SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("profile_updated", true);
                

                setResult(RESULT_REQUIRE_LOGOUT);
                ProfileActivity.this.finish();
                //finish();
            }
        });

        if(editProfileTouch == null){
            editProfileTouch = findViewById(R.id.select_edit_my_profile);
        }

        editProfileTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditProfileScreen();

            }
        });

        loadProfileImageFromStorage();
    }

    private void launchEditProfileScreen(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }


    private void loadProfileImageFromStorage()
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        try {
            File f = new File(directory,"profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Bitmap optimizedProfilePic = ProfileImageUtils.processOptimizedRoundBitmap(72, 72, b);
            pictureView.setImageBitmap(optimizedProfilePic);
            Log.i(TAG, "Action bar profile picture loaded");
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "Unable to find profile picture.", e);
        }

    }


}
