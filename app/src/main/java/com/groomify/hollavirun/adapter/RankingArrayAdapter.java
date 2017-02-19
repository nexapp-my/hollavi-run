package com.groomify.hollavirun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Ranking;
import com.groomify.hollavirun.utils.AppUtils;

/**
 * Created by Valkyrie1988 on 10/23/2016.
 */

public class RankingArrayAdapter extends ArrayAdapter<Ranking> {
    private final Context context;
    private final Ranking[] values;

    public RankingArrayAdapter(Context context, Ranking[] values) {
        super(context, R.layout.item_mission, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_ranking, parent, false);

        TextView itemRankingNoTextView = (TextView) rowView.findViewById(R.id.item_ranking_no);
        TextView itemRankingNameTextView = (TextView) rowView.findViewById(R.id.item_ranking_name);
        TextView itemRankingUserIdTextView = (TextView) rowView.findViewById(R.id.item_ranking_user_id);
        TextView itemRankingTimeTextView = (TextView) rowView.findViewById(R.id.item_ranking_time);

        if(values != null && values[position] != null){
            Ranking ranking = values[position];
            itemRankingNoTextView.setText(ranking.getRankNumber()+".");
            itemRankingNameTextView.setText(ranking.getName());
            itemRankingTimeTextView.setText(ranking.getCompletionTime() == null ? "" : AppUtils.getFormattedTimeFromSeconds(ranking.getCompletionTime()));
            itemRankingUserIdTextView.setText("(ID:"+ranking.getId()+")");
        }

        return rowView;
    }


}
