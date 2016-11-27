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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.fragment.MainFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.fragment.MissionListFragment;
import com.groomify.hollavirun.fragment.RankingListFragment;
import com.groomify.hollavirun.fragment.dummy.MissionContent;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.view.ProfilePictureView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class MainActivity extends AppCompatActivity
implements
        MissionListFragment.OnListFragmentInteractionListener,
        RankingListFragment.OnFragmentInteractionListener{

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int REQUEST_PROFILE_LOGOUT = 102;


    private ImageView menuBarLogo;
    private TextView menuBarGreetingText;
    private ImageView pictureView;
    public Fragment currentFragment;
    public int currentMenuIndex = 0;

    private View homeMenu;
    private View missionMenu;
    private View cameraButton;
    private View sosMenu;
    private View couponMenu;

    private ImageView homeMenuIcon;
    private ImageView missionMenuIcon;

    public MainFragment mainFragment = new MainFragment();
    public MissionFragment missionFragment = new MissionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        Log.i(TAG,  "In the Main activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        if(menuBarLogo == null)
            menuBarLogo = (ImageView) findViewById(R.id.menu_bar_gromify_logo);

        if(menuBarGreetingText == null)
            menuBarGreetingText = (TextView) findViewById(R.id.menu_bar_greeting_text);

        if(pictureView == null)
            pictureView = (ImageView) findViewById(R.id.menu_bar_profile_picture);




        menuBarLogo.setVisibility(ImageView.INVISIBLE);
        menuBarGreetingText.setVisibility(TextView.VISIBLE);
        pictureView.setVisibility(View.VISIBLE);


        //TODO load all the fragments here.



        if (savedInstanceState == null) {
            currentFragment = new MainFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_placeholder, currentFragment).commit();

        }

        if(Profile.getCurrentProfile() != null){
            menuBarGreetingText.setText("Good Morning, " +Profile.getCurrentProfile().getName());


            //pictureView.setProfileId(Profile.getCurrentProfile().getId());
            //pictureView.setDrawingCacheEnabled(true);
            //Log.i(TAG, "Action bar profile picture loaded");

        }

        pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                startActivityForResult(profileIntent, REQUEST_PROFILE_LOGOUT);
            }
        });

        initializeMenuBarListener();
        loadProfileImageFromStorage();
        toggleMenuState();

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
            Bitmap optimizedProfilePic = ProfileImageUtils.processOptimizedRoundBitmap(30, 30, b);
            pictureView.setImageBitmap(optimizedProfilePic);
            Log.i(TAG, "Action bar profile picture loaded");
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "Unable to find profile picture.", e);
        }

    }

    int[] menusId = {
            R.id.menu_home,
            R.id.menu_mission,
            R.id.menu_camera,
            R.id.menu_coupons,
            R.id.menu_sos
    };

   /* Fragment[] fragments = {
            new MainFragment(),
            new MissionFragment()
    };*/

    String mCurrentPhotoPath;

    private void initializeMenuBarListener(){

        homeMenuIcon = (ImageView) findViewById(R.id.menu_home_image_view);
        missionMenuIcon = (ImageView) findViewById(R.id.menu_mission_image_view);


        homeMenu = findViewById(menusId[0]);

        homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 0 ){

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.remove(currentFragment).add(R.id.main_placeholder, mainFragment).commit();
                    currentFragment = mainFragment;
                    currentMenuIndex = 0;
                    toggleMenuState();
                }
            }
        });

        missionMenu = findViewById(menusId[1]);

        missionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 1){

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    missionFragment = new MissionFragment();

                    ft.remove(currentFragment).add(R.id.main_placeholder,  missionFragment).commit();
                    currentFragment = missionFragment;
                    currentMenuIndex = 1;
                    toggleMenuState();
                }
            }
        });


        sosMenu = findViewById(menusId[4]);

        sosMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(currentMenuIndex != 4){
                    Intent sosIntent = new Intent(v.getContext(), SOSActivity.class);
                    startActivity(sosIntent);

                    currentMenuIndex = 4;
                //}
            }
        });

        cameraButton = findViewById(menusId[2]);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted()){
                    /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }*/

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.i(TAG, "IOException while saving image.", ex);
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                    "com.groomify.hollavirun.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }else{
                    requestPermission();
                }
            }
        });

        couponMenu = findViewById(menusId[3]);
        couponMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Coupon clicked", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void toggleMenuState(){
        homeMenuIcon.setImageResource(R.drawable.ic_menu_home);
        missionMenuIcon.setImageResource(R.drawable.ic_menu_missions);

        switch (currentMenuIndex){
            case 0:
                homeMenuIcon.setImageResource(R.drawable.ic_menu_home_filled);
                break;
            case 1:
                missionMenuIcon.setImageResource(R.drawable.ic_menu_mission_filled);
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Groomify");

        if (! storageDir.exists()){
            if (! storageDir.mkdirs()){
                Log.i(TAG, "failed to create directory");
                return null;
            }
        }
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode:"+requestCode+", resultCode: "+resultCode+", data:"+data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.i(TAG, "Image captured successfully.");
            String result;
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Image captured successfully. Result OK.");
                galleryAddPic();
                Toast.makeText(this, "Image saved to gallery.", Toast.LENGTH_SHORT).show();

            } else {
                result = "Error";
            }

        }else if(requestCode == REQUEST_PROFILE_LOGOUT){
            if(resultCode == ProfileActivity.RESULT_REQUIRE_LOGOUT){

                SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove(AppConstant.PREFS_USER_LOGGGED_IN);
                editor.remove(AppConstant.PREFS_PROFILE_PIC_UPDATED);
                editor.remove(AppConstant.PREFS_TEAM_SELECTED);
                editor.remove(AppConstant.PREFS_RUN_SELECTED);
                editor.remove(AppConstant.PREFS_EMERGENCY_CONTACT_NUM);
                editor.remove(AppConstant.PREFS_EMERGENCY_CONTACT_NAME);



                // Commit the edits!
                editor.commit();

                Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
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
    public void onListFragmentInteraction(MissionContent.MissionItem item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
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
}
