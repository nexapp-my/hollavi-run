package com.groomify.hollavirun.fragment;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.groomify.hollavirun.MainActivity;
import com.groomify.hollavirun.QRActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storable;
import com.sromku.simple.storage.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MissionDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MissionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MissionDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM_MISSION = "MISSION";

    // TODO: Rename and change types of parameters
    private Mission mission;
    private static DecimalFormat decimalFormat = new DecimalFormat("00");

    private OnFragmentInteractionListener mListener;

    public static final int QR_REQUEST = 111;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICTURE_FROM_GALLERY = 102;
    private static final int PERMISSIONS_REQUEST = 100;

    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;

    private static final String TAG = MissionDetailsFragment.class.getSimpleName();

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

    ShareDialog shareDialog;
    CallbackManager callbackManager;

    String[] verificationCode;
    Storage storage;

    public MissionDetailsFragment() {
        // Required empty public constructor
    }

    public static MissionDetailsFragment newInstance(Mission mission) {
        MissionDetailsFragment fragment = new MissionDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_MISSION, mission);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mission = getArguments().getParcelable(PARAM_MISSION);
            //unlocked = mission.isUnlocked(); //TODO this will be implement in next phase. Currently hardcode at local.
            SharedPreferences settings = this.getContext().getSharedPreferences(AppConstant.PREFS_NAME, 0);

            unlocked = settings.getBoolean(AppConstant.PREFS_MISSION_UNLOCK_PREFIX + mission.getId(), false);
        }

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
                MissionDetailsFragment.this.getActivity().onBackPressed();
                Toast.makeText(MissionDetailsFragment.this.getActivity(), "Mission shared :)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MissionDetailsFragment.this.getActivity(), "Post cancel :(", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MissionDetailsFragment.this.getActivity(), "Failed to share facebook post: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storage = SimpleStorage.getInternalStorage(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_mission_details, container, false);

        scanQRButton = (Button) view.findViewById(R.id.scan_qr_button);

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
                                    .build();*/


                           /* SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap()
                                    .build();
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();

                            shareDialog.show(linkContent);*/
                        }
                    }
                }else{
                    Toast.makeText(getContext(), "Scan QR to unlock mission.", Toast.LENGTH_SHORT).show();
                    requestQRCodeScan(v);
                }

            }
        });

        imgPlaceHolderOne = (ImageView) view.findViewById(R.id.add_pic_placeholder1);
        imgPlaceHolderTwo = (ImageView) view.findViewById(R.id.add_pic_placeholder2);
        imgPlaceHolderThree = (ImageView) view.findViewById(R.id.add_pic_placeholder3);

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
            missionBannerImgView = (ImageView) view.findViewById(R.id.mission_banner_image_view);
        }

        if(missionNumberTxtView == null){
            missionNumberTxtView = (TextView) view.findViewById(R.id.mission_item_number);
        }

        if(missionTitleTxtView == null){
            missionTitleTxtView = (TextView) view.findViewById(R.id.mission_item_title);
        }

        if(missionDescTxtView == null){
            missionDescTxtView = (TextView) view.findViewById(R.id.mission_item_desc);
        }
        Log.i(TAG, "Mission details: "+mission.toString());

        if(mission.getCoverPhotoBase64() != null){
            Bitmap bitmap = BitmapUtils.cropBitmap(missionBannerImgView.getWidth(), missionBannerImgView.getHeight(), BitmapUtils.decodeFromBase64ToBitmap(mission.getCoverPhotoBase64()));
            missionBannerImgView.setImageBitmap(bitmap);
            //ImageLoader.getInstance().displayImage(mission.getCoverPhotoUrl(), missionBannerImgView, ImageLoadUtils.getDisplayImageOptions());
        }else if(mission.getCoverPhotoDefaultResourceId() > 0){
            Bitmap bm = BitmapFactory.decodeResource(getResources(), mission.getCoverPhotoDefaultResourceId());
            missionBannerImgView.setImageBitmap(bm);
        }

        missionNumberTxtView.setText(decimalFormat.format(+mission.getSequenceNumber()));
        missionTitleTxtView.setText(mission.getTitle());
        missionDescTxtView.setText(mission.getDescription());

        return view;
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
        Intent qrScanIntent = new Intent(getContext(), QRActivity.class);
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
                    SharedPreferencesHelper.savePreferences(MissionDetailsFragment.this.getContext(), SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_MISSION_UNLOCK_PREFIX + mission.getId(), true);

                    message = "Valid verification code. Mission unlocked";
                    toggleMissionPanel();
                }else{
                    Log.i(TAG, "Mission verification code not match.");
                    message = "Invalid verification code. Please try again.";
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Unable to get QR code.", Toast.LENGTH_SHORT).show();
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

        EasyImage.handleActivityResult(requestCode, resultCode, data, MissionDetailsFragment.this.getActivity(), new DefaultCallback() {
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MissionDetailsFragment.this.getContext());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });


        if(checkIsMissionReadyToSubmit()){
            scanQRButton.setEnabled(true);
        }


    }


    private void onPhotosReturned(File imageFile){

        if(!storage.isDirectoryExists("Groomify")){
            storage.createDirectory("Groomify", true);
            Log.i(TAG, "Directory not exists, creating directory.");
        }

        Log.i(TAG, "File name: "+imageFile.getAbsolutePath());

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    //from here on is the dealing with photo etc.

    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Upload profile picture, option "+which+" selected.");
                        if(which == OPTION_CAMERA){
                            if(isPermissionGranted()){
                                //dispatchTakePictureIntent();
                                EasyImage.openCamera(MissionDetailsFragment.this, 0);
                            }else{
                                requestPermission();
                            }
                        }else if(which == OPTION_GALLERY){
                            if(isPermissionGranted()){
                                //dispatchSelectPhotoIntent();
                                EasyImage.openGallery(MissionDetailsFragment.this, 0);
                            }else{
                                requestPermission();
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i(TAG, "mCurrentPhotoPath: "+mCurrentPhotoPath);
            } catch (IOException ex) {
                Toast.makeText(this.getActivity(), "Unable to save photo to your phone's storage.",Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            try {
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this.getContext(), "com.groomify.hollavirun.fileprovider", photoFile);
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
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICTURE_FROM_GALLERY);
        startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.getActivity().recreate();
                }
            } else {
                Toast.makeText(this.getContext(), "This application needs Camera permission to take photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "GROOMIFY_" + timeStamp + "_";
        //File storageDir = this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Groomify");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    private void setPicFromBitmap(Bitmap bitmap){

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

        Bitmap processedBitmap = BitmapUtils.cropBitmap(targetH,targetW,bitmap);

        missionSubmissionImageView.setImageBitmap(processedBitmap);
    }

    private void setPic() {
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

        Bitmap bitmap = BitmapUtils.cropBitmap(targetH,targetW,mCurrentPhotoPath);

        missionSubmissionImageView.setImageBitmap(bitmap);

    }

    private void saveProfilePictureToDisk(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(this.getActivity().getApplicationContext());
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

}
