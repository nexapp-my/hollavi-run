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
import retrofit2.Response;


public class WelcomeActivity extends AppCompatActivity {

    private final static String TAG = "WelcomeActivity";
    public static ProfilePictureView profilePictureView;
    public static String facebookUserId;

    ImageView addProfileImageView;
    TextView usernameTextView;
    TextView proceedTextView;
    ProgressBar progressBar;

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICTURE_FROM_GALLERY = 102;
    private static final int REQUEST_PICTURE_FROM_FACEBOOK = 103;
    private static final int PERMISSIONS_REQUEST = 100;

    private Bitmap profilePictureBitmap = null;

    private Realm realm;

    RestClient client = new RestClient();

    private final boolean DEBUG_FACEBOOK_GRAPH_API = false;

    private AlertDialog loadingDialog;

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

        /*if(profilePictureView == null){
            profilePictureView = (ProfilePictureView) findViewById(R.id.welcome_screen_fb_profile_picture);
            profilePictureView.setVisibility(View.INVISIBLE);
            profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
        }*/

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
    }

    private String profilePictureBase64;

    private void flagProfilePictureSelected(){
        if(profilePictureBitmap == null){
            Log.i(TAG, "User skip profile picture selection.");
            profilePictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_avatar);
        }
        saveProfilePictureToDisk(profilePictureBitmap);

        SharedPreferencesHelper.savePreferences(WelcomeActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_PROFILE_PIC_UPDATED, Boolean.TRUE);
        launchTeamSelectScreen();

    }

    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;

    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Upload profile picture, option "+which+" selected.");
                        if(which == OPTION_CAMERA){
                            if(AppPermissionHelper.isCameraPermissionGranted(WelcomeActivity.this)){
                                dispatchTakePictureIntent();
                            }else{
                                AppPermissionHelper.requestCameraAndStoragePermission(WelcomeActivity.this);
                            }
                        }else if(which == OPTION_GALLERY){
                            if(AppPermissionHelper.isCameraPermissionGranted(WelcomeActivity.this)){
                                dispatchSelectPhotoIntent();
                            }else{
                                AppPermissionHelper.requestCameraAndStoragePermission(WelcomeActivity.this);
                            }
                        }
                    }
                });
        builder.create().show();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i(TAG, "mCurrentPhotoPath: "+mCurrentPhotoPath);
            } catch (IOException ex) {
               Toast.makeText(this, "Unable to save photo to your phone's storage.",Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            try {
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.groomify.hollavirun.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }catch (Exception e){
                Log.e(TAG, "Unable to retrieve the provided root.", e);
            }
        }
    }

    private void dispatchSelectPhotoIntent(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICTURE_FROM_GALLERY);
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

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "GROOMIFY_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode:"+resultCode+", Data:"+data);
        try {

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Log.i(TAG, "Setup profile picture from camera success.");
                setPic();
                Bitmap temp = BitmapFactory.decodeFile(mCurrentPhotoPath);
                profilePictureBitmap = BitmapUtils.cropBitmap(300,300, temp);
                profilePictureBase64 = ProfileImageUtils.convertToBase64(profilePictureBitmap);
            }else if (requestCode == REQUEST_PICTURE_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Log.i(TAG, "Setup profile picture from gallery success.");
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                setPicFromBitmap(bitmap);
                profilePictureBitmap = BitmapUtils.cropBitmap(300,300, bitmap);
                profilePictureBase64 = ProfileImageUtils.convertToBase64(profilePictureBitmap);
            }

        }catch (Exception e){
            Log.e(TAG, "Failed to capture image return from intent.", e);
        }
    }

    private void setPicFromBitmap(Bitmap bitmap){
        int targetW = addProfileImageView.getWidth();
        int targetH = addProfileImageView.getHeight();

        Log.i(TAG, "Original bitmap dimension: "+bitmap.getWidth()+", "+bitmap.getHeight());
        Bitmap processedBitmap = ProfileImageUtils.processOptimizedRoundBitmap(120,120,bitmap);
        addProfileImageView.setImageBitmap(processedBitmap);
        proceedTextView.setText("Next");
    }

    private void setPic() {
        int targetW = addProfileImageView.getWidth();
        int targetH = addProfileImageView.getHeight();

        Bitmap bitmap = ProfileImageUtils.processOptimizedRoundBitmap(120,120,mCurrentPhotoPath);
        addProfileImageView.setImageBitmap(bitmap);
        proceedTextView.setText("Next");
    }

    private void saveProfilePictureToDisk(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e(TAG, "Unable to save file to",e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                profilePictureBase64 = ProfileImageUtils.convertToBase64(profilePictureBitmap);
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
                profilePictureBase64 = ProfileImageUtils.convertToBase64(profilePictureBitmap);
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
