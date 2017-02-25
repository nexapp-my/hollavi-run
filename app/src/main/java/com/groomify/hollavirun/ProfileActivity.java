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
import com.facebook.login.LoginManager;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.realm.Realm;

public class ProfileActivity extends AppCompatActivity {
    private ImageView pictureView;
    private TextView userDisplayNameTextView;
    private ImageView closeProfileButton;
    private TextView countryTextView;
    private TextView numberOfRunsTextView;
    private TextView userBibNoTextView;

    private final String TAG = ProfileActivity.class.getSimpleName();

    View logoutTouch = null;

    View editProfileTouch = null;

    View sponsorsTouch = null;


    public static final int REQUEST_CODE_EDIT_PROFILE = 100;
    public static final int RESULT_REQUIRE_LOGOUT = 2;
    public static final int RESULT_PROFILE_UPDATED = 3;

    private Realm realm;
    private GroomifyUser groomifyUser;

    private boolean profileUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Realm.init(this);
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());
        groomifyUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(this)).findFirst();

        ImageLoadUtils.initImageLoader(this);

        if(userDisplayNameTextView == null){
            userDisplayNameTextView = (TextView) findViewById(R.id.user_display_name);
        }

        if(pictureView == null)
            pictureView = (ImageView) findViewById(R.id.profile_picture_image_view);

        if(logoutTouch == null){
            logoutTouch = findViewById(R.id.select_logout);
        }

        if(countryTextView == null) {
            countryTextView = (TextView) findViewById(R.id.country_text_view);
        }
        if(numberOfRunsTextView == null){
            numberOfRunsTextView = (TextView) findViewById(R.id.total_runs_text_view);
        }

        if(userBibNoTextView == null){
            userBibNoTextView = (TextView) findViewById(R.id.user_bib_no_text_view);
        }

        userDisplayNameTextView.setText(groomifyUser.getName());
        userBibNoTextView.setText("BIB No: "+SharedPreferencesHelper.getBibNo(this));

        if(groomifyUser.getTotalRuns() != null && groomifyUser.getTotalRuns() > 0){
            numberOfRunsTextView.setText(""+groomifyUser.getTotalRuns());
        }

        if(groomifyUser.getCountry() != null){
            countryTextView.setText(groomifyUser.getCountry());
        }else{
            countryTextView.setVisibility(View.INVISIBLE);
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

        if(sponsorsTouch == null) {
            sponsorsTouch = findViewById(R.id.select_sponsors_and_partner);
        }

        sponsorsTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSponsorsScreen();
            }
        });

        loadProfilePicture();
    }

    private void launchEditProfileScreen(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
    }

    private void launchSponsorsScreen(){
        Intent intent = new Intent(this, SponsorsActivity.class);
        startActivity(intent);
    }


    private void loadProfilePicture()
    {
        try{

            ImageLoader.getInstance().loadImage(groomifyUser.getProfilePictureUrl(), ImageLoadUtils.getDisplayImageOptions() ,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    Log.i(TAG, "LoadedImage: "+loadedImage.getConfig().toString());
                    Bitmap processedBitmap = ProfileImageUtils.processOptimizedRoundBitmap(30,30, loadedImage);
                    pictureView.setImageBitmap(processedBitmap);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

        }catch (Exception e){
            Log.i(TAG, "Faild to load profile picture.", e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode:"+requestCode+", resultCode: "+resultCode+", data:"+data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_EDIT_PROFILE){
            if(resultCode == EditProfileActivity.PROFILE_UPDATED){
                profileUpdated = true;
                groomifyUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(this)).findFirst();
                //TODO setname and the profile picture.
                userDisplayNameTextView.setText(groomifyUser.getName());
                countryTextView.setText(groomifyUser.getCountry());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(profileUpdated){
            setResult(RESULT_PROFILE_UPDATED);
        }
        super.onBackPressed();
    }
}
