package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.RaceResponse;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.groomify.hollavirun.view.ViewPagerCarouselView;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

    private volatile String bibNo = null;

    private final static int MAX_BIB_NO = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_race);

        Bitmap miniMap = BitmapUtils.cropBitmap(183, 183, BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_mini));
        Bitmap badge = BitmapUtils.cropBitmap(183, 183, BitmapFactory.decodeResource(getResources(), R.drawable.ic_finisher_badge));

        ByteArrayOutputStream miniMapByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream badgeByteArr = new ByteArrayOutputStream();

        miniMap.compress(Bitmap.CompressFormat.PNG, 50, miniMapByteArr);
        badge.compress(Bitmap.CompressFormat.PNG, 50, badgeByteArr);

        races = new Races[]{
                /*new Races(1, "GROOMIFY RUN 2016", "Putrajaya Sentral", "10", "5", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray()),
                new Races(2, "GROOMIFY RUN 2017", "KL Sentral", "15", "5", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray()),
                new Races(3, "GROOMIFY RUN 2018", "Penang Sentral", "20", "10", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray()),*/
        };

        /*
        Bitmap b;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 50, bs);
        i.putExtra("byteArray", bs.toByteArray());
        */

        viewPagerCarouselView = (ViewPagerCarouselView) findViewById(R.id.carousel_view);



        joinRaceButton = findViewById(R.id.join_race_button);
        runAsGuestButton = (TextView) findViewById(R.id.run_as_guest_button);
        progressBar = (ProgressBar) findViewById(R.id.race_list_loading_circle);


        joinRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptBibNoInputDialog();
            }
        });

        runAsGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAsGuest();
            }
        });

        new GroomifyLoadListTask().execute();

    }

    private  void runAsGuest(){
        SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_RUN_SELECTED, true);
        SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.INTEGER, AppConstant.PREFS_RUN_SELECTED_ID, -1);
        launchWelcomeScreen();
    }

    private void joinRace(){
        Races race = SelectRaceActivity.races[currentPosition];
        Toast.makeText(SelectRaceActivity.this, "Race "+race.getRaceName()+" has been selected. Bib no:"+bibNo, Toast.LENGTH_LONG).show();
        SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_RUN_SELECTED, true);
        SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.INTEGER, AppConstant.PREFS_RUN_SELECTED_ID, race.getId());
        launchWelcomeScreen();
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
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(input.getText().length() > MAX_BIB_NO){
                    Log.i(TAG, "Bib no input length "+input.getText().length()+" reach maximum "+MAX_BIB_NO+", return false.");
                    return true;
                }else
                    return false;

            }
        });
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(input);
        // set dialog message
        builder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(input.getText().length() > 0 && input.getText().toString().trim().length() > 0){
                                    bibNo = input.getText().toString();
                                    joinRace();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();

    }
    private class GroomifyLoadListTask extends AsyncTask<Void, String, List<RaceResponse>> {

        @Override
        protected List<RaceResponse> doInBackground(Void... params) {


            Log.i(TAG, "#doInBackground Load to groomify race list.");

            try{
                Call<List<RaceResponse>> loginCall = client.getApiService().races("688902499", "gLfi9cm4qFkzij4xi5he");
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
        protected void onPostExecute(List<RaceResponse> raceResponseList) {
            if(raceResponseList != null){
                //TODO populate the race list
                races = new Races[raceResponseList.size()];

                for(int i = 0; i < raceResponseList.size(); i++){
                    RaceResponse raceDetail = raceResponseList.get(i);
                    //new Races(2, "GROOMIFY RUN 2017", "KL Sentral", "15", "5", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray())
                    Races race = new Races(i, raceDetail.getName(), ""+raceDetail.getLocation(), ""+raceDetail.getDistance(), ""+raceDetail.getMissionNo(), null, null);
                    races[i] = race;
                }

                viewPagerCarouselView.setData(getSupportFragmentManager(), races, carouselSlideInterval, SelectRaceActivity.this);
            }else{
                //TODO retry again
                Toast.makeText(SelectRaceActivity.this, "Failed to load race list", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
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
