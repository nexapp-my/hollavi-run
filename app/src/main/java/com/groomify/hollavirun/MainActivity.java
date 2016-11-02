package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.groomify.hollavirun.fragment.MainFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.fragment.MissionListFragment;
import com.groomify.hollavirun.fragment.RankingListFragment;
import com.groomify.hollavirun.fragment.dummy.MissionContent;
import com.groomify.hollavirun.view.ProfilePictureView;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class MainActivity extends AppCompatActivity
implements
        MissionListFragment.OnListFragmentInteractionListener,
        RankingListFragment.OnFragmentInteractionListener{

    private final static String TAG = MainActivity.class.getSimpleName();



    private ImageView menuBarLogo;
    private TextView menuBarGreetingText;
    private ProfilePictureView pictureView;
    public Fragment currentFragment;
    public int currentMenuIndex = 0;


    public MainFragment mainFragment = new MainFragment();
    public MissionFragment missionFragment = new MissionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,  "In the Main activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        if(menuBarLogo == null)
            menuBarLogo = (ImageView) findViewById(R.id.menu_bar_gromify_logo);

        if(menuBarGreetingText == null)
            menuBarGreetingText = (TextView) findViewById(R.id.menu_bar_greeting_text);

        if(pictureView == null)
            pictureView = (ProfilePictureView) findViewById(R.id.menu_bar_profile_picture);


        menuBarLogo.setVisibility(ImageView.INVISIBLE);
        menuBarGreetingText.setVisibility(TextView.VISIBLE);
        pictureView.setVisibility(View.VISIBLE);


        //TODO load all the fragments here.



        if (savedInstanceState == null) {
            currentFragment = new MainFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_placeholder, currentFragment).commit();

        }

        if(Profile.getCurrentProfile() != null){

            menuBarGreetingText.setText("Good Morning, " +Profile.getCurrentProfile().getName());

            pictureView.setProfileId(Profile.getCurrentProfile().getId());
            pictureView.setDrawingCacheEnabled(true);
            Log.i(TAG, "Action bar profile picture loaded");

        }

        pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sosIntent = new Intent(v.getContext(), ProfileActivity.class);
                startActivity(sosIntent);
            }
        });

        initializeMenuBarListener();

    }

    int[] menusId = {
            R.id.menu_home,
            R.id.menu_mission,
            R.id.menu_camera,
            R.id.menu_news_feed,
            R.id.menu_sos
    };

   /* Fragment[] fragments = {
            new MainFragment(),
            new MissionFragment()
    };*/


    private void initializeMenuBarListener(){

        View homeMenu = findViewById(menusId[0]);

        homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 0 ){

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.remove(currentFragment).add(R.id.main_placeholder, mainFragment).commit();
                    currentFragment = mainFragment;
                    currentMenuIndex = 0;
                }
            }
        });

        View missionMenu = findViewById(menusId[1]);

        missionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMenuIndex != 1){

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.remove(currentFragment).add(R.id.main_placeholder,  missionFragment).commit();
                    currentFragment = missionFragment;
                    currentMenuIndex = 1;
                }
            }
        });


        View sosMenu = findViewById(menusId[4]);

        sosMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(currentMenuIndex != 4){
                    Intent sosIntent = new Intent(v.getContext(), SOSActivity.class);
                    startActivity(sosIntent);

                    currentMenuIndex = 4;
                //}
            }
        });

    }

    @Override
    public void onListFragmentInteraction(MissionContent.MissionItem item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Application")
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
}
