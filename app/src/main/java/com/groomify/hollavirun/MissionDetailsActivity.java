package com.groomify.hollavirun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.fragment.MissionDetailsFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.fragment.MissionListFragment;
import com.groomify.hollavirun.fragment.RankingListFragment;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MissionDetailsActivity extends AppCompatActivity {

    private final static String TAG = MissionDetailsActivity.class.getSimpleName();
    //private TabLayout tabLayout;
    //private ViewPager viewPager;
    //MissionDetailsFragment missionDetailFragment;
    Mission mission = null;

    private Toolbar toolbar;


    public static final int QR_REQUEST = 111;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICTURE_FROM_GALLERY = 102;
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;

    private static DecimalFormat decimalFormat = new DecimalFormat("00");

    ImageView imgPlaceHolderOne;
    ImageView imgPlaceHolderTwo;
    ImageView imgPlaceHolderThree;

    Button scanQRButton;

    boolean unlocked = false;

    ImageView missionBannerImgView;
    TextView missionNumberTxtView;
    TextView missionTitleTxtView;
    TextView missionDescTxtView;

    int currentSelectedImage = 1;

    boolean[] missionFilled = {false, false, false};
    String[] originalMissionImagePath = new String[3];

    ShareDialog shareDialog;
    CallbackManager callbackManager;

    String[] verificationCode;
    Storage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras().getBundle("EXTRA_MISSION");
        if (extras != null) {
            mission = extras.getParcelable("MISSION");
        }
        Log.i(TAG, "Mission from main screen: "+mission.toString());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //unlocked = mission.isUnlocked(); //TODO this will be implement in next phase. Currently hardcode at local.
        SharedPreferences settings = this.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        unlocked = settings.getBoolean(AppConstant.PREFS_MISSION_UNLOCK_PREFIX + mission.getId(), false);

        verificationCode = new String[]{
                getResources().getString(R.string.mission1_validation_code),
                getResources().getString(R.string.mission2_validation_code),
                getResources().getString(R.string.mission3_validation_code),
                getResources().getString(R.string.mission4_validation_code),
                getResources().getString(R.string.mission5_validation_code)
        };

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                onBackPressed();
                //TODO save those photo to storage so it is retrivable.
                Toast.makeText(MissionDetailsActivity.this, "Mission shared :)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MissionDetailsActivity.this, "Post cancel :(", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MissionDetailsActivity.this, "Failed to share facebook post: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storage = SimpleStorage.getInternalStorage(this);

        createView();



        /*viewPager = (ViewPager) findViewById(R.id.viewpager);

        Log.i(TAG, "Is view page there? --->"+viewPager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor((R.color.rustyRed)));
        tabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(getResources().getColor((R.color.primaryTextColour)), getResources().getColor((R.color.rustyRed)));*/
    }

    private void createView(){
        scanQRButton = (Button) findViewById(R.id.scan_qr_button);

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unlocked){
                    if(checkIsMissionReadyToSubmit()){
                        //MissionDetailsFragment.this.getActivity().onBackPressed();
                        Log.i(TAG, "Preparing to submit the mission.");
                        if (ShareDialog.canShow(ShareLinkContent.class)){
                           /* ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Welcome to groomify")
                                    .setContentDescription(
                                            "Mission "+mission.getSequenceNumber()+", "+mission.getTitle()+" completed.")
                                    .setImageUrl(Uri.parse("https://static.wixstatic.com/media/318669_facfd24b3dce4767907d5f55cc80b12e.jpg"))
                                    .setContentUrl(Uri.parse("http://www.groomify.com/"))
                                    .build(); shareDialog.show(linkContent);*/

                           SharePhoto photo_0 = new SharePhoto.Builder()
                                    .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[0]))
                                    .build();
                            SharePhoto photo_1 = new SharePhoto.Builder()
                                    .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[1]))
                                    .build();
                            SharePhoto photo_2 = new SharePhoto.Builder()
                                    .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[2]))
                                    .build();

                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo_0)
                                    .addPhoto(photo_1)
                                    .addPhoto(photo_2)
                                    .setShareHashtag(new ShareHashtag.Builder().setHashtag("Groomify").build())
                                    .build();

                            shareDialog.show(content);

                        }
                    }
                }else{
                    Toast.makeText(MissionDetailsActivity.this, "Scan QR to unlock mission.", Toast.LENGTH_SHORT).show();
                    requestQRCodeScan(v);
                }

            }
        });

        imgPlaceHolderOne = (ImageView) findViewById(R.id.add_pic_placeholder1);
        imgPlaceHolderTwo = (ImageView) findViewById(R.id.add_pic_placeholder2);
        imgPlaceHolderThree = (ImageView) findViewById(R.id.add_pic_placeholder3);

        imgPlaceHolderOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Select first picture.", Toast.LENGTH_SHORT).show();
                currentSelectedImage = 1;
                prompPictureSelectionDialog();
            }
        });

        imgPlaceHolderTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Select second picture.", Toast.LENGTH_SHORT).show();
                currentSelectedImage = 2;
                prompPictureSelectionDialog();
            }
        });

        imgPlaceHolderThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Select third picture.", Toast.LENGTH_SHORT).show();
                currentSelectedImage = 3;
                prompPictureSelectionDialog();
            }
        });

        toggleMissionPanel();

        if(missionBannerImgView == null){
            missionBannerImgView = (ImageView) findViewById(R.id.mission_banner_image_view);
        }

        if(missionNumberTxtView == null){
            missionNumberTxtView = (TextView) findViewById(R.id.mission_item_number);
        }

        if(missionTitleTxtView == null){
            missionTitleTxtView = (TextView) findViewById(R.id.mission_item_title);
        }

        if(missionDescTxtView == null){
            missionDescTxtView = (TextView) findViewById(R.id.mission_item_desc);
        }
        Log.i(TAG, "Mission details: "+mission.toString());

        if(mission.getCoverPhotoBase64() != null){
            Bitmap bitmap = BitmapUtils.cropBitmap(missionBannerImgView.getWidth(), missionBannerImgView.getHeight(), ProfileImageUtils.decodeFromBase64ToBitmap(mission.getCoverPhotoBase64()));
            missionBannerImgView.setImageBitmap(bitmap);
            //ImageLoader.getInstance().displayImage(mission.getCoverPhotoUrl(), missionBannerImgView, ImageLoadUtils.getDisplayImageOptions());
        }else if(mission.getCoverPhotoDefaultResourceId() > 0){
            Bitmap bm = BitmapFactory.decodeResource(getResources(), mission.getCoverPhotoDefaultResourceId());
            missionBannerImgView.setImageBitmap(bm);
        }

        missionNumberTxtView.setText(decimalFormat.format(+mission.getSequenceNumber()));
        missionTitleTxtView.setText(mission.getTitle());
        missionDescTxtView.setText(mission.getDescription());
    }

    private void toggleMissionPanel(){
        if(!unlocked){
            imgPlaceHolderOne.setVisibility(View.INVISIBLE);
            imgPlaceHolderTwo.setVisibility(View.INVISIBLE);
            imgPlaceHolderThree.setVisibility(View.INVISIBLE);
            scanQRButton.setText("SCAN QR TO ACTIVE");
            scanQRButton.setEnabled(true);
        }else{
            imgPlaceHolderOne.setVisibility(View.VISIBLE);
            imgPlaceHolderTwo.setVisibility(View.VISIBLE);
            imgPlaceHolderThree.setVisibility(View.VISIBLE);
            scanQRButton.setText("MISSION COMPLETE");
            if(checkIsMissionReadyToSubmit()){
                scanQRButton.setEnabled(true);
            }else{
                scanQRButton.setEnabled(false);
            }

        }
    }

    public void requestQRCodeScan(View v) {
        Intent qrScanIntent = new Intent(this, QRActivity.class);
        qrScanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(qrScanIntent, QR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String qrCodeResult = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                String message = "";
                if(verificationCode[mission.getSequenceNumber() - 1].equals(qrCodeResult)){
                    Log.i(TAG, "Mission verification code match. Mission unlocked.");
                    unlocked = true;
                    SharedPreferencesHelper.savePreferences(MissionDetailsActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_MISSION_UNLOCK_PREFIX + mission.getId(), true);

                    message = "Valid verification code. Mission unlocked";
                    toggleMissionPanel();
                }else{
                    Log.i(TAG, "Mission verification code not match.");
                    message = "Invalid verification code. Please try again.";
                }
                Toast.makeText(MissionDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MissionDetailsActivity.this, "Unable to get QR code.", Toast.LENGTH_SHORT).show();
            }
        }/*else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "Mission image captured.");
            setPic();
            if(checkIsMissionReadyToSubmit()){
                Toast.makeText(this.getContext(), "Mission is ready to submit :) ", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_PICTURE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "Mission image selected.");
            setPicFromBitmap(bitmap);
            if(checkIsMissionReadyToSubmit()){
                Toast.makeText(this.getContext(), "Mission is ready to submit :) ", Toast.LENGTH_SHORT).show();
            }
        }*/

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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MissionDetailsActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });


        if(checkIsMissionReadyToSubmit()){
            scanQRButton.setEnabled(true);
        }


    }


    private void onPhotosReturned(File imageFile){

        /*String directoryName = "Groomify/Mission-"+mission.getId()+"/"+currentSelectedImage;

        if(!storage.isDirectoryExists(directoryName)){
            storage.createDirectory(directoryName, true);
            Log.i(TAG, "Directory not exists, creating directory.");
        }else{
            List<File> files = storage.getFiles("MyDirName", OrderType.DATE);
            for(File file: files){
                try{file.delete();}catch (Exception e){Log.e(TAG,"Failed to delete file:"+file.getAbsolutePath(), e);}
            }
        }
        Log.i(TAG, "File name: "+imageFile.getAbsolutePath());


        String fileName = currentSelectedImage+"_"+imageFile.getName();

        storage.createFile(directoryName, fileName, BitmapUtils.loadBitmapFromFile(600, 400,imageFile.getAbsolutePath()));*/
        Log.i(TAG, "Decoding file: "+imageFile.getAbsolutePath());

        //Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        renderThumnailFromBitmap(imageFile.getAbsolutePath());
        originalMissionImagePath[currentSelectedImage - 1] = imageFile.getAbsolutePath();
        //renderThumnailFromBitmap(BitmapUtils.loadBitmapFromFile(600, 400,imageFile.getAbsolutePath()));

        //String fileName = "Groomify_Mission_"+mission.getId()+"_"+currentSelectedImage+
        //storage.createFile("Groomify", )

    }

    private boolean checkIsMissionReadyToSubmit(){
        if(missionFilled[0] &&  missionFilled[1] && missionFilled[2]){
            return true;
        }else{
            return false;
        }
    }

    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Upload profile picture, option "+which+" selected.");
                        if(!AppPermissionHelper.isCameraPermissionGranted(MissionDetailsActivity.this)){
                            AppPermissionHelper.requestCameraAndStoragePermission(MissionDetailsActivity.this);
                            return;
                        }

                        if(which == OPTION_CAMERA){
                            EasyImage.openCamera(MissionDetailsActivity.this, 0);
                            //dispatchTakePictureIntent();
                        }else if(which == OPTION_GALLERY){
                            EasyImage.openGallery(MissionDetailsActivity.this, 0);
                            //dispatchSelectPhotoIntent();
                        }
                    }
                });
        builder.create().show();
    }

    private void renderThumnailFromBitmap(String filePath){

        ImageView missionSubmissionImageView;

        if(currentSelectedImage == 1){
            missionSubmissionImageView = imgPlaceHolderOne;
            missionFilled[0] = true;
        }else if(currentSelectedImage == 2){
            missionSubmissionImageView = imgPlaceHolderTwo;
            missionFilled[1] = true;
        }else{
            missionSubmissionImageView = imgPlaceHolderThree;
            missionFilled[2] = true;
        }
        //TODO update the thumbnail
        int targetW = missionSubmissionImageView.getWidth();
        int targetH = missionSubmissionImageView.getHeight();

        Bitmap processedBitmap = BitmapUtils.cropBitmap(targetH,targetW,filePath);

        missionSubmissionImageView.setImageBitmap(processedBitmap);
    }















   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);

        if (requestCode == QR_REQUEST) {
            String result;
            if (resultCode == RESULT_OK) {
                result = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);

            } else {
                result = "Error";
            }
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            //mResultTextView.setText(result);
            //mResultTextView.setVisibility(View.VISIBLE);
        }
    }*/

   /* private void setupViewPager(ViewPager viewPager) {
        MissionDetailsActivity.ViewPagerAdapter adapter = new MissionDetailsActivity.ViewPagerAdapter(getSupportFragmentManager());
        missionDetailFragment = MissionDetailsFragment.newInstance(mission);
        adapter.addFragment(missionDetailFragment, "DETAILS");
        //adapter.addFragment(new RankingListFragment(), "RANKINGS");
        viewPager.setAdapter(adapter);
    }*/

   /* class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }*/
}
