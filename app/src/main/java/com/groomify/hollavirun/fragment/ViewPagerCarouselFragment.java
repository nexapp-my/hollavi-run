package com.groomify.hollavirun.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Races;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Valkyrie1988 on 11/27/2016.
 */

public class ViewPagerCarouselFragment extends Fragment {
    //public static final String IMAGE_RESOURCE_ID = "image_resource_id";

    public static final String RACE_OBJECT  = "race_object";

    //private ImageView ivCarouselImage;
    //private int imageResourceId;
    private TextView raceTitleTextView;
    private TextView raceLocationTextView;
    private TextView raceDistanceTextView;
    private TextView raceTotalMissionTextView;
    private TextView raceDateTextView;

    private ImageView miniMapImageView;
    private ImageView finisherBadgeImageView;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
    private static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_race_details, container, false);

        final Races race = getArguments().getParcelable(RACE_OBJECT);

        raceTitleTextView = (TextView) v.findViewById(R.id.race_title_text_view);

        raceLocationTextView = (TextView) v.findViewById(R.id.race_location_text_view);

        raceDistanceTextView = (TextView) v.findViewById(R.id.distance_text_view);

        raceTotalMissionTextView = (TextView) v.findViewById(R.id.total_mission_text_view);

        miniMapImageView = (ImageView) v.findViewById(R.id.mini_map_image_view);

        //finisherBadgeImageView = (ImageView) v.findViewById(R.id.finisher_badge_image_view);

        raceDateTextView = (TextView) v.findViewById(R.id.date_text_view);

        raceTitleTextView.setText(race.getRaceName());
        raceLocationTextView.setText(race.getRaceLocation());
        raceTotalMissionTextView.setText(race.getTotalMission());
        raceDistanceTextView.setText(race.getDistance() + " KM");
        Date jsonDate = null;

        if(race.getEndTime() != null){
            try {
                Log.i("DEBUG", "The damn race end time:"+race.getEndTime());
                jsonDate = jsonDateFormat.parse(race.getEndTime());
            } catch (ParseException e) {
                e.printStackTrace();//Ignore
            }
            if(jsonDate != null){
                raceDateTextView.setText(sdf.format(jsonDate));
            }else{
                raceDateTextView.setText("-");
            }

        }

        miniMapImageView = new ImageView(this.getContext());

        if(race.getMiniMapByteArr() != null){
            Bitmap miniMapBitmap = BitmapFactory.decodeByteArray(race.getMiniMapByteArr(), 0, race.getMiniMapByteArr().length);
            miniMapImageView.setImageBitmap(miniMapBitmap);
        }

        //finisherBadgeImageView = new ImageView(this.getContext());

        /*if(race.getMiniMapByteArr() != null){
            Bitmap finisherBadgeBitmap = BitmapFactory.decodeByteArray(race.getCompletetionBadgeByteArr(), 0, race.getCompletetionBadgeByteArr().length);
            finisherBadgeImageView.setImageBitmap(finisherBadgeBitmap);
        }*/


        /*ivCarouselImage = (ImageView) v.findViewById(R.id.iv_carousel_image);
        imageResourceId = getArguments().getInt(IMAGE_RESOURCE_ID, R.drawable.ic_default_avatar); // default to car1 image resource
        ivCarouselImage.setImageResource(imageResourceId);*/

        /*v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "RaceResponse: "+race.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return v;
    }
}