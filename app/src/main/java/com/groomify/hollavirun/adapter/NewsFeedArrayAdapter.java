package com.groomify.hollavirun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.NewsFeed;

import org.w3c.dom.Text;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class NewsFeedArrayAdapter extends ArrayAdapter<NewsFeed> {

    private final Context context;
    private final NewsFeed[] values;

    public NewsFeedArrayAdapter(Context context, NewsFeed[] values) {

        super(context, R.layout.item_news_feed, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_news_feed, parent, false);

        TextView feedHeaderTextView = (TextView) rowView.findViewById(R.id.feeds_header);
        TextView feedContentTextView = (TextView) rowView.findViewById(R.id.feeds_content);
        TextView feedTimestampTextView = (TextView) rowView.findViewById(R.id.feeds_timestamp);

        if(values != null && values[position] != null){
            NewsFeed newsFeed = values[position];
            feedContentTextView.setText(newsFeed.getContent());
            feedHeaderTextView.setText(newsFeed.getHeader());
            feedTimestampTextView.setText(newsFeed.getTimeStamp());
        }


        return rowView;
    }
}
