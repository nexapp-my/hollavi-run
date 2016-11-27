package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.view.ViewPagerCarouselView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

public class SelectRaceActivity extends AppCompatActivity implements ViewPagerCarouselView.OnPageScrolledListener {
    ViewPagerCarouselView viewPagerCarouselView;

    TextView runAsGuestButton;
    View joinRaceButton;

    int currentPosition = 0;
    private static Races[] races;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_race);


        long carouselSlideInterval = 999999; // 3 SECONDS
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
                new Races(1, "GROOMIFY RUN 2016", "Putrajaya Sentral", "10", "5", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray()),
                new Races(2, "GROOMIFY RUN 2017", "KL Sentral", "15", "5", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray()),
                new Races(3, "GROOMIFY RUN 2018", "Penang Sentral", "20", "10", miniMapByteArr.toByteArray(), badgeByteArr.toByteArray()),
        };

        /*
        Bitmap b;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 50, bs);
        i.putExtra("byteArray", bs.toByteArray());
        */

        viewPagerCarouselView = (ViewPagerCarouselView) findViewById(R.id.carousel_view);
        viewPagerCarouselView.setData(getSupportFragmentManager(), races, carouselSlideInterval, this);


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

    }


    private void launchWelcomeScreen(){

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
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
