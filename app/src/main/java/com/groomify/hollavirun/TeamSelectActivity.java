package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.Runner;
import com.groomify.hollavirun.rest.models.request.UpdateRunnerInfoRequest;
import com.groomify.hollavirun.rest.models.response.RunnerInfoResponse;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.io.IOException;

import io.realm.Realm;
import retrofit2.Response;

public class TeamSelectActivity extends AppCompatActivity {

    private final static String TAG =  TeamSelectActivity.class.getSimpleName();

    private static TextView teamSelectionOneTextView;
    private static TextView teamSelectionTwoTextView;
    private static TextView teamSelectionThreeTextView;

    private static TextView letsGoTextView;

    private String selectedTeam = "A";

    ProgressBar progressBar;

    RestClient client = new RestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);


        progressBar = (ProgressBar) findViewById(R.id.team_update_loading_circle);
        progressBar.setVisibility(View.GONE);

        teamSelectionOneTextView = (TextView) findViewById(R.id.text_view_team_one);
        teamSelectionTwoTextView = (TextView) findViewById(R.id.text_view_team_two);
        teamSelectionThreeTextView = (TextView) findViewById(R.id.text_view_team_three);

        teamSelectionOneTextView.setTextColor(ContextCompat.getColor(this,R.color.rustyRed));
        teamSelectionTwoTextView.setTextColor(ContextCompat.getColor(this,R.color.primaryTextColour));
        teamSelectionThreeTextView.setTextColor(ContextCompat.getColor(this,R.color.primaryTextColour));

        letsGoTextView = (TextView) findViewById(R.id.text_view_lets_go);



        teamSelectionOneTextView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTeamSelection(1);
            }
        });

        teamSelectionTwoTextView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTeamSelection(2);
            }
        });

        teamSelectionThreeTextView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTeamSelection(3);
            }
        });

        letsGoTextView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
              selectTeam();
            }
        });

    }

    private void selectTeam(){
        Log.i(TAG, "Team selected: "+selectedTeam);
        new GroomifyUpdateRunnerInfoTask().execute();
    }

    private void launchMainActivity(){
        Intent intent = new Intent(TeamSelectActivity.this, MainActivity.class);

        startActivity(intent);
        this.finish();
    }

    private void toggleTeamSelection(int currentTeam){
        teamSelectionOneTextView.setTextColor(ContextCompat.getColor(this,R.color.primaryTextColour));
        teamSelectionTwoTextView.setTextColor(ContextCompat.getColor(this,R.color.primaryTextColour));
        teamSelectionThreeTextView.setTextColor(ContextCompat.getColor(this,R.color.primaryTextColour));

        switch (currentTeam){
            case 1:
                teamSelectionOneTextView.setTextColor(ContextCompat.getColor(this,R.color.rustyRed));
                selectedTeam = "A";
                break;
            case 2:
                teamSelectionTwoTextView.setTextColor(ContextCompat.getColor(this,R.color.rustyRed));
                selectedTeam = "B";
                break;
            case 3:
                teamSelectionThreeTextView.setTextColor(ContextCompat.getColor(this,R.color.rustyRed));
                selectedTeam = "C";
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        Log.i(TAG, "onBackPressed. Do nothing.");
        new AlertDialog.Builder(this)
                .setMessage("Setting not saved, confirm to exit groomify?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    private class GroomifyUpdateRunnerInfoTask extends AsyncTask<Void, String, RunnerInfoResponse> {

        @Override
        protected RunnerInfoResponse doInBackground(Void... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(TeamSelectActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(TeamSelectActivity.this);
            Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
            GroomifyUser realmUser = innerRealm.where(GroomifyUser.class).findFirst();

            changeViewState(true);
            try {

                UpdateRunnerInfoRequest updateRunnerInfoRequest = new UpdateRunnerInfoRequest();
                Runner runner = new Runner();
                runner.setRunBibNo(realmUser.getCurrentBibNo());
                runner.setTeam(selectedTeam);
                updateRunnerInfoRequest.setRunner(runner);
                Response<RunnerInfoResponse> restResponse = client.getApiService().updateRunnerInfo(fbId, authToken, realmUser.getCurrentRunnerId(), updateRunnerInfoRequest).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling update runner api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling update runner api failed. response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to call update runner api.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(RunnerInfoResponse runnerInfoResponse) {

            super.onPostExecute(runnerInfoResponse);
            if (runnerInfoResponse != null) {
                Log.i(TAG, "Update user info success, returning:" + runnerInfoResponse.toString());
                Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                innerRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        GroomifyUser realmUser = realm.where(GroomifyUser.class).findFirst();
                        realmUser.setCurrentTeam(selectedTeam);
                    }
                });
                SharedPreferencesHelper.savePreferences(TeamSelectActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_TEAM_SELECTED, true);
                SharedPreferencesHelper.savePreferences(TeamSelectActivity.this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_TEAM_SELECTED_ID, selectedTeam);
                SharedPreferencesHelper.savePreferences(TeamSelectActivity.this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_FIRST_TIME_SETUP_COMPLETE, true);
                launchMainActivity();

            } else {
                Toast.makeText(TeamSelectActivity.this, "Unable to make team selection at this moment. Please try again later.", Toast.LENGTH_SHORT).show();
                changeViewState(false);
            }


        }
    }

    private void changeViewState(final boolean loading){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loading){
                    progressBar.setVisibility(View.VISIBLE);
                    letsGoTextView.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    letsGoTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
