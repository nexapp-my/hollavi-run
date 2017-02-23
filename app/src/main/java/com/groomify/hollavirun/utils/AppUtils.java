package com.groomify.hollavirun.utils;

import android.util.Log;
import android.widget.Spinner;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Mission;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Valkyrie1988 on 2/19/2017.
 */

public class AppUtils {

    private static final String TAG = "GROOMIFY-APP-UTILS";

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          {
            Log.i(TAG, "IO exception while access android ping file", e);
        }
        catch (InterruptedException e) {
            Log.i(TAG, "Ping runtime execution interrupted.", e);
        }
        return false;
    }

    public static String getFormattedTimeFromSeconds(int totalSecs){
        String timeString = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        hours = totalSecs / 3600;
        minutes = (totalSecs % 3600) / 60;
        seconds = totalSecs % 60;

        if(hours != 0){
            timeString += hours+" h ";
        }

        if(minutes != 0){
            timeString += minutes+" m ";
        }

        timeString += seconds+" s";

        return timeString;
    }


    public static int getSpinnerIndexByValue(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            Log.i("AppUtils","Comparing "+spinner.getItemAtPosition(i).toString()+" to "+myString);
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static int totalMission = 5;

    private static String missionTitle[] = {
            "SELFIE WITH STRANGERS",
            "POKEMON GO",
            "DONT'T TEXT & DRIVE",
            "THE 3D JOURNEY",
            "UPSIDE DOWN WORLD"
    };

    private static String missionDesc[] = {
            "Take 3 selfies with your fellow runners",
            "Spot the real Pikachu",
            "Prepare to be mindblown",
            "Meet your favourite Superheroes.",
            "Meet your favourite Superheroes."
    };

    private static int missionCoverResourceId[] = {
            R.drawable.mission_banner_01,
            R.drawable.mission_banner_02,
            R.drawable.mission_banner_03,
            R.drawable.mission_banner_04,
            R.drawable.mission_banner_05
    };

    public static Mission[] getDefaultMission(){
        /*new Mission(1, "01", "SELFIE WITH STRANGERS", "Take 3 selfies with your fellow runners", R.drawable.mission_banner_01, true),
                new Mission(2, "02", "POKEMON GO", "Spot the real Pikachu", R.drawable.mission_banner_02, false),
                new Mission(3, "03", "DONT'T TEXT & DRIVE", "Prepare to be mindblown.", R.drawable.mission_banner_03, false),
                new Mission(4, "04", "INFLATABLE CASTLE", "Meet your favourite Superheroes.", R.drawable.mission_banner_04, false),
                new Mission(5, "05", "THE 3D JOURNEY", "Meet your favourite Superheroes.", R.drawable.mission_banner_05, false),
                new Mission(6, "06", "UPSIDE DOWN WORLD", "Meet your favourite Superheroes.", R.drawable.mission_banner_06, false)*/
        Mission[] missions = new Mission[totalMission];
        for(int i = 0; i < totalMission; i++) {
            missions[i] = new Mission(null,
                    missionCoverResourceId[i],
                    missionDesc[i],
                    i + 1,
                    "",
                    "",
                    i + 1, missionTitle[i], false, "0000");
        }
        return missions;
    }
}
