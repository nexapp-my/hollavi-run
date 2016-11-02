package com.groomify.hollavirun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.groomify.hollavirun.view.ProfilePictureView;

public class ProfileActivity extends AppCompatActivity {
    private ProfilePictureView pictureView;
    private TextView userDisplayNameTextView;
    private ImageView closeProfileButton;

    private final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(userDisplayNameTextView == null){
            userDisplayNameTextView = (TextView) findViewById(R.id.user_display_name);
        }

        if(pictureView == null)
            pictureView = (ProfilePictureView) findViewById(R.id.profile_fb_profile_picture);

        if(Profile.getCurrentProfile() != null){
            String name = Profile.getCurrentProfile().getName();
            userDisplayNameTextView.setText(name);
            pictureView.setProfileId(Profile.getCurrentProfile().getId());
            pictureView.setDrawingCacheEnabled(true);
        }

        if(closeProfileButton == null){
            closeProfileButton = (ImageView) findViewById(R.id.close_profile_button);
        }
        closeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
