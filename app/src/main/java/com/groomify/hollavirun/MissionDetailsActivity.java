package com.groomify.hollavirun;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.fragment.MissionDetailsFragment;
import com.groomify.hollavirun.fragment.MissionFragment;
import com.groomify.hollavirun.fragment.MissionListFragment;
import com.groomify.hollavirun.fragment.RankingListFragment;

import java.util.ArrayList;
import java.util.List;

public class MissionDetailsActivity extends AppCompatActivity {

    private final static String TAG = MissionDetailsActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    public static final int QR_REQUEST = 111;


    Mission mission = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        Bundle extras = getIntent().getExtras().getBundle("EXTRA_MISSION");
        if (extras != null) {
            mission = extras.getParcelable("MISSION");
            //The key argument here must match that used in the other activity
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

/*        if(getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_chevron_left);
            getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        Log.i(TAG, "Is view page there? --->"+viewPager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor((R.color.rustyRed)));
        tabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(getResources().getColor((R.color.primaryTextColour)), getResources().getColor((R.color.rustyRed)));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_REQUEST) {
            String result;
            if (resultCode == RESULT_OK) {
                result = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);

            } else {
                result = "Error";
            }
            Toast.makeText(this, result, Toast.LENGTH_SHORT);
            /*mResultTextView.setText(result);
            mResultTextView.setVisibility(View.VISIBLE);*/
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        MissionDetailsActivity.ViewPagerAdapter adapter = new MissionDetailsActivity.ViewPagerAdapter(getSupportFragmentManager());
        MissionDetailsFragment missionDetailFragment = MissionDetailsFragment.newInstance(mission);
        adapter.addFragment(missionDetailFragment, "DETAILS");
        adapter.addFragment(new RankingListFragment(), "RANKINGS");
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
    }
}
