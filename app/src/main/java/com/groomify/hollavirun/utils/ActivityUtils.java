package com.groomify.hollavirun.utils;

import android.app.Activity;
import android.content.Intent;

import com.groomify.hollavirun.MainActivity;
import com.groomify.hollavirun.OnboardingActivity;
import com.groomify.hollavirun.SelectRaceActivity;
import com.groomify.hollavirun.SplashActivity;
import com.groomify.hollavirun.TeamSelectActivity;
import com.groomify.hollavirun.WelcomeActivity;

/**
 * Created by Valkyrie1988 on 2/19/2017.
 */

public class ActivityUtils {

    public static void launchRaceSelectionScreen(Activity parentActivity, boolean finishParent){
        Intent sosIntent = new Intent(parentActivity, SelectRaceActivity.class);
        parentActivity.startActivity(sosIntent);
        if(finishParent){
            parentActivity.finish();
        }
    }

    public static void launchTeamSelectionScreen(Activity parentActivity, boolean finishParent){
        Intent intent = new Intent(parentActivity, TeamSelectActivity.class);
        parentActivity.startActivity(intent);
        if(finishParent){
            parentActivity.finish();
        }
    }

    public static void launchOnboardingScreen(Activity parentActivity, boolean finishParent){
        Intent intent = new Intent(parentActivity, OnboardingActivity.class);
        parentActivity.startActivity(intent);
        if(finishParent){
            parentActivity.finish();
        }
    }

    public static void launchMainScreen(Activity parentActivity, boolean finishParent){
        Intent intent = new Intent(parentActivity, MainActivity.class);
        parentActivity.startActivity(intent);
        if(finishParent){
            parentActivity.finish();
        }
    }

    public static void launchWelcomeScreen(Activity parentActivity, boolean finishParent){
        Intent intent = new Intent(parentActivity, WelcomeActivity.class);
        parentActivity.startActivity(intent);
        if(finishParent){
            parentActivity.finish();
        }
    }
}
