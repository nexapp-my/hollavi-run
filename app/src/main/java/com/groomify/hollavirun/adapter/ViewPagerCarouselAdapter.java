package com.groomify.hollavirun.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.fragment.ViewPagerCarouselFragment;

/**
 * Created by Valkyrie1988 on 11/27/2016.
 */

public class ViewPagerCarouselAdapter extends FragmentStatePagerAdapter {
    //private int[] imageResourceIds;

    private Races[] races;

    public ViewPagerCarouselAdapter(FragmentManager fm, Races[] races) {
        super(fm);
        this.races = races;
    }


    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ViewPagerCarouselFragment.RACE_OBJECT, races[position]);
        ViewPagerCarouselFragment frag = new ViewPagerCarouselFragment();
        frag.setArguments(bundle);

        return frag;
    }

    @Override
    public int getCount() {
        return (races == null) ? 0: races.length;
    }

}