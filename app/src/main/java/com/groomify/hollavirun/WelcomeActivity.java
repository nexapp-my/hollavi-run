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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.PathUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.view.ProfilePictureView;

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


public class WelcomeActivity extends AppCompatActivity {

    private final static String TAGNAME= "WelcomeActivity";
    public static ProfilePictureView profilePictureView;
    public static String facebookUserId;

    ImageView addProfileImageView;
    TextView usernameTextView;
    TextView proceedTextView;

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICTURE_FROM_GALLERY = 102;
    private static final int REQUEST_PICTURE_FROM_FACEBOOK = 103;
    private static final int PERMISSIONS_REQUEST = 100;

    private Bitmap profilePictureBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        /*if(profilePictureView == null){
            profilePictureView = (ProfilePictureView) findViewById(R.id.welcome_screen_fb_profile_picture);
            profilePictureView.setVisibility(View.INVISIBLE);
            profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
        }*/

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
        usernameTextView.setText("HELLO " +Profile.getCurrentProfile().getName());


        addProfileImageView.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Toast.makeText(WelcomeActivity.this, "Add profile picture clicked", Toast.LENGTH_SHORT).show();

                Log.i(TAGNAME, "Add profile picture clicked");

                /*profilePictureView.setVisibility(View.VISIBLE);
                addProfileImageView.setVisibility(View.INVISIBLE);
                proceedTextView.setText("Next");*/

                prompPictureSelectionDialog();
            }
        });

        proceedTextView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(proceedTextView.getText().equals("Next")){
                    //TODO upload photo should do here.
                    flagProfilePictureSelected();
                    launchTeamSelectScreen();
                }else{
                    flagProfilePictureSelected();
                    launchTeamSelectScreen();
                }
            }
        });
        //pullFacebookProfilePicture();
    }

    private void flagProfilePictureSelected(){
        if(profilePictureBitmap == null){
            Log.i(TAGNAME, "User skip profile picture selection.");
            profilePictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_avatar);
        }
        saveProfilePictureToDisk(profilePictureBitmap);

        SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("profile_updated", true);

        // Commit the edits!
        editor.commit();
    }

    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;

    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAGNAME, "Upload profile picture, option "+which+" selected.");
                        if(which == OPTION_CAMERA){
                            if(isPermissionGranted()){
                                dispatchTakePictureIntent();
                            }else{
                                requestPermission();
                            }
                        }else if(which == OPTION_GALLERY){
                            if(isPermissionGranted()){
                                dispatchSelectPhotoIntent();
                            }else{
                                requestPermission();
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i(TAGNAME, "mCurrentPhotoPath: "+mCurrentPhotoPath);
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
                Log.e(TAGNAME, "Unable to retrieve the provided root.", e);
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
                finish();
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

        Log.i(TAGNAME, "onActivityResult, requestCode: "+requestCode+", resultCode:"+resultCode+", Data:"+data);
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                setPic();
                Bitmap temp = BitmapFactory.decodeFile(mCurrentPhotoPath);

               profilePictureBitmap = BitmapUtils.cropBitmap(300,300, temp);

            }else if (requestCode == REQUEST_PICTURE_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                setPicFromBitmap(bitmap);
                profilePictureBitmap = BitmapUtils.cropBitmap(300,300, bitmap);



            }
        }catch (Exception e){
            Log.e(TAGNAME, "Failed to capture image return from intent.", e);
        }
    }

    private void setPicFromBitmap(Bitmap bitmap){
        int targetW = addProfileImageView.getWidth();
        int targetH = addProfileImageView.getHeight();

        Bitmap processedBitmap = ProfileImageUtils.processOptimizedRoundBitmap(targetH,targetW,bitmap);

        addProfileImageView.setImageBitmap(processedBitmap);

        proceedTextView.setText("Next");
        flagProfilePictureSelected();
    }

    private void setPic() {
        int targetW = addProfileImageView.getWidth();
        int targetH = addProfileImageView.getHeight();

        Bitmap bitmap = ProfileImageUtils.processOptimizedRoundBitmap(targetH,targetW,mCurrentPhotoPath);

        addProfileImageView.setImageBitmap(bitmap);

        proceedTextView.setText("Next");
        flagProfilePictureSelected();
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
            Log.e(TAGNAME, "Unable to save file to",e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*private void pullFacebookProfilePicture(){
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
    }*/


    private void launchTeamSelectScreen(){
        Log.i(TAGNAME, "Team selection screen launched");
        Intent intent;
        intent = new Intent(WelcomeActivity.this, TeamSelectActivity.class);

        startActivity(intent);
        finish();
    }
}
