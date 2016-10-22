package com.groomify.hollavirun.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.NewsFeedArrayAdapter;
import com.groomify.hollavirun.entities.MissionCard;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.view.ProfilePictureView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class MainFragment  extends ListFragment {

    public static ProfilePictureView profilePictureView;

    private final static String TAG = MainFragment.class.getSimpleName();

    private RecyclerView horizonRecyclerView;
    private ArrayList<MissionCard> missionCards;
    private HorizontalAdapter horizontalAdapter;

    public static View mainFragment;
    public boolean isFirstTimeInitialized = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isFirstTimeInitialized){
            return;
        }


        Log.i(TAG, "Inside the main fragment!!!!");

        NewsFeed[] newsFeeds = {
                new NewsFeed("Accident reported at section 13", "Get the latest news on the run right here.", "4 mins ago"),
                new NewsFeed("Well done guys!", "It was a beautiful run today. Thanks for your participation and till we meet again.", "4 mins ago"),
                new NewsFeed("King of the road!", "Congratulations. You have done this.", "1 day ago"),
                new NewsFeed("Neque porro quisquam est qui dolorem ipsum ", "Cras fermentum dolor et nisl tincidunt, in molestie nisl pellentesque. Duis dictum laoreet elit", "1 day ago"),
                new NewsFeed("quia dolor sit amet, consectetur", "Duis id tincidunt velit. Sed gravida diam nibh, et egestas sem porttitor et.", "1 day ago"),
                new NewsFeed("adipisci velit...", "Donec ut sapien enim. Fusce non tristique odio.", "2 days ago"),
                new NewsFeed("Nam eu pulvinar velit. Mauris ac nisl qua", "Morbi pellentesque nisi vel sollicitudin dapibus.", "1 day ago")
        };
        setListAdapter(new NewsFeedArrayAdapter(this.getContext(), newsFeeds));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        missionCards = new ArrayList<MissionCard>();
        missionCards.add(new MissionCard("01", "SELFIE WITH STRANGERS", null));
        //missionCards.add(new MissionCard("02", "POKEMON GO" ,null));
        missionCards.add(new MissionCard("02", "POKEMON GO", decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.pikachu_resize, 128, 128)));
//        missionCards.add(new MissionCard("03", "THE 3D JOURNEY", null));
//        missionCards.add(new MissionCard("04", "INFLATABLE CASTLE", null));
//        missionCards.add(new MissionCard("05", "DON't TEXT & DRIVE", null));

        missionCards.add(new MissionCard("03", "THE 3D JOURNEY", decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.crysis_cover, 128, 128)));
        missionCards.add(new MissionCard("04", "INFLATABLE CASTLE", decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mission_sample_cod, 128, 128)));
        missionCards.add(new MissionCard("05", "DON't TEXT & DRIVE", decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mission_sample_ffantasy, 128, 128)));
        missionCards.add(new MissionCard("06", "UPSIDE DOWN WORLD", null));

        isFirstTimeInitialized = false;
/*

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_resource);
*/
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mainFragment != null){
            return mainFragment;
        }
        // Inflate the layout for this fragment
        mainFragment = inflater.inflate(R.layout.fragment_main, container, false);
//
//        View profileRankingView = inflater.inflate(R.layout.item_profile_ranking,container,false);
//
//        TextView usernameTextView = (TextView) profileRankingView.findViewById(R.id.user_profile_name);
//        usernameTextView.setText(Profile.getCurrentProfile().getName());
//
//        profilePictureView = (ProfilePictureView) profileRankingView.findViewById(R.id.user_profile_picture);
//        profilePictureView.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
//        profilePictureView.setDrawingCacheEnabled(true);

        //LinearLayout view = (LinearLayout) mainFragment.findViewById(R.id.profile_layout);
        //view.addView(profileRankingView);

        horizonRecyclerView = (RecyclerView) mainFragment.findViewById(R.id.horizontal_mission_list);
        horizontalAdapter = new HorizontalAdapter(missionCards);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        Log.i(TAG, "LinearLayoutManager: -->"+horizontalLayoutManager);
        horizonRecyclerView.setLayoutManager(horizontalLayoutManager);

        horizonRecyclerView.setAdapter(horizontalAdapter);
        return mainFragment;

    }


    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<MissionCard> missionList;
        private DecimalFormat format = new DecimalFormat("00");

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView missionNumText;
            public TextView missionTitleText;
            public ImageView missionImage;
            public View clickableArea;


            public MyViewHolder(View view) {
                super(view);
                missionNumText = (TextView) view.findViewById(R.id.mission_num_text);
                missionTitleText = (TextView) view.findViewById(R.id.mission_title_text);
                missionImage = (ImageView) view.findViewById(R.id.mission_background_image);
                clickableArea = view.findViewById(R.id.mission_card);

            }
        }

        public HorizontalAdapter(List<MissionCard> missionList) {
            this.missionList = missionList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mission_card, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            MissionCard missionCard = missionList.get(position);
            holder.missionNumText.setText(format.format(position + 1));
            holder.missionTitleText.setText(missionCard.getMissionTitle());

            if(missionCard.getMissionBackgroundImage() != null) {
                holder.missionImage.setImageBitmap(missionCard.getMissionBackgroundImage());
            }

            holder.clickableArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getContext(),holder.missionTitleText.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return missionList.size();
        }
    }
}
