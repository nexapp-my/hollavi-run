package com.groomify.hollavirun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.groomify.hollavirun.adapter.GridViewImageAdapter;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.Gallery;
import com.groomify.hollavirun.rest.models.response.RaceGalleryResponse;
import com.groomify.hollavirun.rest.models.response.SearchRunnerLocationResponse;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.GalleryUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class RunGalleryActivity extends AppCompatActivity {

    private static final String TAG = RunGalleryActivity.class.getSimpleName();
    public static final int REMOVE_FILE_REQUEST_CODE = 100;
    public static final  String REQUIRE_RELOAD_LOCAL_FILE = "REQUIRE_RELOAD_LOCAL_FILE";
    public static final String DELETED_FILE = "DELETED_FILE";
    private List<String> imagePaths = new ArrayList<String>();
    private List<String> localImagePaths = new ArrayList<String>();
    private List<String> missionImagePaths = new ArrayList<String>();
    private List<String> remoteImagePaths = new ArrayList<String>();
    private Map<String, String> localFilePathMap = new HashMap<>();

    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private Toolbar toolbar;
    public EditText editText;
    private AlertDialog loadingDialog;

    private Storage storage;
    private Mission[] missions;
    private Long userId;

    private RestClient client = new RestClient();

    private int maxBibNo = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_gallery);

        storage = SimpleStorage.getInternalStorage(this);

        missions = AppUtils.getDefaultMission();
        userId = SharedPreferencesHelper.getUserId(this);
        gridView = (GridView) findViewById(R.id.grid_view);

        maxBibNo = getResources().getInteger(R.integer.max_bib_no);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editText = (EditText) findViewById(R.id.search_runner_field);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxBibNo);
        editText.setFilters(filterArray);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) v;
                editText.setHint("");
                editText.setText("");
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    EditText editText = (EditText) v;
                    editText.setHint("");
                    editText.setText("");
                }else{
                    InputMethodManager imm = (InputMethodManager) RunGalleryActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        for(Mission mission: missions){
            String directoryName = "Groomify_"+userId+"_Mission_"+mission.getId();
            List<File> files = storage.getFiles(directoryName, OrderType.NAME);
            Log.i(TAG, "Searching mission "+mission.getId()+" directory: "+directoryName);

            for(File file: files){
                Log.i(TAG, "File found, pushing to list:"+file.getAbsolutePath());
                missionImagePaths.add(file.getAbsolutePath());
            }
        }

        loadInAppCapturedFile();

        combineAllPath();

        // Initilizing Grid View
        initilizeGridLayout();

        // loading all image paths from SD card
        //imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new GridViewImageAdapter(RunGalleryActivity.this, imagePaths,columnWidth, localFilePathMap);

        // setting grid view adapter
        gridView.setAdapter(adapter);
        loadingDialog = DialogUtils.buildLoadingDialog(this);

    }

    private void loadInAppCapturedFile(){
        List<File> cameraFiles = storage.getFiles(AppConstant.CAMERA_IMAGE_DIRECTORY, OrderType.DATE);
        localFilePathMap.clear();
        localImagePaths.clear();
        for(File file: cameraFiles){
            localFilePathMap.put(file.getAbsolutePath(), file.getAbsolutePath());
            localImagePaths.add(file.getAbsolutePath());
        }
    }

    private void combineAllPath(){
        imagePaths.clear();

        imagePaths.addAll(remoteImagePaths);
        imagePaths.addAll(localImagePaths);
        imagePaths.addAll(missionImagePaths);

    }

    private void initilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((GalleryUtils.getScreenWidth(this) - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    private void performSearch(String bibNo){
        editText.clearFocus();
        changeViewState(true);
        new GroomifyGetRunnerGalleryTask().execute(bibNo);
    }

    private void notifyDataChanged(){
        for(String imagePath: imagePaths){
            Log.i(TAG, "Image path: "+imagePath);
        }
        adapter = new GridViewImageAdapter(RunGalleryActivity.this, imagePaths,columnWidth, localFilePathMap);
        adapter.notifyDataSetChanged();
        // setting grid view adapter
        gridView.setAdapter(adapter);

    }


    private class GroomifyGetRunnerGalleryTask extends AsyncTask<String, Void, RaceGalleryResponse> {

        @Override
        protected RaceGalleryResponse doInBackground(String... params) {

            String authToken = SharedPreferencesHelper.getAuthToken(RunGalleryActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(RunGalleryActivity.this);
            Long raceId = SharedPreferencesHelper.getSelectedRaceId(RunGalleryActivity.this);
            String bibNo = params[0];

            try {
                Response<RaceGalleryResponse> restResponse = client.getApiService().getRaceGallery(fbId, authToken, raceId.toString(), bibNo, null).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling search runner gallery api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling search runner gallery api failed. response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to call search runner gallery api.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(RaceGalleryResponse raceGalleryResponse) {
            changeViewState(false);
            super.onPostExecute(raceGalleryResponse);

            if (raceGalleryResponse != null) {

                //TODO put into list, let the array list do the job.
                remoteImagePaths.clear();
                for(Gallery gallery: raceGalleryResponse.getGallery()){
                    if(gallery.getPhoto().getContent().getUrl() != null )
                    remoteImagePaths.add(gallery.getPhoto().getContent().getUrl());
                }
                Collections.sort(remoteImagePaths, Collections.reverseOrder());

                imagePaths.clear();
                imagePaths.addAll(remoteImagePaths);
                imagePaths.addAll(localImagePaths);

                notifyDataChanged();


            } else {
                Toast.makeText(RunGalleryActivity.this, "Runner doesn't have any photo at this moment. Please try again later.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REMOVE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data != null){
                boolean requireReload  = data.getBooleanExtra(REQUIRE_RELOAD_LOCAL_FILE, false);
                if(requireReload){
                    loadInAppCapturedFile();
                    combineAllPath();
                    notifyDataChanged();
                }
            }
        }
    }
}
