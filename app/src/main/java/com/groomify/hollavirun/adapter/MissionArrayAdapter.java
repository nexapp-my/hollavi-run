package com.groomify.hollavirun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.NewsFeed;

import java.util.List;

/**
 * Created by Valkyrie1988 on 10/23/2016.
 */

public class MissionArrayAdapter extends ArrayAdapter<Mission> {
    private final Context context;
    private final Mission[] values;

    public MissionArrayAdapter(Context context, Mission[] values) {
        super(context, R.layout.item_mission, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_mission, parent, false);

        TextView missionNumberTextView = (TextView) rowView.findViewById(R.id.mission_item_number);
        TextView missionTitleTextView = (TextView) rowView.findViewById(R.id.mission_item_title);
        TextView missionDescTextView = (TextView) rowView.findViewById(R.id.mission_item_desc);

        if(values != null && values[position] != null){
            Mission mission = values[position];
            missionNumberTextView.setText(mission.getMissionNumber());
            missionTitleTextView.setText(mission.getMissionTitle());
            missionDescTextView.setText(mission.getMissionDesc());
        }


        return rowView;
    }
}
