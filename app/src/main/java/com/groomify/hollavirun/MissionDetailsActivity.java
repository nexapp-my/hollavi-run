package com.groomify.hollavirun;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.crash.FirebaseCrash;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.Team;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.MissionTransaction;
import com.groomify.hollavirun.rest.models.request.MissionTransactionRequest;
import com.groomify.hollavirun.rest.models.request.PhotosAttribute;
import com.groomify.hollavirun.rest.models.response.MissionSubmissionResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Response;

public class MissionDetailsActivity extends AppCompatActivity {

    private final static String TAG = MissionDetailsActivity.class.getSimpleName();
    private final SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);

    private Mission mission = null;

    public static final int QR_REQUEST = 111;

    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_GALLERY = 1;
    private static DecimalFormat decimalFormat = new DecimalFormat("00");

    private RestClient client = new RestClient();
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private Toolbar toolbar;
    private ImageView imgPlaceHolderOne;
    private ImageView imgPlaceHolderTwo;
    private ImageView imgPlaceHolderThree;
    private ImageView missionBannerImgView;
    private TextView missionNumberTxtView;
    private TextView missionTitleTxtView;
    private TextView missionDescTxtView;
    private AlertDialog loadingDialog;
    private Button scanQRButton;
    private View imageSelectionPanel;
    private View questionSelectionpanel;
    private TextView missionQuestionTextView;
    private Spinner answerSpinner;
    private TextView scanAndWinAnnouncerTextView;
    private TextView missionAnswerHint;

    private boolean unlocked = false;
    private boolean submitted = false;

    private int currentSelectedImage = 1;

    private boolean[] missionFilled = {false, false, false};
    private String[] originalMissionImagePath = new String[3];
    private String[] missionBase64 = new String[3];

    private String[] verificationCode;
    private Storage storage;

    private String fileNameOne, fileNameTwo, fileNameThree;
    private String directoryName;

    private Long raceId;
    private String runnerId;
    private Long userId;

    private String[] submittedPhotoLocation = new String[3];

    private Team selectedTeam = null;

    private String qrScanned = null;

    private int answer;

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

        raceId = SharedPreferencesHelper.getSelectedRaceId(this);
        userId = SharedPreferencesHelper.getUserId(this);
        runnerId = SharedPreferencesHelper.getRunnerId(this);
        //unlocked = mission.isUnlocked(); //TODO this will be implement in next phase. Currently hardcode at local.
        unlocked = SharedPreferencesHelper.isMissionUnlocked(this, raceId, mission.getId());
        submitted = SharedPreferencesHelper.isMissionSubmitted(this, raceId, mission.getId());

        fileNameOne = "MISSION_"+mission.getId()+"_1.jpg";
        fileNameTwo = "MISSION_"+mission.getId()+"_2.jpg";
        fileNameThree = "MISSION_"+mission.getId()+"_3.jpg";
        directoryName = "Groomify_"+userId+"_Mission_"+mission.getId();

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
                Toast.makeText(MissionDetailsActivity.this, "Your photo has been shared to facebook.", Toast.LENGTH_SHORT).show();
                submitted = SharedPreferencesHelper.isMissionSubmitted(MissionDetailsActivity.this, raceId, mission.getId());
                toggleMissionPanel();
            }

            @Override
            public void onCancel() {
                //Toast.makeText(MissionDetailsActivity.this, "Post cancel :(", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Failed to share content to facebook.Errors: "+error.getMessage(),error.getCause() );

                //Toast.makeText(MissionDetailsActivity.this, "Failed to share facebook post. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        storage = SimpleStorage.getInternalStorage(this);

        loadingDialog = DialogUtils.buildLoadingDialog(this);

        String sharePrefSelectedTeam = SharedPreferencesHelper.getTeamId(this);
        Log.i(TAG, "Selected team: "+sharePrefSelectedTeam);
        for(Team team: AppUtils.getDefaultTeam()){
            if(team.getTeamName().equals(sharePrefSelectedTeam)){
                selectedTeam = team;
                Log.i(TAG, "Selected team found: "+selectedTeam);
                break;
            }
        }

        createView();
    }



    private void createView(){
        scanQRButton = (Button) findViewById(R.id.scan_qr_button);

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unlocked){
                    if(mission.getType() != Mission.MISSION_TYPE_SCAN_AND_ANSWER_QUESTION){
                        if(checkIsMissionReadyToSubmit()){
                            Log.i(TAG, "Preparing to submit the mission.");
                            new GroomifySubmitMissionTask().execute();
                        }
                    }else{
                        if(answerSpinner.getSelectedItemPosition() == answer){
                            Toast.makeText(MissionDetailsActivity.this, getResources().getString(R.string.mission_toast_answer_correct), Toast.LENGTH_SHORT).show();
                            new GroomifySubmitMissionTask().execute();
                        }else{
                            Toast.makeText(MissionDetailsActivity.this, getResources().getString(R.string.mission_toast_answer_wrong), Toast.LENGTH_SHORT).show();
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
                if(!submitted) {
                    Toast.makeText(v.getContext(), "Select first picture.", Toast.LENGTH_SHORT).show();
                    currentSelectedImage = 1;
                    prompPictureSelectionDialog();
                }else{
                    viewFullScreenPhoto(1);
                }
            }
        });

        imgPlaceHolderTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!submitted) {
                    Toast.makeText(v.getContext(), "Select second picture.", Toast.LENGTH_SHORT).show();
                    currentSelectedImage = 2;
                    prompPictureSelectionDialog();
                }else{
                    viewFullScreenPhoto(2);
                }
            }
        });

        imgPlaceHolderThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!submitted) {
                    Toast.makeText(v.getContext(), "Select third picture.", Toast.LENGTH_SHORT).show();
                    currentSelectedImage = 3;
                    prompPictureSelectionDialog();
                }else{
                    viewFullScreenPhoto(3);
                }
            }
        });

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
            Bitmap bitmap = BitmapUtils.cropBitmap(missionBannerImgView.getWidth(), missionBannerImgView.getHeight(), BitmapUtils.decodeFromBase64ToBitmap(mission.getCoverPhotoBase64()));
            missionBannerImgView.setImageBitmap(bitmap);
            //ImageLoader.getInstance().displayImage(mission.getCoverPhotoUrl(), missionBannerImgView, ImageLoadUtils.getDisplayImageOptions());
        }else if(mission.getCoverPhotoDefaultResourceId() > 0){
            if(mission.getId() == 3){
                if(selectedTeam.getPrefixAlphabet().equals("G")){
                    missionBannerImgView.setImageResource(R.drawable.mission_banner_03_grooton);
                }else if(selectedTeam.getPrefixAlphabet().equals("M")){
                    missionBannerImgView.setImageResource(R.drawable.mission_banner_03_miki);
                }else{
                    missionBannerImgView.setImageResource(R.drawable.mission_banner_03_fyre);
                }
            }else{
                Bitmap bm = BitmapFactory.decodeResource(getResources(), mission.getCoverPhotoDefaultResourceId());
                missionBannerImgView.setImageBitmap(bm);
            }
        }

        missionNumberTxtView.setText(decimalFormat.format(+mission.getSequenceNumber()));
        missionTitleTxtView.setText(mission.getTitle());
        missionDescTxtView.setText(mission.getDescription());

        answerSpinner = (Spinner) findViewById(R.id.answer_spinner);
        imageSelectionPanel = findViewById(R.id.image_selection_panel);
        questionSelectionpanel = findViewById(R.id.question_selection_panel);
        missionQuestionTextView = (TextView) findViewById(R.id.mission_question);
        scanAndWinAnnouncerTextView = (TextView) findViewById(R.id.scan_and_win_announce_text_view);
        missionAnswerHint = (TextView) findViewById(R.id.answer_hint_text_view);

        String[] answerList;
        String question;
        //TODO populate the answer.
        if(selectedTeam.getPrefixAlphabet().equals("G")){
            answerList = getResources().getStringArray(R.array.grooton_answers);
            question = getResources().getString(R.string.mission_question_grooton);
            answer = getResources().getInteger(R.integer.mission_answer_grooton);
        }else if(selectedTeam.getPrefixAlphabet().equals("M")){
            answerList = getResources().getStringArray(R.array.miki_answers);
            question = getResources().getString(R.string.mission_question_miki);
            answer = getResources().getInteger(R.integer.mission_answer_miki);
        }else{
            answerList = getResources().getStringArray(R.array.fyre_answers);
            question = getResources().getString(R.string.mission_question_fyre);
            answer = getResources().getInteger(R.integer.mission_answer_fyre);
        }

        Log.i(TAG, "Answer List: "+ Arrays.toString(answerList));
        Log.i(TAG, "Question: "+ question);
        Log.i(TAG, "Correct answer: "+ question);

        missionQuestionTextView.setText(question);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_layout, answerList);
        answerSpinner.setAdapter(adapter);

        toggleMissionPanel();

    }



    private void toggleMissionPanel() {

        imageSelectionPanel.setVisibility(View.GONE);
        questionSelectionpanel.setVisibility(View.GONE);
        imgPlaceHolderOne.setVisibility(View.INVISIBLE);
        imgPlaceHolderTwo.setVisibility(View.INVISIBLE);
        imgPlaceHolderThree.setVisibility(View.INVISIBLE);

        if(!unlocked) {
            imgPlaceHolderOne.setVisibility(View.INVISIBLE);
            imgPlaceHolderTwo.setVisibility(View.INVISIBLE);
            imgPlaceHolderThree.setVisibility(View.INVISIBLE);
            if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                scanQRButton.setText("SCAN QR TO COMPLETE");
            }else{
                scanQRButton.setText("SCAN QR TO ACTIVE");
            }
            scanQRButton.setEnabled(true);
        }
        else if(submitted){
            scanQRButton.setText("MISSION COMPLETED");
            scanQRButton.setEnabled(false);

            //TODO populate the shit back.

            File missionImageOne = storage.getFile(directoryName, fileNameOne);
            File missionImageTwo = storage.getFile(directoryName, fileNameTwo);
            File missionImageThree = storage.getFile(directoryName, fileNameThree);

            if(missionImageOne.exists()){
                renderThumnailFromBitmap(missionImageOne.getAbsolutePath(), 1);
                submittedPhotoLocation[0] = missionImageOne.getAbsolutePath();
            }

            if(missionImageTwo.exists()){
                renderThumnailFromBitmap(missionImageTwo.getAbsolutePath(), 2);
                submittedPhotoLocation[1] = missionImageTwo.getAbsolutePath();
            }

            if(missionImageThree.exists()){
                renderThumnailFromBitmap(missionImageThree.getAbsolutePath(), 3);
                submittedPhotoLocation[2] = missionImageThree.getAbsolutePath();
            }

            if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                scanAndWinAnnouncerTextView.setVisibility(View.VISIBLE);
            }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_ANSWER_QUESTION){
                questionSelectionpanel.setVisibility(View.VISIBLE);
                answerSpinner.setSelection(answer);
                answerSpinner.setEnabled(false);
                missionAnswerHint.setVisibility(View.INVISIBLE);
            }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO){
                imageSelectionPanel.setVisibility(View.VISIBLE);
                imgPlaceHolderTwo.setVisibility(View.VISIBLE);
            }else{
                imageSelectionPanel.setVisibility(View.VISIBLE);
                imgPlaceHolderOne.setVisibility(View.VISIBLE);
                imgPlaceHolderTwo.setVisibility(View.VISIBLE);
                imgPlaceHolderThree.setVisibility(View.VISIBLE);
            }

        }
        else{
            if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                scanQRButton.setText("MISSION COMPLETED");
            }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_ANSWER_QUESTION){
                questionSelectionpanel.setVisibility(View.VISIBLE);
                scanQRButton.setText("MISSION COMPLETE");
            }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO){
                imageSelectionPanel.setVisibility(View.VISIBLE);
                imgPlaceHolderTwo.setVisibility(View.VISIBLE);
                scanQRButton.setText("MISSION COMPLETE");
            }else{
                imageSelectionPanel.setVisibility(View.VISIBLE);
                imgPlaceHolderOne.setVisibility(View.VISIBLE);
                imgPlaceHolderTwo.setVisibility(View.VISIBLE);
                imgPlaceHolderThree.setVisibility(View.VISIBLE);
                scanQRButton.setText("MISSION COMPLETE");
            }
            if(checkIsMissionReadyToSubmit()){
                scanQRButton.setEnabled(true);
            }else{
                scanQRButton.setEnabled(false);
            }
        }


        /*if (!unlocked) {
            imgPlaceHolderOne.setVisibility(View.INVISIBLE);
            imgPlaceHolderTwo.setVisibility(View.INVISIBLE);
            imgPlaceHolderThree.setVisibility(View.INVISIBLE);
            if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                scanQRButton.setText("SCAN QR TO COMPLETE");
            }else{
                scanQRButton.setText("SCAN QR TO ACTIVE");
            }
            scanQRButton.setEnabled(true);
        }else if(submitted){
            scanQRButton.setText("MISSION COMPLETED");
            scanQRButton.setEnabled(false);

            File missionImageOne = storage.getFile(directoryName, fileNameOne);
            File missionImageTwo = storage.getFile(directoryName, fileNameTwo);
            File missionImageThree = storage.getFile(directoryName, fileNameThree);

            renderThumnailFromBitmap(missionImageOne.getAbsolutePath(), 1);
            renderThumnailFromBitmap(missionImageTwo.getAbsolutePath(), 2);
            renderThumnailFromBitmap(missionImageThree.getAbsolutePath(), 3);
            submittedPhotoLocation[0] = missionImageOne.getAbsolutePath();
            submittedPhotoLocation[1] = missionImageTwo.getAbsolutePath();
            submittedPhotoLocation[2] = missionImageThree.getAbsolutePath();

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

        }*/
    }

    public void requestQRCodeScan(View v) {
        Intent qrScanIntent = new Intent(this, QRActivity.class);
        qrScanIntent.setClassName("com.groomify.run", "com.groomify.hollavirun.QRActivity");
        startActivityForResult(qrScanIntent, QR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String qrCodeResult = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                String message;
                if(validateQRCode(qrCodeResult)){
                    handleQRValidationSuccess();
                    message = "Valid verification code. Mission unlocked";
                }else{
                    Log.i(TAG, "Mission verification code not match.");
                    message = "Invalid verification code. Please try again.";
                }
                Toast.makeText(MissionDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MissionDetailsActivity.this, "Unable to get QR code.", Toast.LENGTH_SHORT).show();
            }
        }

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

    private boolean validateQRCode(String qrCodeResult){
        if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
            Log.i(TAG, "Scan and win mission: QR Code: "+qrCodeResult);
            SharedPreferencesHelper.setMissionFirstAttemptsTime(MissionDetailsActivity.this, raceId, mission.getId(), sdf.format(new Date()));
            String teamVerificationCode = verificationCode[mission.getSequenceNumber() - 1] + "-"+ selectedTeam.getPrefixAlphabet();

            Log.i(TAG, "Scan and win mission: Team QR Code: "+teamVerificationCode);

            if(qrCodeResult.startsWith(teamVerificationCode)){
                qrScanned = qrCodeResult;
                SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_SCAN_AND_WIN_QR_CODE, qrScanned);
                return true;
            }else{
                return false;
            }
        }else{
            return verificationCode[mission.getSequenceNumber() - 1].equals(qrCodeResult);
        }
    }

    private void handleQRValidationSuccess(){
        Log.i(TAG, "Mission verification code match. Mission unlocked.");
        unlocked = true;
        SharedPreferencesHelper.setMissionUnlocked(MissionDetailsActivity.this, raceId, mission.getId(), true);
        SharedPreferencesHelper.setMissionUnlockedTime(MissionDetailsActivity.this, raceId, mission.getId(), sdf.format(new Date()));

        toggleMissionPanel();
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
            new GroomifySubmitMissionTask().execute();
        }
    }

    private void onPhotosReturned(File imageFile){

        Log.i(TAG, "Decoding file: "+imageFile.getAbsolutePath());
        renderThumnailFromBitmap(imageFile.getAbsolutePath(), currentSelectedImage);
        originalMissionImagePath[currentSelectedImage - 1] = imageFile.getAbsolutePath();

    }

    private void saveImagesToStorage(){

        if(!storage.isDirectoryExists(directoryName)){
            storage.createDirectory(directoryName, true);
            Log.i(TAG, "Directory not exists, creating directory.");
        }else{
            Log.i(TAG, "Purging old files.");
            List<File> files = storage.getFiles("MyDirName", OrderType.DATE);
            for(File file: files){
                try{file.delete();}catch (Exception e){Log.e(TAG,"Failed to delete file:"+file.getAbsolutePath(), e);}
            }
        }

        if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_THREE_PHOTO){
            byte[] imageOneByte = BitmapUtils.loadFileToJpegByte(600, 800, originalMissionImagePath[0]);
            byte[] imageThreeByte = BitmapUtils.loadFileToJpegByte(600, 800, originalMissionImagePath[2]);
            if(!storage.createFile(directoryName, fileNameOne, imageOneByte)){
                Log.i(TAG, "Unable to write first mission image file");
            }
            if(!storage.createFile(directoryName, fileNameThree, imageThreeByte)){
                Log.i(TAG, "Unable to write third mission image file");
            }
        }

        byte[] imageTwoByte = BitmapUtils.loadFileToJpegByte(600, 800, originalMissionImagePath[1]);

        if(!storage.createFile(directoryName, fileNameTwo, imageTwoByte)){
            Log.i(TAG, "Unable to write second mission image file");
        }
        Log.i(TAG, "Writing file to storage complete.");
    }

    private boolean checkIsMissionReadyToSubmit(){
        Log.i(TAG, "checkIsMissionReadyToSubmit, type:"+mission.getType());

        if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
            return false; // scan only will auto submit mission
        }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_ANSWER_QUESTION){
            return true;
        }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO){
            return missionFilled[1];
        }else {
            return missionFilled[0] && missionFilled[1] && missionFilled[2];
        }
    }

    private void prompPictureSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(R.array.profile_picture_selection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Upload profile picture, option "+which+" selected.");
                        if(!AppPermissionHelper.isCameraPermissionGranted(MissionDetailsActivity.this) || !AppPermissionHelper.isStoragePermissionGranted(MissionDetailsActivity.this)){
                            AppPermissionHelper.requestCameraAndStoragePermission(MissionDetailsActivity.this);
                            return;
                        }

                        if(which == OPTION_CAMERA){
                            EasyImage.openCamera(MissionDetailsActivity.this, 0);
                        }else if(which == OPTION_GALLERY){
                            EasyImage.openGallery(MissionDetailsActivity.this, 0);
                        }
                    }
                });
        builder.create().show();
    }

    private void renderThumnailFromBitmap(String filePath, int selectedImage){

        ImageView missionSubmissionImageView;

        if(selectedImage == 1){
            Log.i(TAG, "Image 1 filled");
            missionSubmissionImageView = imgPlaceHolderOne;
            missionFilled[0] = true;
        }else if(selectedImage == 2){
            Log.i(TAG, "Image 2 filled");
            missionSubmissionImageView = imgPlaceHolderTwo;
            missionFilled[1] = true;
        }else{
            Log.i(TAG, "Image 3 filled");
            missionSubmissionImageView = imgPlaceHolderThree;
            missionFilled[2] = true;
        }
        int targetW = missionSubmissionImageView.getWidth();
        int targetH = missionSubmissionImageView.getHeight();
        Log.i(TAG, "H/W of view: "+targetH+"/"+targetW);

        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());

        if(targetH == 0|| targetW == 0){
            targetH = dimension;
            targetW = dimension;
        }

        Bitmap processedBitmap = BitmapUtils.cropBitmap(targetH,targetW,filePath);

        missionSubmissionImageView.setImageBitmap(processedBitmap);
    }

    private void viewFullScreenPhoto(int photoNum){
        Intent intent = new Intent(this, FullScreenImageActivity.class);

        if(photoNum == 1){
            intent.putExtra("IMAGE_FILE_PATH", submittedPhotoLocation[photoNum -1]);
        }else if(photoNum == 2){
            intent.putExtra("IMAGE_FILE_PATH", submittedPhotoLocation[photoNum -1]);
        }else{
            intent.putExtra("IMAGE_FILE_PATH", submittedPhotoLocation[photoNum -1]);
        }
        startActivity(intent);
    }

    private class GroomifySubmitMissionTask extends AsyncTask<Void, String, MissionSubmissionResponse> {

        @Override
        protected MissionSubmissionResponse doInBackground(Void... params) {
            changeLoadingState(true);
            runnerId = SharedPreferencesHelper.getRunnerId(MissionDetailsActivity.this);
            Long raceId = SharedPreferencesHelper.getSelectedRaceId(MissionDetailsActivity.this);
            String facebookId = SharedPreferencesHelper.getFbId(MissionDetailsActivity.this);
            String authToken = SharedPreferencesHelper.getAuthToken(MissionDetailsActivity.this);
            String missionUnlockedTime = SharedPreferencesHelper.getMissionUnlockTime(MissionDetailsActivity.this, raceId, mission.getId());
            String missionFirstAttemptTime = SharedPreferencesHelper.getMissionFirstAttemptsTime(MissionDetailsActivity.this, raceId, mission.getId());

            MissionTransactionRequest missionTransactionRequest = new MissionTransactionRequest();
            MissionTransaction missionTransaction = new MissionTransaction();
            missionTransaction.setMissionId(mission.getId());
            missionTransaction.setRunnerId(Integer.parseInt(runnerId));
            Long missionTime = 300L;
            try {
                if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                    if(missionFirstAttemptTime != null) {
                        Date unlockedTime = sdf.parse(missionFirstAttemptTime);
                        missionTime = (new Date().getTime() - unlockedTime.getTime()) / 1000;
                    }
                }else{
                    if(missionUnlockedTime != null){
                        Date unlockedTime = sdf.parse(missionUnlockedTime);
                        missionTime = (new Date().getTime() - unlockedTime.getTime()) / 1000;
                    }
                }

            }catch (Exception e){
                Log.i(TAG, "Failed to parse mission unlocked time.");
            }
            missionTransaction.setMissionTime(missionTime.intValue());

            List<PhotosAttribute> photosAttributeList = new ArrayList<>();

            if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO){
                //Photo attribute
                missionBase64[1] = BitmapUtils.loadFileToJpegBase64(600, 800, originalMissionImagePath[1]);
                photosAttributeList.add(new PhotosAttribute(missionBase64[1], raceId, Long.parseLong(runnerId)));
                missionTransaction.setPhotosAttributes(photosAttributeList);
            }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_THREE_PHOTO){
                //Photo attribute
                missionBase64[0] = BitmapUtils.loadFileToJpegBase64(600, 800, originalMissionImagePath[0]);
                missionBase64[1] = BitmapUtils.loadFileToJpegBase64(600, 800, originalMissionImagePath[1]);
                missionBase64[2] = BitmapUtils.loadFileToJpegBase64(600, 800, originalMissionImagePath[2]);
                photosAttributeList.add(new PhotosAttribute(missionBase64[0], raceId, Long.parseLong(runnerId)));
                photosAttributeList.add(new PhotosAttribute(missionBase64[1], raceId, Long.parseLong(runnerId)));
                photosAttributeList.add(new PhotosAttribute(missionBase64[2], raceId, Long.parseLong(runnerId)));
                missionTransaction.setPhotosAttributes(photosAttributeList);
            }else if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                missionTransaction.setRemark(qrScanned);
            }

            missionTransactionRequest.setMissionTransaction(missionTransaction);

            try {
                Response<MissionSubmissionResponse> restResponse = client.getApiService().submitMissionTransaction(facebookId, authToken, missionTransactionRequest).execute();
                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling update submitMissionTransaction api success");
                    return restResponse.body();
                }else{
                    Log.e(TAG, "Calling update submitMissionTransaction api failed."+restResponse.code());
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while calling submitMissionTransaction api.",e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(MissionSubmissionResponse missionSubmissionResponse) {
            super.onPostExecute(missionSubmissionResponse);
            changeLoadingState(false);
            if(missionSubmissionResponse == null){
                Toast.makeText(MissionDetailsActivity.this, "Unable to submit mission at this moment. Please try again.", Toast.LENGTH_SHORT).show();

                if(mission.getType() == Mission.MISSION_TYPE_SCAN_ONLY){
                    SharedPreferencesHelper.setMissionUnlocked(MissionDetailsActivity.this, raceId, mission.getId(), false);
                    SharedPreferencesHelper.setMissionUnlockedTime(MissionDetailsActivity.this, raceId, mission.getId(), null);
                    unlocked = false;
                    toggleMissionPanel();
                }
            }else{

                Toast.makeText(MissionDetailsActivity.this, "Well done fella. Mission submitted.", Toast.LENGTH_SHORT).show();
                SharedPreferencesHelper.setMissionSubmitted(MissionDetailsActivity.this, raceId, mission.getId(), true);
                submitted = true;
                toggleMissionPanel();

                if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO || mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_THREE_PHOTO){
                    saveImagesToStorage();
                    promptShareToFaceBook();
                }

            }
        }
    }
    private void changeLoadingState(final boolean loading){
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

    private void promptShareToFaceBook(){

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Share this photo to Facebook?")
                .setPositiveButton("Share Now", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ShareDialog.canShow(SharePhotoContent.class)){

                            SharePhotoContent content = null;

                            if(mission.getType() == Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO){
                                SharePhoto photo_1 = new SharePhoto.Builder()
                                        .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[1]))
                                        .build();
                                String hashtag =  MissionDetailsActivity.this.getResources().getString(R.string.facebook_hashtag);
                                content = new SharePhotoContent.Builder()
                                        .addPhoto(photo_1)
                                        .setShareHashtag(new ShareHashtag.Builder().setHashtag(hashtag).build())
                                        .build();
                            }else{
                                SharePhoto photo_0 = new SharePhoto.Builder()
                                        .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[0]))
                                        .build();
                                SharePhoto photo_1 = new SharePhoto.Builder()
                                        .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[1]))
                                        .build();
                                SharePhoto photo_2 = new SharePhoto.Builder()
                                        .setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, originalMissionImagePath[2]))
                                        .build();

                                String hashtag =  MissionDetailsActivity.this.getResources().getString(R.string.facebook_hashtag);
                                content = new SharePhotoContent.Builder()
                                        .addPhoto(photo_0)
                                        .addPhoto(photo_1)
                                        .addPhoto(photo_2)
                                        .setShareHashtag(new ShareHashtag.Builder().setHashtag(hashtag).build())
                                        .build();
                            }
                            shareDialog.show(content);

                        }else{
                            Toast.makeText(MissionDetailsActivity.this, "Your device does not support facebook share.", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .setNegativeButton("Maybe Later", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            unlocked = SharedPreferencesHelper.isMissionUnlocked(this, raceId, mission.getId());
            submitted = SharedPreferencesHelper.isMissionSubmitted(this, raceId, mission.getId());
            toggleMissionPanel();
        }catch (Exception e){
            FirebaseCrash.logcat(Log.ERROR, TAG, "Exception onResume for mission detail activity.");
            FirebaseCrash.report(e);
        }
    }
}
