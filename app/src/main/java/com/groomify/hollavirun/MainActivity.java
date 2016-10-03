package com.groomify.hollavirun;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.groomify.hollavirun.fragment.ChatFragment;
import com.groomify.hollavirun.fragment.MainFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.view.ProfilePictureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valkyrie1988 on 9/17/2016.
 */
public class MainActivity extends AppCompatActivity   {

    private final static String TAG = MainActivity.class.getSimpleName();

   /* private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;*/

    private ImageView menuBarLogo;
    private TextView menuBarGreetingText;
    private ProfilePictureView pictureView;

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



        if (savedInstanceState == null) {
            Fragment newFragment = new MainFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_placeholder, newFragment).commit();

        }

        if(Profile.getCurrentProfile() != null){

            menuBarGreetingText.setText("Good Morning, " +Profile.getCurrentProfile().getName());

            pictureView.setProfileId(Profile.getCurrentProfile().getId());
            pictureView.setDrawingCacheEnabled(true);
            Log.i(TAG, "Action bar profile picture loaded");


        }

        /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_hollavi);
*/



       /* ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Profile Clicked!",
                        Toast.LENGTH_LONG).show();

                Log.i(TAG, "Logout bye bye.");

                LoginManager.getInstance().logOut();

                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        LoginManager.getInstance().logOut();

                    }
                }).executeAsync();

                launchLoginScreen();

            }
        });*/

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

      /*  viewPager = (ViewPager) findViewById(R.id.viewpager);

        Log.i(TAG, "Is view page there? --->"+viewPager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);*/
    }

   /* private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MissionFragment(), "Mission");
        adapter.addFragment(new MainFragment(), "Main");
        adapter.addFragment(new ChatFragment(), "Chat");
        //adapter.addFragment(new ChatFragment(), "Chat");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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


    private void launchLoginScreen(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
