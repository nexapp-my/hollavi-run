package com.groomify.hollavirun;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.FbUser;
import com.groomify.hollavirun.rest.models.request.UpdateUserInfoRequest;
import com.groomify.hollavirun.rest.models.response.RunnerInfoResponse;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();
    public static final int PROFILE_UPDATED = 100;
    public static final int PROFILE_NO_CHANGES = 101;
    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;
    ImageView pictureView = null;
    View saveButton;
    View cancelButton;

    private RestClient client = new RestClient();
    private Realm realm;
    private GroomifyUser groomifyUser;

    private AlertDialog loadingDialog ;

    private EditText fullNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private ImageView editProfilePictureImageView;
    private Spinner countrySpinner;
    private Bitmap profilePictureBitmap;
    private String profilePictureBase64;

    private Storage storage;
    private String directoryName = Environment.DIRECTORY_DCIM + "/Groomify";
    private String mCurrentPhotoPath;

    boolean profileUpdated = false;
    boolean profilePictureUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Realm.init(this);
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());
        groomifyUser = realm.where(GroomifyUser.class).findFirst();

        ImageLoadUtils.initImageLoader(this);

        if(pictureView == null)
            pictureView = (ImageView) findViewById(R.id.profile_picture_image_view);

        if(fullNameEditText == null)
            fullNameEditText = (EditText) findViewById(R.id.full_name_field);

        if(emailEditText == null)
            emailEditText = (EditText) findViewById(R.id.email_field);

        if(phoneNumberEditText == null)
            phoneNumberEditText = (EditText) findViewById(R.id.phone_number_field);

        if(countrySpinner == null)
            countrySpinner = (Spinner) findViewById(R.id.country_spinner);

        if(saveButton == null)
            saveButton = findViewById(R.id.save_text_view);

        if(cancelButton == null)
            cancelButton = findViewById(R.id.cancel_text_view);

        if(editProfilePictureImageView == null)
            editProfilePictureImageView = (ImageView) findViewById(R.id.profile_picture_image_view);

        if(groomifyUser.getName() != null){
            fullNameEditText.setText(groomifyUser.getName());
        }
        if(groomifyUser.getPhoneNo() != null){
            phoneNumberEditText.setText(groomifyUser.getPhoneNo());
        }
        if(groomifyUser.getEmail() != null){
            emailEditText.setText(groomifyUser.getEmail());
        }

        editProfilePictureImageView.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Edit profile picture clicked");
                prompPictureSelectionDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        loadProfilePicture();
        populateCountryListing();

        if(groomifyUser.getCountry() != null){
            int index = AppUtils.getSpinnerIndexByValue(countrySpinner, groomifyUser.getCountry());
            countrySpinner.setSelection(index);
        }

        loadingDialog = DialogUtils.buildLoadingDialog(this);

        if (storage == null){
            if(SimpleStorage.isExternalStorageWritable()){
                storage = SimpleStorage.getExternalStorage();
            }else{
                storage = SimpleStorage.getInternalStorage(EditProfileActivity.this);
            }
        }

    }

    private void save(){
        if(validateInput()){
            new GroomifyUpdateRunnerInfoTask().execute();
        }else{
            Toast.makeText(this, "Please ensure the information entered is correct.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(){

        if(fullNameEditText.getText().toString().trim().length() == 0 ){
            fullNameEditText.setError("Required.");
            return false;
        }

        if(phoneNumberEditText.getText().toString().trim().length() == 0){
            phoneNumberEditText.setError("Required.");
            return false;
        }

        if(emailEditText.getText().toString().trim().length() == 0){
            emailEditText.setError("Required.");
            return false;
        }

        if(!AppUtils.isValidEmail(emailEditText.getText().toString().trim())){
            emailEditText.setError("Invalid email address.");
            return false;
        }

        return true;

    }
    private void cancel(){
        this.onBackPressed();
    }

    private  void populateCountryListing(){
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        countrySpinner.setAdapter(adapter);
    }

    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Edit profile picture, option "+which+" selected.");

                        if(!AppPermissionHelper.isCameraPermissionGranted(EditProfileActivity.this) || !AppPermissionHelper.isStoragePermissionGranted(EditProfileActivity.this)){
                            AppPermissionHelper.requestCameraAndStoragePermission(EditProfileActivity.this);
                            return;
                        }

                        if(which == OPTION_CAMERA){
                            EasyImage.openCamera(EditProfileActivity.this, 0);
                        }else if(which == OPTION_GALLERY){
                            EasyImage.openGallery(EditProfileActivity.this, 0);
                        }
                    }
                });
        builder.create().show();
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
        super.onActivityResult(requestCode, resultCode, data);
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditProfileActivity.this);
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
        profilePictureUpdated = true;
    }

    private void setPictureThumnail() {
        int targetW = AppUtils.getPixelFromDIP(this, 120);
        int targetH = AppUtils.getPixelFromDIP(this, 120);

        Bitmap bitmap = ProfileImageUtils.processOptimizedRoundBitmap(targetH,targetW,mCurrentPhotoPath);
        editProfilePictureImageView.setImageBitmap(bitmap);

    }

    private class GroomifyUpdateRunnerInfoTask extends AsyncTask<Void, String, UserInfoResponse> {
        @Override
        protected UserInfoResponse doInBackground(Void... params) {
            changeViewState(true);

            String authToken = SharedPreferencesHelper.getAuthToken(EditProfileActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(EditProfileActivity.this);
            Long userId = SharedPreferencesHelper.getUserId(EditProfileActivity.this);

            UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
            FbUser fbUser = new FbUser();
            fbUser.setName(fullNameEditText.getText().toString().trim());
            fbUser.setPhoneNo(phoneNumberEditText.getText().toString().trim());
            fbUser.setEmail(emailEditText.getText().toString().trim());
            fbUser.setCountry(countrySpinner.getSelectedItem().toString());
            if(profilePictureUpdated){
                fbUser.setProfilePicture(profilePictureBase64);
            }
            updateUserInfoRequest.setFbUser(fbUser);

            try {
                Response<UserInfoResponse> restResponse = client.getApiService().updateUser(fbId, authToken, userId, updateUserInfoRequest).execute();
                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling update user api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling update user api failed. response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to call update user api.",e);
            }
            changeViewState(false);
            return null;
        }

        @Override
        protected void onPostExecute(final UserInfoResponse userInfoResponse) {
            super.onPostExecute(userInfoResponse);

            if(userInfoResponse == null){
                Toast.makeText(EditProfileActivity.this, "Unable to update your user information at this moment. Please try again.", Toast.LENGTH_SHORT).show();

            }else{
                Log.i(TAG, "Update user info success, returning:" + userInfoResponse.toString());
                Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                innerRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        GroomifyUser realmUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(EditProfileActivity.this)).findFirst();
                        realmUser.setProfilePictureBase64(profilePictureBase64);
                        realmUser.setName(fullNameEditText.getText().toString().trim());
                        realmUser.setPhoneNo(phoneNumberEditText.getText().toString().trim());
                        realmUser.setEmail(emailEditText.getText().toString().trim());
                        realmUser.setCountry(countrySpinner.getSelectedItem().toString());
                        realmUser.setProfilePictureUrl(userInfoResponse.getProfilePicture().getUrl());
                        realm.copyToRealmOrUpdate(realmUser);
                    }
                });
                profileUpdated = true;
                setResult(PROFILE_UPDATED);
                finish();
            }
        }
    }

    private void changeViewState(final boolean loading){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loading){
                    loadingDialog.show();
                }else{
                    loadingDialog.hide();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(profileUpdated){
            setResult(PROFILE_UPDATED);
        }else{
            setResult(PROFILE_NO_CHANGES);
        }
        super.onBackPressed();
    }

    /*private void loadProfileImageFromStorage()
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

    }*/
}
