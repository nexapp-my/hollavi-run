package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.JoinRaceResponse;
import com.groomify.hollavirun.rest.models.response.RaceDetailResponse;
import com.groomify.hollavirun.rest.models.response.RaceResponse;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.groomify.hollavirun.view.ViewPagerCarouselView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Response;

public class SelectRaceActivity extends AppCompatActivity implements ViewPagerCarouselView.OnPageScrolledListener {
    ViewPagerCarouselView viewPagerCarouselView;

    TextView runAsGuestButton;
    View joinRaceButton;
    ProgressBar progressBar;

    int currentPosition = 0;
    private static Races[] races;

    RestClient client = new RestClient();
    long carouselSlideInterval = 999999; // 3 SECONDS
    private static final String TAG = SelectRaceActivity.class.getSimpleName();

    private volatile String bibNo = AppConstant.DEFAULT_BIB_NO;

    private final static int MAX_BIB_NO = 5;

    private DecimalFormat decimalFormat = new DecimalFormat("00");

    //private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Realm.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_race);

        //TODO Currently API does not return badge and minimap, waiting API to enhance.
        Bitmap miniMap = BitmapUtils.cropBitmap(183, 183, BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_mini));
        Bitmap badge = BitmapUtils.cropBitmap(183, 183, BitmapFactory.decodeResource(getResources(), R.drawable.ic_finisher_badge));

        ByteArrayOutputStream miniMapByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream badgeByteArr = new ByteArrayOutputStream();

        miniMap.compress(Bitmap.CompressFormat.PNG, 50, miniMapByteArr);
        badge.compress(Bitmap.CompressFormat.PNG, 50, badgeByteArr);

        viewPagerCarouselView = (ViewPagerCarouselView) findViewById(R.id.carousel_view);
        joinRaceButton = findViewById(R.id.join_race_button);
        runAsGuestButton = (TextView) findViewById(R.id.run_as_guest_button);
        progressBar = (ProgressBar) findViewById(R.id.race_list_loading_circle);

        joinRaceButton.setVisibility(View.GONE);
        runAsGuestButton.setVisibility(View.GONE);

        joinRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptBibNoInputDialog();
            }
        });

        runAsGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRace(true);
            }
        });

        new GroomifyLoadListTask().execute();

    }

    private void joinRace(boolean asGuest){
        Races race = SelectRaceActivity.races[currentPosition];

        if(asGuest){
            SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_BIB_NO, AppConstant.DEFAULT_BIB_NO);
        }else{
            SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_BIB_NO, bibNo);
        }

        new GroomifyJoinRaceTask().execute(""+race.getId());
    }

    private void launchWelcomeScreen(){

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void promptBibNoInputDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter your bib number");
        // Set up the input
        final EditText input = new EditText(this);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(MAX_BIB_NO);
        input.setFilters(filterArray);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(input);
        // set dialog message
        builder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (input.getText().length() > 0 && input.getText().toString().trim().length() > 0) {
                                    bibNo = input.getText().toString();
                                    //joinRace();
                                    populateRaceDetails();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = builder.create();
        // show it
        alertDialog.show();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    ((AlertDialog) alertDialog).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    ((AlertDialog) alertDialog).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

    }

    private void populateRaceDetails(){
        Races race = SelectRaceActivity.races[currentPosition];
        new GroomifyLoadRaceDetailTask().execute(""+race.getId());
    }

    private class GroomifyLoadListTask extends AsyncTask<Void, String, List<RaceResponse>> {

        @Override
        protected List<RaceResponse> doInBackground(Void... params) {
            Log.i(TAG, "#doInBackground Load to groomify race list.");
            try{

                String authToken = SharedPreferencesHelper.getAuthToken(SelectRaceActivity.this);
                String fbId = SharedPreferencesHelper.getFbId(SelectRaceActivity.this);

                Log.i(TAG, "Calling API with "+authToken+", "+fbId);
                Call<List<RaceResponse>> loginCall = client.getApiService().races(fbId, authToken);

                Response<List<RaceResponse>> response = loginCall.execute();
                if(response != null && response.code() == 200){
                    List<RaceResponse> raceResponseList = response.body();
                    Log.i(TAG, "#doInBackground User logged in: "+response.body().toString());
                    return raceResponseList;
                }else{
                    Log.e(TAG, "#doInBackground API returned HTTP Response code:"+ response.code()+", errors:"+response.errorBody());
                    return null;
                }
            }catch (Exception e){
                Log.e(TAG, "#doInBackground Exception while invoke groomify API service.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<RaceResponse> raceResponseList) {
            if(raceResponseList != null) {

                Realm realm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Races.class);//truncate the tables.
                        //TODO populate the race list
                        races = new Races[raceResponseList.size()];
                        int totalActive = 0;
                        for(int i = 0; i < raceResponseList.size(); i++){
                            RaceResponse raceDetail = raceResponseList.get(i);
                            if(raceDetail.getStatus()){
                                Races race = realm.createObject(Races.class, raceDetail.getId());
                                race.setDistance(""+raceDetail.getDistance());
                                race.setRaceLocation(raceDetail.getLocation());
                                race.setRaceName(raceDetail.getName());
                                race.setTotalMission(""+raceDetail.getMissionNo());
                                //Races race = new Races(i, raceDetail.getName(), ""+raceDetail.getLocation(), ""+raceDetail.getDistance(), ""+raceDetail.getMissionNo(), null, null);
                                races[totalActive] = race;
                                totalActive++;
                            }
                        }
                        races = java.util.Arrays.copyOf(races, totalActive);
                    }
                });

                viewPagerCarouselView.setData(getSupportFragmentManager(), races, carouselSlideInterval, SelectRaceActivity.this);
                joinRaceButton.setVisibility(View.VISIBLE);
                runAsGuestButton.setVisibility(View.VISIBLE);
            }else{
                //TODO retry again
                Toast.makeText(SelectRaceActivity.this, "Failed to load race list", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }


    private class GroomifyLoadRaceDetailTask extends AsyncTask<String, String, RaceDetailResponse> {
        @Override
        protected RaceDetailResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(SelectRaceActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(SelectRaceActivity.this);
            try {
                Response<RaceDetailResponse> raceDetailResponse = client.getApiService().raceDetail(fbId, authToken, params[0]).execute();

                if(raceDetailResponse.isSuccessful()){
                    Log.i(TAG, "Calling get race response success.");
                   return raceDetailResponse.body();
                }else{
                    Log.i(TAG, "Calling get race response failed.");
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to get race details.",e);
                Toast.makeText(SelectRaceActivity.this, "Unable to get race detail.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final RaceDetailResponse raceDetail) {
            super.onPostExecute(raceDetail);

            if(raceDetail != null){

                Realm realm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                try {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Races races = new Races(raceDetail.getId(), raceDetail.getName(),
                                    raceDetail.getLocation(),
                                    "" + raceDetail.getDistance(),
                                    "" + raceDetail.getMissionNo(),
                                    raceDetail.getStatus(),
                                    raceDetail.getEndTime(),
                                    raceDetail.getFirstAid(),
                                    raceDetail.getGrSupport());
                            races.missions = new RealmList<>();
                            realm.delete(Mission.class);

                            for (int i = 0; i < raceDetail.getMissions().size(); i++) {
                                com.groomify.hollavirun.rest.models.response.Mission missionResponse = raceDetail.getMissions().get(i);
                                //public Mission(boolean unlocked, String coverPhotoUrl, String missionDesc, int missionNumber, String missionNumberString, String missionTitle) {
                                Mission mission = new Mission(false, missionResponse.getUrl(), missionResponse.getDescription(), i + 1, decimalFormat.format(i + 1), missionResponse.getTitle());
                                Log.i(TAG, "Adding mission into database. " + mission.toString());

                                races.missions.add(i, mission);
                            }
                            Races realRaces = realm.copyToRealmOrUpdate(races);
                            joinRace(false);
                        }
                    });
                }finally {
                    realm.close();
                }
            }
        }
    }

    private class GroomifyJoinRaceTask extends AsyncTask<String, String, JoinRaceResponse> {

        @Override
        protected JoinRaceResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(SelectRaceActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(SelectRaceActivity.this);
            try {
                Response<JoinRaceResponse> joinRaceResponse = client.getApiService().joinRace(fbId, authToken, params[0]).execute();

                if(joinRaceResponse.isSuccessful()){
                    Log.i(TAG, "Calling join race api success");
                    return joinRaceResponse.body();
                }else{
                    Log.i(TAG, "Calling join race api failed, race id: "+params[0]+", response code: "+joinRaceResponse.code()+", error body: "+joinRaceResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to call join race api.",e);
                Toast.makeText(SelectRaceActivity.this, "Unable to get race detail.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JoinRaceResponse joinRace) {

            super.onPostExecute(joinRace);
            if(joinRace != null){
                Log.i(TAG, "Join race success, runner id:"+joinRace.getRunnerId());
                SharedPreferencesHelper.savePreferences(SelectRaceActivity.this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_RUNNER_ID, ""+joinRace.getRunnerId());

                Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                try {
                    innerRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            GroomifyUser realmUser = realm.where(GroomifyUser.class).findFirst();
                            Log.i(TAG, "After join race API call success. GroomifyUser: ("+realmUser+")");

                            realmUser.setCurrentBibNo(""+bibNo);
                            realmUser.setCurrentRunnerId("" + joinRace.getRunnerId());
                            realmUser.setCurrentRaceId(SelectRaceActivity.races[currentPosition].getId());

                            Log.i(TAG, "User saved in database: "+realmUser.getCurrentRaceId());

                            SharedPreferencesHelper.savePreferences(SelectRaceActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_RUN_SELECTED, true);
                            SharedPreferencesHelper.savePreferences(SelectRaceActivity.this, SharedPreferencesHelper.PreferenceValueType.LONG, AppConstant.PREFS_RUN_SELECTED_ID, races[currentPosition].getId());
                        }
                    });
                }finally {
                    innerRealm.close();
                }
            }
                launchWelcomeScreen();
            }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPagerCarouselView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Setting not saved, confirm to exit groomify?")
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

    @Override
    public void onPageScrolled(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
