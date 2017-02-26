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

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.entities.Team;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Valkyrie1988 on 11/27/2016.
 */

public class TeamViewPagerCarouselFragment extends Fragment {
    public static final String TEAM_OBJECT  = "race_object";

    private ImageView teamEmbasaddorImageView;
    private TextView teamNameTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_details, container, false);

        final Team team = getArguments().getParcelable(TEAM_OBJECT);
        teamEmbasaddorImageView = (ImageView) v.findViewById(R.id.team_embasaddor_image_view);
        teamNameTextView = (TextView) v.findViewById(R.id.team_name_text_view);

        int dimension = AppUtils.getPixelFromDIP(this.getContext(), 200);
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(this.getResources(), team.getResourceId(), dimension, dimension);

        teamEmbasaddorImageView.setImageBitmap(bitmap);
        teamNameTextView.setText(team.getTeamName());

        return v;
    }
}