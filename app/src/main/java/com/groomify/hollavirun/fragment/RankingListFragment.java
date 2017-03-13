package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.RankingArrayAdapter;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Ranking;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.RaceRankingResponse;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Response;

public class RankingListFragment extends ListFragment {


    private final static String TAG = RankingListFragment.class.getSimpleName();

    private TextView myRankingNoTextView = null;
    private TextView myRankingNameTextView = null;
    private TextView myRankingUserIdTextView = null;
    private TextView myRankingTimeTextView = null;
    private TextView listEmptryTextView;
    private View myRankingPanel = null;
    private ProgressBar loadingProgress;

    private Realm realm;

    private RealmChangeListener<RealmResults<Ranking>> realmChangeListener;

    private RankingArrayAdapter rankingArrayAdapter;

    private RestClient client = new RestClient();


    List<Ranking> rankings = new ArrayList<>();

    Long raceId;
    Long userId;

    public RankingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "on create ranking list fragment.");

        Realm.init(this.getContext());
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());

        /*RealmResults<Ranking> rankingRealmResults = realm.where(Ranking.class).findAll();

        Log.i(TAG, "All ranking. "+rankingRealmResults.size());
        rankings = rankingRealmResults.toArray(new Ranking[0]);*/
        rankingArrayAdapter = new RankingArrayAdapter(this.getContext(), rankings);
        setListAdapter(rankingArrayAdapter);

        raceId = SharedPreferencesHelper.getSelectedRaceId(getContext());

        userId = SharedPreferencesHelper.getUserId(getContext());

/*
        // set up a Realm change listener
        realmChangeListener = new RealmChangeListener<RealmResults<Ranking>>() {
            @Override
            public void onChange(RealmResults<Ranking> results) {
                // This is called anytime the Realm database changes on any thread.
                // Please note, change listeners only work on Looper threads.
                // For non-looper threads, you manually have to use Realm.waitForChange() instead.
                Log.i(TAG, "Realm changed detected, notify dataset changed.");
                rankingArrayAdapter.notifyDataSetChanged(); // Update the UI
            }
        };

        rankingRealmResults.addChangeListener(realmChangeListener);
*/


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);
        loadingProgress = (ProgressBar) view.findViewById(R.id.ranking_loading_progress);

        View parentView = view.findViewById(R.id.my_profile_ranking);
        myRankingNoTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_no);
        myRankingNameTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_name);
        myRankingUserIdTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_user_id);
        myRankingTimeTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_time);
        listEmptryTextView = (TextView) view.findViewById(R.id.list_empty_text_view);
        listEmptryTextView.setVisibility(View.INVISIBLE);
        myRankingPanel = parentView;
        initializeMyRanking();

        if(rankings.size() == 0){
            loadingProgress.setVisibility(View.VISIBLE);
        }

        new GroomifyMissionRankingTask().execute(""+raceId);

        return view;
    }

    private void initializeMyRanking()
    {
        GroomifyUser groomifyUser = realm.where(GroomifyUser.class).findFirst();

        Ranking ranking = groomifyUser.getMyRanking() ;

        if(ranking != null){
            myRankingNoTextView.setText("#"+ranking.getRankNumber()+".");
            myRankingNameTextView.setText(ranking.getName());
            myRankingTimeTextView.setText(ranking.getCompletionTime() == null ? "" : AppUtils.getFormattedTimeFromSeconds(ranking.getCompletionTime()));
            myRankingUserIdTextView.setText("(ID:"+ranking.getId()+")");
            myRankingPanel.setVisibility(View.VISIBLE);
        }else{
            myRankingPanel.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //TODO might need a thread periodicaly pull the ranking.
    private class GroomifyMissionRankingTask extends AsyncTask<String, String, RaceRankingResponse> {

        @Override
        protected RaceRankingResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(getContext());
            String fbId = SharedPreferencesHelper.getFbId(getContext());
            try {
                Response<RaceRankingResponse> restResponse = client.getApiService().raceRanking(fbId, authToken, params[0]).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling race ranking api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling race ranking api failed, race id: "+params[0]+", response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to get race ranking.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final RaceRankingResponse raceRankingResponse) {
            super.onPostExecute(raceRankingResponse);

            if(raceRankingResponse != null){

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(com.groomify.hollavirun.entities.Ranking.class);//truncate the tables.

                        Log.i(TAG, "Total ranking size: "+raceRankingResponse.getRankings().size());

                        for(int i =0; i < raceRankingResponse.getRankings().size(); i++){
                            com.groomify.hollavirun.entities.Ranking ranking = realm.createObject(com.groomify.hollavirun.entities.Ranking.class, i + 1);
                            ranking.setName(raceRankingResponse.getRankings().get(i).getRunnerName());

                            ranking.setCompletionTime(raceRankingResponse.getRankings().get(i).getTotalMissionTime());
                            ranking.setId(raceRankingResponse.getRankings().get(i).getRunnerBib());
                            ranking.setTeamName(raceRankingResponse.getRankings().get(i).getTeam());

                            Log.i(TAG, "Saving ranking into database: "+ranking.toString());
                            realm.copyToRealmOrUpdate(ranking);
                        }

                        Log.i(TAG, "Ranking list saved into database. Saving user own ranking into database.");
                        final com.groomify.hollavirun.entities.Ranking myRanking = new Ranking();
                        myRanking.setRankNumber(raceRankingResponse.getMyRanking().getRanking());
                        myRanking.setName(raceRankingResponse.getMyRanking().getRunnerName());
                        myRanking.setCompletionTime(raceRankingResponse.getMyRanking().getTotalMissionTime());
                        myRanking.setId(raceRankingResponse.getMyRanking().getRunnerBib());
                        myRanking.setTeamName(raceRankingResponse.getMyRanking().getTeam());

                        final Ranking realmRanking = realm.copyToRealmOrUpdate(myRanking);
                        Log.i(TAG, "Saving user ranking into database: "+myRanking.toString());

                        GroomifyUser groomifyUser = realm.where(GroomifyUser.class).equalTo("id", userId).findFirst();
                        groomifyUser.setMyRanking(realmRanking);
                        Log.i(TAG, "User info from database: "+groomifyUser.toString());
                    }
                });

            }else{
                //Toast.makeText(getContext(), "Unable to get race ranking at this moment.", Toast.LENGTH_SHORT).show();
            }

            reloadRankingList();
        }
    }

    private void reloadRankingList(){
        loadingProgress.setVisibility(View.GONE);
        RealmResults<Ranking> rankingRealmResults = realm.where(Ranking.class).findAll();

        rankings.clear();
        rankingArrayAdapter.clear();
        rankingArrayAdapter.addAll(rankingRealmResults);
        rankingArrayAdapter.notifyDataSetChanged();
        initializeMyRanking();

        if(rankings.size() == 0){
            listEmptryTextView.setVisibility(View.VISIBLE);
        }

    }
}
