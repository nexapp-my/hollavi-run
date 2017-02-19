package com.groomify.hollavirun;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.entities.Ranking;
import com.groomify.hollavirun.fragment.CouponsListFragment;
import com.groomify.hollavirun.fragment.MainFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.fragment.MissionListFragment;
import com.groomify.hollavirun.fragment.RankingListFragment;
import com.groomify.hollavirun.fragment.dummy.MissionContent;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.Info;
import com.groomify.hollavirun.rest.models.response.JoinRaceResponse;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.rest.models.response.RaceRankingResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Response;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class MainActivity extends AppCompatActivity
        implements
        MissionListFragment.OnListFragmentInteractionListener,
        RankingListFragment.OnFragmentInteractionListener
{

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    //private static final int PERMISSIONS_REQUEST = 100;
    private static final int REQUEST_PROFILE_LOGOUT = 102;

    private View topMenuBar;


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
    private ImageView couponMenuIcon;

    private TextView alertText;
    private View alertBanner;

    //public MainFragment mainFragment = new MainFragment();
    //public MissionFragment missionFragment = new MissionFragment();
    //public CouponsListFragment couponsListFragment = new CouponsListFragment();

    private View.OnClickListener granPermissionListener = null;
    private View.OnClickListener updateProfileListener = null;

    private final String ALERT_TEXT_REQUIRE_PERMISSION = "Location permission is required.";
    private final String ALERT_UPDATE_PROFILE = "Please update your profile";

    private RestClient client = new RestClient();
    private Realm realm;
    GroomifyUser groomifyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        Log.i(TAG,  "In the Main activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);
        groomifyUser = realm.where(GroomifyUser.class).findFirst();

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        if(menuBarLogo == null)
            menuBarLogo = (ImageView) findViewById(R.id.menu_bar_gromify_logo);

        if(menuBarGreetingText == null)
            menuBarGreetingText = (TextView) findViewById(R.id.menu_bar_greeting_text);

        if(pictureView == null)
            pictureView = (ImageView) findViewById(R.id.menu_bar_profile_picture);


        if(topMenuBar == null){
            topMenuBar = findViewById(R.id.main_menu_bar_top);
            /*Log.i(TAG, " rootMenuBar: "+rootMenuBar);

            topMenuBar = rootMenuBar.findViewById(R.id.menu_bar_top);
            Log.i(TAG, " topMenuBar: "+topMenuBar);*/
        }

        if(alertBanner == null){
            alertBanner = findViewById(R.id.alert_banner);
        }

        alertBanner.setVisibility(View.GONE);
        if(alertText == null){
            alertText = (TextView) findViewById(R.id.alert_text);
        }

        menuBarLogo.setVisibility(ImageView.INVISIBLE);
        menuBarGreetingText.setVisibility(TextView.VISIBLE);
        pictureView.setVisibility(View.VISIBLE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_placeholder, new MainFragment()).commit();

        menuBarGreetingText.setText("Good Morning, " +groomifyUser.getName());

        pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                startActivityForResult(profileIntent, REQUEST_PROFILE_LOGOUT);
            }
        });

        initializeMenuBarListener();
        loadProfilePicture();
        toggleMenuState();
        initializeBannerOnClickListener();


        if(!AppPermissionHelper.isLocationPermissionGranted(getApplicationContext())){
            Log.i(TAG, "Location permission is not granted, showing alert banner.");
            alertText.setText(ALERT_TEXT_REQUIRE_PERMISSION);
            alertBanner.setVisibility(View.VISIBLE);
            alertBanner.setOnClickListener(granPermissionListener);
        }else{
            alertBanner.setVisibility(View.GONE);
        }

        Long raceId = SharedPreferencesHelper.getSelectedRaceId(this);
        new GroomifyRaceInfoTask().execute(""+raceId);
        new GroomifyMissionRankingTask().execute(""+raceId);


    }



    private void initializeBannerOnClickListener(){
        granPermissionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPermissionHelper.requestLocationPermission(MainActivity.this);
            }
        };

        updateProfileListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Update profile", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void loadProfilePicture()
    {
        try{
            ImageLoader.getInstance().displayImage(groomifyUser.getProfilePictureUrl(), pictureView, ImageLoadUtils.getDisplayImageOptions());
        }catch (Exception e){
            Log.i(TAG, "Faild to load profile picture.");
        }

    }

    int[] menusId = {
            R.id.menu_home,
            R.id.menu_mission,
            R.id.menu_camera,
            R.id.menu_coupons,
            R.id.menu_sos
    };
    String mCurrentPhotoPath;

    private void initializeMenuBarListener(){

        homeMenuIcon = (ImageView) findViewById(R.id.menu_home_image_view);
        missionMenuIcon = (ImageView) findViewById(R.id.menu_mission_image_view);
        couponMenuIcon = (ImageView) findViewById(R.id.menu_coupon_image_view);


        homeMenu = findViewById(menusId[0]);
        missionMenu = findViewById(menusId[1]);
        cameraButton = findViewById(menusId[2]);
        couponMenu = findViewById(menusId[3]);
        sosMenu = findViewById(menusId[4]);

        homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 0 ){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    //mainFragment = new MainFragment();
                    ft.replace(R.id.main_placeholder, new MainFragment()).commit();
                   /* if(currentFragment != null){
                        ft.remove(currentFragment);
                    }
                    ft.add(R.id.main_placeholder, mainFragment).commit();*/
                    //currentFragment = mainFragment;
                    currentMenuIndex = 0;
                    toggleMenuState();
                }
            }
        });

        missionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 1){
                    try {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        //missionFragment = new MissionFragment();
                        ft.replace(R.id.main_placeholder, new MissionFragment()).commit();
                        /*if (currentFragment != null) {
                            ft.remove(currentFragment);
                        }
                        ft.add(R.id.main_placeholder, missionFragment).commit();*/
                        //currentFragment = missionFragment;
                        currentMenuIndex = 1;
                        toggleMenuState();
                    }catch (Exception ex){
                        Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>", ex);
                    }
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppPermissionHelper.isCameraPermissionGranted(MainActivity.this)){
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
                    AppPermissionHelper.requestPermission(MainActivity.this);
                }
            }
        });

        couponMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Coupon clicked", Toast.LENGTH_SHORT).show();
                if(currentMenuIndex != 3){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    //couponsListFragment = new CouponsListFragment();
                    ft.replace(R.id.main_placeholder, new CouponsListFragment()).commit();
                    /*if(currentFragment != null){
                        ft.remove(currentFragment);
                    }
                    ft.add(R.id.main_placeholder,  couponsListFragment).commit();*/
                    //currentFragment = couponsListFragment;
                    currentMenuIndex = 3;
                    toggleMenuState();
                }

            }
        });

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

    }

    private void toggleMenuState(){
        homeMenuIcon.setImageResource(R.drawable.ic_menu_home);
        missionMenuIcon.setImageResource(R.drawable.ic_menu_missions);
        couponMenuIcon.setImageResource(R.drawable.ic_menu_coupons);

        //topMenuBar.setVisibility(View.VISIBLE);

        switch (currentMenuIndex){
            case 0:
                homeMenuIcon.setImageResource(R.drawable.ic_menu_home_filled);
                break;
            case 1:
                missionMenuIcon.setImageResource(R.drawable.ic_menu_mission_filled);
                break;
            case 3:
                couponMenuIcon.setImageResource(R.drawable.ic_coupons_filled);
                //topMenuBar.setVisibility(View.GONE);
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

  /*  private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult, requestCode:"+requestCode+", permissions:"+Arrays.toString(permissions)+", grantResults:"+Arrays.toString(grantResults));

        boolean successGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if(successGranted){
            Log.i(TAG, "Permission granted.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Log.i(TAG, "Recreating activity.");

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                },2000);*/
                if(MainFragment.googleMap != null){
                    MainFragment.googleMap.setMyLocationEnabled(true);
                    alertBanner.setVisibility(View.GONE);
                }


            }
        }else{
            if (requestCode == AppPermissionHelper.PERMISSIONS_CAMERA_AND_STOAGE_REQUEST_CODE) {
                Toast.makeText(this, "This application require camera and storage permission to take photo.", Toast.LENGTH_LONG).show();
            }else if(requestCode == AppPermissionHelper.PERMISSIONS_LOCATION_REQUEST_CODE){
                Toast.makeText(this, "This application require to access your location for optimal performance.", Toast.LENGTH_LONG).show();
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


    //TODO might need a thread periodicaly pull the news.
    private class GroomifyRaceInfoTask extends AsyncTask<String, String, RaceInfoResponse> {

        @Override
        protected RaceInfoResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(MainActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(MainActivity.this);
            try {
                Response<RaceInfoResponse> restResponse = client.getApiService().raceInfo(fbId, authToken, params[0]).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling race news api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling race news api failed, race id: "+params[0]+", response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to get race news.",e);
                Toast.makeText(MainActivity.this, "Unable to get race detail.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(RaceInfoResponse raceInfoResponses) {
            super.onPostExecute(raceInfoResponses);

            //TODO save race info into database.
            if(raceInfoResponses != null){

                realm.beginTransaction();
                realm.delete(NewsFeed.class);//truncate the tables.
                List<Info> infos = raceInfoResponses.getInfos();
                Log.i(TAG, "Total news: "+infos.size());


                for(int i =0; i < infos.size(); i++){
                    NewsFeed newsFeed = realm.createObject(NewsFeed.class, i + 1);
                    newsFeed.setContent(infos.get(i).getContent());
                    newsFeed.setHeader(infos.get(i).getTitle());
                    newsFeed.setTimeStamp("1 min ago");//TODO missing timestamp
                    newsFeed.setCoverPhotoUrl(infos.get(i).getCover().getCover().getUrl());

                    realm.copyToRealmOrUpdate(newsFeed);
                }

                realm.commitTransaction();

            }
        }
    }

    //TODO might need a thread periodicaly pull the ranking.
    private class GroomifyMissionRankingTask extends AsyncTask<String, String, RaceRankingResponse> {

        @Override
        protected RaceRankingResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(MainActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(MainActivity.this);
            try {
                Response<RaceRankingResponse> restResponse = client.getApiService().raceRanking(fbId, authToken, params[0]).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling race news api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling race news api failed, race id: "+params[0]+", response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to get race news.",e);
                Toast.makeText(MainActivity.this, "Unable to get race detail.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(RaceRankingResponse raceRankingResponse) {
            super.onPostExecute(raceRankingResponse);

            //TODO save race info into database.
            if(raceRankingResponse != null){

                realm.beginTransaction();
                realm.delete(com.groomify.hollavirun.entities.Ranking.class);//truncate the tables.

                Log.i(TAG, "Total ranking size: "+raceRankingResponse.getRankings().size());

                for(int i =0; i < raceRankingResponse.getRankings().size(); i++){
                    com.groomify.hollavirun.entities.Ranking ranking = realm.createObject(com.groomify.hollavirun.entities.Ranking.class, i + 1);
                    ranking.setName(raceRankingResponse.getRankings().get(i).getRunnerName());

                    ranking.setCompletionTime(raceRankingResponse.getRankings().get(i).getTotalMissionTime());
                    ranking.setId(raceRankingResponse.getRankings().get(i).getRunnerBib());
                    ranking.setTeamName(raceRankingResponse.getRankings().get(i).getTeam());

                    Log.i(TAG, "Saving ranking into database: "+ranking.toString());
                    realm.copyToRealmOrUpdate(ranking);
                }


                Log.i(TAG, "Ranking list saved into database. Saving user own ranking into database.");
                final com.groomify.hollavirun.entities.Ranking myRanking = new Ranking();
                myRanking.setRankNumber(raceRankingResponse.getMyRanking().getRanking());
                myRanking.setName(raceRankingResponse.getMyRanking().getRunnerName());
                myRanking.setCompletionTime(raceRankingResponse.getMyRanking().getTotalMissionTime());
                myRanking.setId(raceRankingResponse.getMyRanking().getRunnerBib());
                myRanking.setTeamName(raceRankingResponse.getMyRanking().getTeam());

                final Ranking realmRanking = realm.copyToRealmOrUpdate(myRanking);
                Log.i(TAG, "Saving user ranking into database: "+myRanking.toString());
                realm.commitTransaction();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        GroomifyUser groomifyUser = realm.where(GroomifyUser.class).findFirst();
                        groomifyUser.setMyRanking(realmRanking);
                        Log.i(TAG, "User info from database: "+groomifyUser.toString());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
