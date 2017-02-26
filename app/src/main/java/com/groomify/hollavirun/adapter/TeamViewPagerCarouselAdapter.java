package com.groomify.hollavirun.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.entities.Team;
import com.groomify.hollavirun.fragment.TeamViewPagerCarouselFragment;
import com.groomify.hollavirun.fragment.ViewPagerCarouselFragment;

/**
 * Created by Valkyrie1988 on 11/27/2016.
 */

public class TeamViewPagerCarouselAdapter extends FragmentStatePagerAdapter {
    private Team[] team;

    public TeamViewPagerCarouselAdapter(FragmentManager fm, Team[] team) {
        super(fm);
        this.team = team;
    }


    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TeamViewPagerCarouselFragment.TEAM_OBJECT, team[position]);
        TeamViewPagerCarouselFragment frag = new TeamViewPagerCarouselFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return (team == null) ? 0: team.length;
    }

}