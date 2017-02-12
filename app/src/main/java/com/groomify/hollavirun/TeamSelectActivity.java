package com.groomify.hollavirun;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

public class TeamSelectActivity extends AppCompatActivity {

    private final static String TAG =  TeamSelectActivity.class.getSimpleName();

    private static TextView teamSelectionOneTextView;
    private static TextView teamSelectionTwoTextView;
    private static TextView teamSelectionThreeTextView;

    private static TextView letsGoTextView;

    private String selectedTeam = "A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);


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

        SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.BOOLEAN, AppConstant.PREFS_TEAM_SELECTED, true);
        SharedPreferencesHelper.savePreferences(this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_TEAM_SELECTED_ID, selectedTeam);
        launchMainActivity();
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
}
