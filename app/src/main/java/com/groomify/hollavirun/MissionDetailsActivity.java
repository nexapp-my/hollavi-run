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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    public static final int QR_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);

       /* if(getSupportActionBar() != null)
            getSupportActionBar().hide();*/

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
        adapter.addFragment(new MissionDetailsFragment(), "DETAILS");
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
