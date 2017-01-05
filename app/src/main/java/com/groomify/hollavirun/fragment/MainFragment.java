package com.groomify.hollavirun.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.NewsFeedArrayAdapter;
import com.groomify.hollavirun.entities.MissionCard;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.view.ProfilePictureView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class MainFragment  extends Fragment {

    public static ProfilePictureView profilePictureView;

    private final static String TAG = MainFragment.class.getSimpleName();

    /*private RecyclerView horizonRecyclerView;
    private ArrayList<MissionCard> missionCards;
    private HorizontalAdapter horizontalAdapter;*/

    public static View mainFragment;
    //public boolean isFirstTimeInitialized = true;

    public static EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* if(!isFirstTimeInitialized){
            return;
        }*/


        Log.i(TAG, "Inside the main fragment!!!!");

        /*NewsFeed[] newsFeeds = {
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
        missionCards.add(new MissionCard("02", "POKEMON GO", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.pikachu_resize, 128, 128)));
        missionCards.add(new MissionCard("03", "THE 3D JOURNEY", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.crysis_cover, 128, 128)));
        missionCards.add(new MissionCard("04", "INFLATABLE CASTLE", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mission_sample_cod, 128, 128)));
        missionCards.add(new MissionCard("05", "DON't TEXT & DRIVE", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mission_sample_ffantasy, 128, 128)));
        missionCards.add(new MissionCard("06", "UPSIDE DOWN WORLD", null));
*/
        //isFirstTimeInitialized = false;

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*if(mainFragment != null){
            return mainFragment;
        }*/
        // Inflate the layout for this fragment
        mainFragment = inflater.inflate(R.layout.fragment_main, container, false);


        if(editText == null){
            editText = (EditText) mainFragment.findViewById(R.id.search_runner_field);
        }
        /*editText.setOnFocusChangeListener(
                new EditText.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View v, boolean hasFocus) {
                          EditText editText = (EditText) v;
                          if(hasFocus){
                              editText.setText("");
                          }else{
                              editText.setText("Search Runner ID");
                          }
                      }
                  }
        );*/

        return mainFragment;

    }
}
