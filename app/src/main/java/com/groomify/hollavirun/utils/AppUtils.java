package com.groomify.hollavirun.utils;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Spinner;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.Team;

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
            timeString += hours+"h ";
        }

        if(minutes != 0){
            timeString += minutes+"m ";
        }

        timeString += seconds+"s";

        return timeString;
    }


    public static String getFormattedHoursAndMinsFromSeconds(int totalSecs){
        String timeString = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        hours = totalSecs / 3600;
        minutes = (totalSecs % 3600) / 60;
        seconds = totalSecs % 60;

        if(seconds < 60){
            minutes++; //round up.
        }

        if(hours != 0){
            if(hours > 1){
                timeString += hours+" hours";
            }else{
                timeString += hours+" hour";
            }
        }

        if(hours <= 0 && minutes != 0){
            if(minutes > 1){
                timeString += minutes+" minutes";
            }else{
                timeString += minutes+" minute";
            }
        }

        return timeString;
    }


    public static int getSpinnerIndexByValue(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            //Log.i("AppUtils","Comparing "+spinner.getItemAtPosition(i).toString()+" to "+myString);
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
            "Let's go, GMF!",
            "Hunt the GEMS!",
            "Welcome to Planet Groomify!",
            "Are you the All-Round Champion?",
            "Hop on into the 3D World !"
    };

    private static String missionDesc[] = {
            "Take a selfie together with any one of your team member! EG: If you are in team Grooton, take a selfie with another runner in the same team.",
            "Follow your 6th sense and scan your desired QR CODE & you will stand a chance to win hidden gems.",
            "Home to our three leaders, GMF, a place where the virtual and reality world meets. Snap a picture in front of the green screen to reveal your very own Planet Groomify!",
            "Be smart like GROOTON, Be calm like MIKI, Be fast like FYRE! Challenge the Groomify Trivia & you are almost there to the last Pit Stop!",
            "Explore the 3D wall of fame with GMF"
    };

    private static int missionType[] = {
        Mission.MISSION_TYPE_SCAN_AND_UPLOAD_THREE_PHOTO,
                Mission.MISSION_TYPE_SCAN_ONLY,
                Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO,
                Mission.MISSION_TYPE_SCAN_AND_ANSWER_QUESTION,
                Mission.MISSION_TYPE_SCAN_AND_UPLOAD_THREE_PHOTO
    };

    private static int missionCoverResourceId[] = {
            R.drawable.mission_banner_01,
            R.drawable.mission_banner_02,
            R.drawable.mission_banner_04,
            R.drawable.mission_banner_05,
            R.drawable.mission_banner_06
    };

    public static Mission[] getDefaultMission(){
        Mission[] missions = new Mission[totalMission];
        for(int i = 0; i < totalMission; i++) {
            missions[i] = new Mission(null,
                    missionCoverResourceId[i],
                    missionDesc[i],
                    i + 1,
                    "",
                    "",
                    i + 1, missionTitle[i], false, "0000", missionType[i]);
        }
        return missions;
    }

    public static Team[] getDefaultTeam(){
        Team[] teams = new Team[3];
        teams[0] = new Team(R.drawable.team_3_ambassador, R.drawable.team_grooton_for_fb, "Grooton", "G");
        teams[1] = new Team(R.drawable.team_1_ambassador, R.drawable.team_miki_for_fb,"Miki", "M");
        teams[2] = new Team(R.drawable.team_2_ambassador, R.drawable.team_fyre_for_fb, "Fyre", "F");
        return teams;
    }

    public static int getPixelFromDIP(Context context, float targetDIP){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetDIP, context.getResources().getDisplayMetrics());
    }
}
