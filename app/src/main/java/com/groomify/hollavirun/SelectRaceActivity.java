package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.Race;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.view.ViewPagerCarouselView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SelectRaceActivity extends AppCompatActivity implements ViewPagerCarouselView.OnPageScrolledListener {
    ViewPagerCarouselView viewPagerCarouselView;

    TextView runAsGuestButton;
    View joinRaceButton;

    int currentPosition = 0;
    private static Races[] races;

    RestClient client = new RestClient();
    long carouselSlideInterval = 999999; // 3 SECONDS
    private static final String TAG = SelectRaceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_race);



        /*int [] imageResourceIds = {
                R.drawable.mission_banner_01,
                R.drawable.mission_banner_02,
                R.drawable.mission_banner_03,
                R.drawable.mission_banner_04,
                R.drawable.mission_banner_05,
                R.drawable.mission_banner_06};*/

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


        joinRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Races race = SelectRaceActivity.races[currentPosition];
                Toast.makeText(SelectRaceActivity.this, "Race "+race.getRaceName()+" has been selected.", Toast.LENGTH_SHORT).show();
                launchWelcomeScreen();
            }
        });

        runAsGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWelcomeScreen();
            }
        });

        new GroomifyLoadListTask().execute();

    }


    private void launchWelcomeScreen(){

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private class GroomifyLoadListTask extends AsyncTask<Void, String, List<Race>> {

        @Override
        protected List<Race> doInBackground(Void... params) {

            Log.i(TAG, "#doInBackground Load to groomify race list.");

            try{
                Call<List<Race>> loginCall = client.getApiService().races("688902499", "gLfi9cm4qFkzij4xi5he");
                Response<List<Race>> response = loginCall.execute();
                if(response != null && response.code() == 200){
                    List<Race> raceList = response.body();
                    Log.i(TAG, "#doInBackground User logged in: "+response.body().toString());
                    return raceList;
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
        protected void onPostExecute(List<Race> raceList) {
            if(raceList != null){
                //TODO populate the race list
                races = new Races[raceList.size()];

                for(int i = 0; i < raceList.size(); i++){
                    Race raceDetail = raceList.get(i);
                    //new Races(2, "GROOMIFY RUN 2017", "KL Sentral", "15", "5", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray())
                    Races race = new Races(i, raceDetail.getName(), ""+raceDetail.getLocation(), ""+raceDetail.getDistance(), ""+raceDetail.getMissionNo(), null, null);
                    races[i] = race;
                }

                viewPagerCarouselView.setData(getSupportFragmentManager(), races, carouselSlideInterval, SelectRaceActivity.this);
            }else{
                //TODO retry again
                Toast.makeText(SelectRaceActivity.this, "Failed to load race list", Toast.LENGTH_SHORT).show();
            }
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
