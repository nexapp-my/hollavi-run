package com.groomify.hollavirun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.NewsFeed;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LatestUpdateDetailsActivity extends AppCompatActivity {

    private static final String TAG = LatestUpdateDetailsActivity.class.getSimpleName();
    private ImageView latestNewsCoverPhotoImageView;
    private TextView latestNewsHeaderTextView;
    private TextView latestNewsContentTextView;
    private Toolbar toolbar;
    private NewsFeed latestNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_update_details);

        latestNewsContentTextView = (TextView) findViewById(R.id.latest_news_content);
        latestNewsHeaderTextView = (TextView) findViewById(R.id.latest_news_header);
        latestNewsCoverPhotoImageView = (ImageView) findViewById(R.id.latest_news_cover_photo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Back press triggered.");
                onBackPressed();
            }
        });
        Bundle extras = getIntent().getExtras().getBundle("EXTRA_LATEST_NEWS");
        if (extras != null) {
            latestNews = extras.getParcelable("LATEST_NEWS");
        }
        latestNewsContentTextView.setText(latestNews.getContent());
        latestNewsHeaderTextView.setText(latestNews.getHeader());
        ImageLoader.getInstance().displayImage(latestNews.getCoverPhotoUrl(), latestNewsCoverPhotoImageView);

    }
}
