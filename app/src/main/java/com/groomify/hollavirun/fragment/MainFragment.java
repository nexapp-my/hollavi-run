package com.groomify.hollavirun.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.NewsFeedArrayAdapter;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.view.ProfilePictureView;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class MainFragment  extends ListFragment {

    public static ProfilePictureView profilePictureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainFragment = inflater.inflate(R.layout.fragment_main, container, false);

        View profileRankingView = inflater.inflate(R.layout.item_profile_ranking,container,false);

        TextView usernameTextView = (TextView) profileRankingView.findViewById(R.id.user_profile_name);
        usernameTextView.setText(Profile.getCurrentProfile().getName());

        profilePictureView = (ProfilePictureView) profileRankingView.findViewById(R.id.user_profile_picture);
        profilePictureView.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
        profilePictureView.setDrawingCacheEnabled(true);

        LinearLayout view = (LinearLayout) mainFragment.findViewById(R.id.profile_layout);
        view.addView(profileRankingView);


        return mainFragment;
    }
}
