package com.groomify.hollavirun.utils;

import android.util.Log;
import android.widget.Spinner;

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
}
