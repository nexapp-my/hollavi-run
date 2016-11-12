package com.groomify.hollavirun.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.groomify.hollavirun.QRActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.utils.ProfileImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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


    private OnFragmentInteractionListener mListener;

    public static final int QR_REQUEST = 111;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICTURE_FROM_GALLERY = 102;
    private static final int REQUEST_PICTURE_FROM_FACEBOOK = 103;
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
            unlocked = mission.isUnlocked();
        }
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
                        MissionDetailsFragment.this.getActivity().onBackPressed();
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

        //hardcoded now, figure out later
        //Bitmap bm = BitmapFactory.decodeResource(getResources(), mission.getMissionImageId());
        //missionBannerImgView.setImageBitmap(bm);

        Log.i(TAG, "Mission details: "+mission.toString());

        if(mission.getMissionNumber() == 1){
            missionBannerImgView.setImageResource(R.drawable.mission_banner_01);
        }else if(mission.getMissionNumber() == 2){
            missionBannerImgView.setImageResource(R.drawable.mission_banner_02);
        }else if(mission.getMissionNumber() == 3){
            missionBannerImgView.setImageResource(R.drawable.mission_banner_03);
        }else if(mission.getMissionNumber() == 4){
            missionBannerImgView.setImageResource(R.drawable.mission_banner_04);
        }else if(mission.getMissionNumber() == 5) {
            missionBannerImgView.setImageResource(R.drawable.mission_banner_05);
        }else{
            missionBannerImgView.setImageResource(R.drawable.mission_banner_06);
        }
        missionNumberTxtView.setText(mission.getMissionNumberString());
        missionTitleTxtView.setText(mission.getMissionTitle());
        missionDescTxtView.setText(mission.getMissionDesc());

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
                scanQRButton.setAlpha(.5f);
            }else{
                scanQRButton.setEnabled(false);
                scanQRButton.setAlpha(1f);
            }

        }
    }

    public void requestQRCodeScan(View v) {
        Intent qrScanIntent = new Intent(getContext(), QRActivity.class);
        startActivityForResult(qrScanIntent, QR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);

        if (requestCode == QR_REQUEST) {
            String result = "";
            if (resultCode == Activity.RESULT_OK) {
                unlocked = true;
                toggleMissionPanel();
                result = "Mission unlocked. QR Data:";
                result += data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
            else {
                result = "Error";
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
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
        }

        if(checkIsMissionReadyToSubmit()){
            scanQRButton.setEnabled(true);
        }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICTURE_FROM_GALLERY);
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
        File storageDir = this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

        Bitmap processedBitmap = ProfileImageUtils.cropBitmap(targetH,targetW,bitmap);

        missionSubmissionImageView.setImageBitmap(processedBitmap);

        //proceedTextView.setText("Next");
        //flagProfilePictureSelected();
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

        Bitmap bitmap = ProfileImageUtils.cropBitmap(targetH,targetW,mCurrentPhotoPath);

        missionSubmissionImageView.setImageBitmap(bitmap);

        //proceedTextView.setText("Next");
        //flagProfilePictureSelected();
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
