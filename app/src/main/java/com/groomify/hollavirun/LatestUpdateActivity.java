package com.groomify.hollavirun;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.groomify.hollavirun.adapter.NewsFeedArrayAdapter;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.Info;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Response;

public class LatestUpdateActivity extends ListActivity {

    private static final String TAG = LatestUpdateActivity.class.getSimpleName();

    private RestClient client = new RestClient();
    private Realm realm;

    private NewsFeed[] newsFeeds;

    Toolbar toolbar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_update);

        progressBar = (ProgressBar) findViewById(R.id.latest_update_loading_circle);
        progressBar.setVisibility(View.GONE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        RealmResults<NewsFeed> newsFeedRealmResults = realm.where(NewsFeed.class).findAll();

        newsFeeds = newsFeedRealmResults.toArray(new NewsFeed[newsFeedRealmResults.size()]);

        NewsFeedArrayAdapter adapter = new NewsFeedArrayAdapter(this, newsFeeds);

        setListAdapter(adapter);

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        Log.i(TAG, "News feed clicked on "+position+", item: "+newsFeeds[position]);
        //TODO launch the news info page.

        Intent intent = new Intent(this, LatestUpdateDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("LATEST_NEWS", newsFeeds[position]);
        intent.putExtra("EXTRA_LATEST_NEWS", bundle);
        startActivity(intent);

    }

    private class GroomifyRaceInfoTask extends AsyncTask<String, String, RaceInfoResponse> {

        @Override
        protected RaceInfoResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(LatestUpdateActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(LatestUpdateActivity.this);
            try {
                Response<RaceInfoResponse> listResponse = client.getApiService().raceInfo(fbId, authToken, params[0]).execute();

                if(listResponse.isSuccessful()){
                    Log.i(TAG, "Calling race news api success");
                    return listResponse.body();
                }else{
                    Log.i(TAG, "Calling race news api failed, race id: "+params[0]+", response code: "+listResponse.code()+", error body: "+listResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to get race news.",e);
                Toast.makeText(LatestUpdateActivity.this, "Unable to get race detail.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(RaceInfoResponse raceInfoResponses) {
            super.onPostExecute(raceInfoResponses);

            //TODO save race info into database.
            if(raceInfoResponses != null){

                realm.beginTransaction();
                realm.delete(NewsFeed.class);//truncate the tables.
                List<Info> infos = raceInfoResponses.getInfos();
                Log.i(TAG, "Total news: "+infos.size());


                for(int i =0; i < infos.size(); i++){
                    NewsFeed newsFeed = realm.createObject(NewsFeed.class, i + 1);
                    newsFeed.setContent(infos.get(i).getContent());
                    newsFeed.setHeader(infos.get(i).getTitle());
                    newsFeed.setTimeStamp("1 min ago");//TODO missing timestamp
                    newsFeed.setCoverPhotoUrl(infos.get(i).getCover().getUrl());

                    realm.copyToRealmOrUpdate(newsFeed);
                }

                realm.commitTransaction();

            }
        }
    }
}
