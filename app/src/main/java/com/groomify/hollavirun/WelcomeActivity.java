package com.groomify.hollavirun;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.FbUser;
import com.groomify.hollavirun.rest.models.request.UpdateUserInfoRequest;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.PathUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.groomify.hollavirun.view.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Response;


public class WelcomeActivity extends AppCompatActivity {

    private final static String TAG = "WelcomeActivity";

    private ImageView addProfileImageView;
    private TextView usernameTextView;
    private TextView proceedTextView;
    private ProgressBar progressBar;
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;

    private Bitmap profilePictureBitmap = null;

    private Realm realm;

    private RestClient client = new RestClient();

    private final boolean DEBUG_FACEBOOK_GRAPH_API = false;

    private AlertDialog loadingDialog;
    private Storage storage;
    private String directoryName = Environment.DIRECTORY_DCIM + "/Groomify";
    private String mCurrentPhotoPath;
    private String profilePictureBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());

        ImageLoadUtils.initImageLoader(this);
        setContentView(R.layout.activity_welcome);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        GroomifyUser groomifyUser = realm.where(GroomifyUser.class).findFirst();

        if(progressBar == null){
            progressBar = (ProgressBar) findViewById(R.id.profile_pic_loading_circle);
        }

        progressBar.setVisibility(View.GONE);

        if(addProfileImageView == null){
            addProfileImageView = (ImageView) findViewById(R.id.image_view_add_profile_pic);
        }

        if(usernameTextView == null){
            usernameTextView = (TextView) findViewById(R.id.welcome_header);
        }

        if(proceedTextView == null){
            proceedTextView = (TextView) findViewById(R.id.welcome_proceed_text);
        }

        usernameTextView.setAllCaps(true);

        if(groomifyUser != null && groomifyUser.getName() != null){
            usernameTextView.setText("HELLO " +groomifyUser.getName());
        }else{
            usernameTextView.setText("HELLO " +Profile.getCurrentProfile().getName());
        }

        addProfileImageView.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Add profile picture clicked");

                /*profilePictureView.setVisibility(View.VISIBLE);
                addProfileImageView.setVisibility(View.INVISIBLE);
                proceedTextView.setText("Next");*/
                prompPictureSelectionDialog();
            }
        });

        if(DEBUG_FACEBOOK_GRAPH_API || groomifyUser.getProfilePictureUrl() == null || groomifyUser.getProfilePictureUrl().trim().length() == 0){
            pullFacebookProfilePicture(groomifyUser.getFacebookId());
        }else{
            renderPreviousProfilePicture(groomifyUser.getProfilePictureUrl());

        }
        proceedTextView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(proceedTextView.getText().equals("Next")){
                    //TODO upload photo should do here.
                    new GroomifyUpdateProfileTask().execute();

                }else{
                    //User skip upload photo.
                    flagProfilePictureSelected();
                }
            }
        });

        loadingDialog = DialogUtils.buildLoadingDialog(this);

        if (storage == null){
            if(SimpleStorage.isExternalStorageWritable()){
                storage = SimpleStorage.getExternalStorage();
            }else{
                storage = SimpleStorage.getInternalStorage(WelcomeActivity.this);
            }
        }
    }

    private void flagProfilePictureSelected(){
        if(profilePictureBitmap == null){
            Log.i(TAG, "User skip profile picture selection.");
            profilePictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_avatar);
        }
        //saveProfilePictureToDisk(profilePictureBitmap);
        if(!storage.isDirectoryExists(directoryName)){
            storage.createDirectory(directoryName, false);
        }

        if(mCurrentPhotoPath != null) {
            File imageFile = new File(mCurrentPhotoPath);
            storage.copy(imageFile, directoryName, imageFile.getName());
        }
        //File targetFile = storage.getFile(directoryName, imageFile.getName());

        SharedPreferencesHelper.savePreferences(WelcomeActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_PROFILE_PIC_UPDATED, Boolean.TRUE);
        launchTeamSelectScreen();

    }



    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Upload profile picture, option "+which+" selected.");
                        if(!AppPermissionHelper.isCameraPermissionGranted(WelcomeActivity.this) || !AppPermissionHelper.isStoragePermissionGranted(WelcomeActivity.this)){
                            AppPermissionHelper.requestCameraAndStoragePermission(WelcomeActivity.this);
                            return;
                        }

                        if(which == OPTION_CAMERA){
                            EasyImage.openCamera(WelcomeActivity.this, 0);
                        }else if(which == OPTION_GALLERY){
                            EasyImage.openGallery(WelcomeActivity.this, 0);
                        }
                    }
                });
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    recreate();
                }
            } else {
                Toast.makeText(this, "This application needs Camera permission to take photo", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode:"+resultCode+", Data:"+data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                Log.e(TAG, "onImagePickerError", e);
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(WelcomeActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    private  void onPhotosReturned(File imageFile){

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        Bitmap temp = BitmapFactory.decodeFile(mCurrentPhotoPath);
        int dimension = AppUtils.getPixelFromDIP(this, 300);
        profilePictureBitmap = BitmapUtils.cropBitmap(dimension,dimension, temp);
        profilePictureBase64 = BitmapUtils.convertToBase64(profilePictureBitmap);
        setPictureThumnail();
    }

    private void setPicFromBitmap(Bitmap bitmap){
        int targetW = AppUtils.getPixelFromDIP(this, 120);
        int targetH = AppUtils.getPixelFromDIP(this, 120);

        Log.i(TAG, "Original bitmap dimension: "+bitmap.getWidth()+", "+bitmap.getHeight());
        Bitmap processedBitmap = ProfileImageUtils.processOptimizedRoundBitmap(targetW,targetH,bitmap);
        addProfileImageView.setImageBitmap(processedBitmap);
        proceedTextView.setText("Next");
    }

    private void setPictureThumnail() {
        int targetW = AppUtils.getPixelFromDIP(this, 120);
        int targetH = AppUtils.getPixelFromDIP(this, 120);

        Bitmap bitmap = ProfileImageUtils.processOptimizedRoundBitmap(targetH,targetW,mCurrentPhotoPath);
        addProfileImageView.setImageBitmap(bitmap);
        proceedTextView.setText("Next");
    }

    private void pullFacebookProfilePicture(Long fbId){

        String url = "https://graph.facebook.com/" + fbId + "/picture?type=large";
        ImageLoader.getInstance().loadImage(url, ImageLoadUtils.getDisplayImageOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d(TAG, "Render url into bitmap.");
                profilePictureBitmap = loadedImage.copy(loadedImage.getConfig(), true);
                profilePictureBase64 = BitmapUtils.convertToBase64(profilePictureBitmap);
                setPicFromBitmap(profilePictureBitmap);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        /*new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/{user-id}/picture?type=large",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d(TAG, "GraphResponse"+response.getRawResponse());
                            String url = response.getJSONObject().getJSONObject("data").getString("url");
                            Log.d(TAG, "URL from graph API: "+url);
                            //ImageLoader.getInstance().displayImage(url, addProfileImageView, ImageLoadUtils.getDisplayImageOptions());
                            //Log.d(TAG, "Render url into image view.");
                            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    Log.d(TAG, "Render url into bitmap.");
                                    profilePictureBitmap = loadedImage.copy(loadedImage.getConfig(), true);
                                    profilePictureBase64 = ProfileImageUtils.convertToBase64(profilePictureBitmap);
                                    setPicFromBitmap(profilePictureBitmap);
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {

                                }
                            });

                        } catch (JSONException e) {
                            Log.e(TAG, "Unable to get facebook profile image url.",e);
                        }
                    }


                }
        ).executeAsync();*/
    }

    private void renderPreviousProfilePicture(String url){

        ImageLoader.getInstance().loadImage(url, ImageLoadUtils.getDisplayImageOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d(TAG, "Render url into bitmap.");
                profilePictureBitmap = loadedImage.copy(loadedImage.getConfig(), true);
                profilePictureBase64 = BitmapUtils.convertToBase64(profilePictureBitmap);
                setPicFromBitmap(profilePictureBitmap);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    private class GroomifyUpdateProfileTask extends AsyncTask<Void, String, UserInfoResponse> {

        @Override
        protected UserInfoResponse doInBackground(Void... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(WelcomeActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(WelcomeActivity.this);
            Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
            GroomifyUser realmUser = innerRealm.where(GroomifyUser.class).findFirst();

            changeViewState(true);
            try {
                Log.i(TAG, "Update profile picture to back-end system..");
                UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
                FbUser fbUser = new FbUser();
                fbUser.setProfilePicture(profilePictureBase64);
                updateUserInfoRequest.setFbUser(fbUser);
                Log.i(TAG, "Request ready to post:"+updateUserInfoRequest);

                Response<UserInfoResponse> restResponse = client.getApiService().updateUser(fbId, authToken, realmUser.getId(), updateUserInfoRequest).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling update user api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling update user api failed. response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to call update user api.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserInfoResponse userInfoResponse) {
            //TODO get the runner_id

            super.onPostExecute(userInfoResponse);
            if (userInfoResponse != null) {
                Log.i(TAG, "Update user info success, returning:" + userInfoResponse.toString());
                Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                innerRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        GroomifyUser realmUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(WelcomeActivity.this)).findFirst();
                        realmUser.setProfilePictureBase64(profilePictureBase64);
                    }
                });
                flagProfilePictureSelected();

            } else {
                Toast.makeText(WelcomeActivity.this, "Unable to change profile picture at this moment. Please try again later.", Toast.LENGTH_SHORT).show();
                changeViewState(false);
            }


        }
    }

    private void changeViewState(final boolean loading){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loading){
                    //progressBar.setVisibility(View.VISIBLE);
                    //proceedTextView.setVisibility(View.GONE);
                    loadingDialog.show();
                }else{
                    //progressBar.setVisibility(View.GONE);
                    //proceedTextView.setVisibility(View.VISIBLE);
                    loadingDialog.hide();
                }
            }
        });
    }

    private void launchTeamSelectScreen(){

        Log.i(TAG, "Team selection screen launched");
        Intent intent;
        intent = new Intent(WelcomeActivity.this, TeamSelectActivity.class);

        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Setting not saved, confirm to exit groomify?")
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
