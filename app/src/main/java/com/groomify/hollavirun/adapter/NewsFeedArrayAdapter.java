package com.groomify.hollavirun.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class NewsFeedArrayAdapter extends ArrayAdapter<NewsFeed> {

    private final Context context;
    private NewsFeed[] values;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");

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
        ImageView feedImageView = (ImageView) rowView.findViewById(R.id.feeds_image);

        if(values != null && values[position] != null){
            NewsFeed newsFeed = values[position];
            feedContentTextView.setText(newsFeed.getDescription());
            feedHeaderTextView.setText(newsFeed.getHeader());
            if(newsFeed.getTimeStamp() != null){
                Long secDifferent = Calendar.getInstance().getTime().getTime() - newsFeed.getTimeStamp().getTime();
                Log.i("DEBUG", "Seconds diff >"+secDifferent);
                if(secDifferent >= 864000000){
                    //One day before we just display date.
                    feedTimestampTextView.setText(sdf.format(newsFeed.getTimeStamp()));
                }else{
                    String formattedDate =  AppUtils.getFormattedHoursAndMinsFromSeconds(secDifferent.intValue() / 1000);
                    feedTimestampTextView.setText(formattedDate +" ago.");
                }
            }else{
                feedTimestampTextView.setVisibility(View.GONE);
            }

            ImageLoader.getInstance().displayImage(newsFeed.getCoverPhotoUrl(), feedImageView, ImageLoadUtils.getDisplayImageOptions());
        }


        return rowView;
    }

    public void setValues(NewsFeed[] values) {
        this.values = values;
    }
}
