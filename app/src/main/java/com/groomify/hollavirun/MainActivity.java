package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.entities.Ranking;
import com.groomify.hollavirun.fragment.CouponsListFragment;
import com.groomify.hollavirun.fragment.MainFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.fragment.MissionListFragment;
import com.groomify.hollavirun.fragment.RankingListFragment;
import com.groomify.hollavirun.fragment.dummy.MissionContent;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.Info;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.rest.models.response.RaceRankingResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.AppUtils;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
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
    private SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);

    private static final int REQUEST_IMAGE_CAPTURE = 101;
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

    private View.OnClickListener granPermissionListener = null;
    private View.OnClickListener updateProfileListener = null;

    private final String ALERT_TEXT_REQUIRE_PERMISSION = "Location permission is required.";
    private final String ALERT_UPDATE_PROFILE = "Please update your profile";

    private RestClient client = new RestClient();
    private Realm realm;
    GroomifyUser groomifyUser;

    private Storage storage;

    private String directoryName = Environment.DIRECTORY_DCIM + "/Groomify";

    private boolean isRunAsGuest;

    private Date raceEndDate;
    private Long raceId;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        Log.i(TAG,  "In the Main activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());
        groomifyUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(this)).findFirst();

        ImageLoadUtils.initImageLoader(this);

        isRunAsGuest = SharedPreferencesHelper.isRunAsGuest(this);
        raceId = SharedPreferencesHelper.getSelectedRaceId(this);
        try {
            raceEndDate = sdf.parse(SharedPreferencesHelper.getRaceExpirationTime(this, raceId));
        } catch (ParseException e) {
            Log.e(TAG, "Unable to get race end date.");
        }

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
        mainFragment = new MainFragment();
        ft.replace(R.id.main_placeholder, mainFragment).commit();

        menuBarGreetingText.setText("Hello, " +groomifyUser.getName());

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

            ImageLoader.getInstance().loadImage(groomifyUser.getProfilePictureUrl(),ImageLoadUtils.getDisplayImageOptions() ,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    Log.i(TAG, "LoadedImage: "+loadedImage.getConfig().toString());
                    int pixel = AppUtils.getPixelFromDIP(MainActivity.this, 30);
                    Bitmap processedBitmap = ProfileImageUtils.processOptimizedRoundBitmap(pixel,pixel, loadedImage);
                    pictureView.setImageBitmap(processedBitmap);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            //ImageLoader.getInstance().displayImage(groomifyUser.getProfilePictureUrl(), pictureView, ImageLoadUtils.getDisplayImageOptions());
        }catch (Exception e){
            Log.i(TAG, "Faild to load profile picture.", e);
        }

    }

    int[] menusId = {
            R.id.menu_home,
            R.id.menu_mission,
            R.id.menu_camera,
            R.id.menu_coupons,
            R.id.menu_sos
    };

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
                    mainFragment = new MainFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_placeholder, mainFragment).commit();
                    currentMenuIndex = 0;
                    toggleMenuState();
                }
            }
        });

        missionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 1){

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_placeholder, new MissionFragment()).commit();
                    currentMenuIndex = 1;
                    toggleMenuState();

                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppPermissionHelper.isCameraPermissionGranted(MainActivity.this) && AppPermissionHelper.isStoragePermissionGranted(MainActivity.this)){

                    if (storage == null){

                        if(SimpleStorage.isExternalStorageWritable()){
                            storage = SimpleStorage.getExternalStorage();
                        }else{
                            storage = SimpleStorage.getInternalStorage(MainActivity.this);
                        }
                    }
                    EasyImage.openCamera(MainActivity.this, 0);
                }else{
                    AppPermissionHelper.requestCameraAndStoragePermission(MainActivity.this);
                }
            }
        });

        couponMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentMenuIndex != 3) {
                    if (isRunAsGuest) {
                        Toast.makeText(MainActivity.this, "Guest runner is not entitle for coupon.", Toast.LENGTH_SHORT).show();
                    } else {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.main_placeholder, new CouponsListFragment()).commit();
                        currentMenuIndex = 3;
                        toggleMenuState();
                    }
                }

            }
        });

        sosMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sosIntent = new Intent(v.getContext(), SOSActivity.class);
                startActivity(sosIntent);
                currentMenuIndex = 4;
            }
        });

    }

    private void toggleMenuState(){
        homeMenuIcon.setImageResource(R.drawable.ic_menu_home);
        missionMenuIcon.setImageResource(R.drawable.ic_menu_missions);
        couponMenuIcon.setImageResource(R.drawable.ic_menu_coupons);

        switch (currentMenuIndex){
            case 0:
                homeMenuIcon.setImageResource(R.drawable.ic_menu_home_filled);
                break;
            case 1:
                missionMenuIcon.setImageResource(R.drawable.ic_menu_mission_filled);
                break;
            case 3:
                couponMenuIcon.setImageResource(R.drawable.ic_coupons_filled);
                break;
        }
    }

    private void galleryAddPic() {

        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(directoryName)));
        sendBroadcast(scanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode:"+requestCode+", resultCode: "+resultCode+", data:"+data);
       if(requestCode == REQUEST_PROFILE_LOGOUT){
            if(resultCode == ProfileActivity.RESULT_REQUIRE_LOGOUT){

                SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                // Commit the edits!
                editor.commit();

                Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
            }else if( resultCode == ProfileActivity.RESULT_PROFILE_UPDATED){
                groomifyUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(this)).findFirst();

                menuBarGreetingText.setText("Welcome, " +groomifyUser.getName());
                loadProfilePicture();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                Log.e(TAG, "onImagePickerError", e);
                //e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private  void onPhotosReturned(File imageFile){

        if(!storage.isDirectoryExists(directoryName)){
            storage.createDirectory(directoryName, false);
        }

        storage.copy(imageFile, directoryName, imageFile.getName());

        galleryAddPic();
        File targetFile = storage.getFile(directoryName, imageFile.getName());
        Intent intent = new Intent(getBaseContext(), FullScreenImageActivity.class);
        intent.putExtra("IMAGE_FILE_PATH", targetFile.getAbsolutePath());
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult, requestCode:"+requestCode+", permissions:"+Arrays.toString(permissions)+", grantResults:"+Arrays.toString(grantResults));

        boolean successGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if(successGranted){
            Log.i(TAG, "Permission granted.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Log.i(TAG, "Recreating activity.");
                if(mainFragment != null && mainFragment.getGoogleMap() != null){
                    mainFragment.getGoogleMap().setMyLocationEnabled(true);
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
    public void onListFragmentInteraction(MissionContent.MissionItem item) {}

    @Override
    public void onFragmentInteraction(Uri uri) {}

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
            } catch (Exception e) {
                Log.e(TAG, "Unable to get race news.",e);
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
                    newsFeed.setCoverPhotoUrl(infos.get(i).getCover().getUrl());

                    realm.copyToRealmOrUpdate(newsFeed);
                }

                realm.commitTransaction();

            }else{
                Toast.makeText(MainActivity.this, "Unable to get race news at this moment.", Toast.LENGTH_SHORT).show();
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
            } catch (Exception e) {
                Log.e(TAG, "Unable to get race news.",e);
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
                        GroomifyUser groomifyUser = realm.where(GroomifyUser.class).equalTo("id", SharedPreferencesHelper.getUserId(MainActivity.this)).findFirst();
                        groomifyUser.setMyRanking(realmRanking);
                        Log.i(TAG, "User info from database: "+groomifyUser.toString());
                    }
                });
            }else{
                Toast.makeText(MainActivity.this, "Unable to get race ranking at this moment.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
