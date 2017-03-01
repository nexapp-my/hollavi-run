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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Team;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.Runner;
import com.groomify.hollavirun.rest.models.request.UpdateRunnerInfoRequest;
import com.groomify.hollavirun.rest.models.response.RunnerInfoResponse;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.groomify.hollavirun.view.TeamViewPagerCarouselView;
import com.groomify.hollavirun.view.ViewPagerCarouselView;

import java.io.IOException;

import io.realm.Realm;
import retrofit2.Response;

public class TeamSelectActivity extends AppCompatActivity implements TeamViewPagerCarouselView.OnPageScrolledListener {

    private final static String TAG =  TeamSelectActivity.class.getSimpleName();

    private TeamViewPagerCarouselView teamViewPagerCarouselView;

    private static TextView letsGoTextView;

    private String selectedTeam = null;

    private RestClient client = new RestClient();

    private AlertDialog loadingDialog ;

    Team[] team;
    int currentPosition = 0;
    long carouselSlideInterval = 2000; // 3 SECONDS
    int totalScrollCount = 0;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);
        Realm.init(this);

        teamViewPagerCarouselView = (TeamViewPagerCarouselView) findViewById(R.id.team_carousel_view);

        team = AppUtils.getDefaultTeam();
        selectedTeam = team[0].getTeamName(); // just give first as his selected team.
        teamViewPagerCarouselView.setData(getSupportFragmentManager(), team, carouselSlideInterval, TeamSelectActivity.this);
        letsGoTextView = (TextView) findViewById(R.id.post_to_facebook_btn);
        letsGoTextView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
              selectTeam();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(TeamSelectActivity.this, "Your photo has been shared to facebook.", Toast.LENGTH_SHORT);
                new GroomifyUpdateRunnerInfoTask().execute();
            }

            @Override
            public void onCancel() {
                letsGoTextView.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Failed to share content to facebook. Errors: "+error.getMessage(),error.getCause() );
                Toast.makeText(TeamSelectActivity.this, "Failed to share facebook post. Please try again.", Toast.LENGTH_SHORT).show();
                letsGoTextView.setEnabled(true);
            }
        });

        loadingDialog = DialogUtils.buildLoadingDialog(this);

    }

    private void selectTeam(){
        Log.i(TAG, "Team selected: "+selectedTeam);

        selectedTeam = team[currentPosition].getTeamName();
        int resourceId = team[currentPosition].getResourceId();

        if (ShareDialog.canShow(SharePhotoContent.class)){

            letsGoTextView.setEnabled(false);
            SharePhoto.Builder photoBuilder = new SharePhoto.Builder();
            photoBuilder.setBitmap(BitmapUtils.decodeSampledBitmapFromResource(getResources(), resourceId, 800, 600));
            String hashtag =  TeamSelectActivity.this.getResources().getString(R.string.facebook_hashtag);

            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photoBuilder.build())
                    .setShareHashtag(new ShareHashtag.Builder().setHashtag(hashtag).build())
                    .build();

            shareDialog.show(content);

        }else{
            Toast.makeText(TeamSelectActivity.this, "Your device does not support facebook share.", Toast.LENGTH_SHORT).show();
        }

    }

    private void launchMainActivity(){
        Intent intent = new Intent(TeamSelectActivity.this, MainActivity.class);

        startActivity(intent);
        this.finish();
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

    @Override
    public void onPageScrolled(int currentPosition) {
        Log.i(TAG, "scrolled. position: "+currentPosition+" total scroll now: "+totalScrollCount);
        this.currentPosition = currentPosition;
        if(totalScrollCount < 13){
            totalScrollCount++;
        }else{
            teamViewPagerCarouselView.setCarouselSlideInterval(99999);
        }
    }


    private class GroomifyUpdateRunnerInfoTask extends AsyncTask<Void, String, RunnerInfoResponse> {

        @Override
        protected RunnerInfoResponse doInBackground(Void... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(TeamSelectActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(TeamSelectActivity.this);
            Long userId = SharedPreferencesHelper.getUserId(TeamSelectActivity.this);
            Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
            GroomifyUser realmUser = innerRealm.where(GroomifyUser.class).equalTo("id", userId).findFirst();

            changeViewState(true);
            try {

                Log.d(TAG, "User information: "+realmUser);
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
            changeViewState(false);
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
                letsGoTextView.setEnabled(false);
                launchMainActivity();
            } else {
                Toast.makeText(TeamSelectActivity.this, "Unable to make team selection at this moment. Please try again later.", Toast.LENGTH_SHORT).show();
                letsGoTextView.setEnabled(true);
            }


        }
    }

    private void changeViewState(final boolean loading){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loading){
                   // progressBar.setVisibility(View.VISIBLE);
                    //letsGoTextView.setVisibility(View.GONE);
                    loadingDialog.show();
                }else{
                    //progressBar.setVisibility(View.GONE);
                    //letsGoTextView.setVisibility(View.VISIBLE);
                    loadingDialog.hide();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
