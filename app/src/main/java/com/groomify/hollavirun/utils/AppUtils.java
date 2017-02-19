package com.groomify.hollavirun.utils;

import android.util.Log;

import java.io.IOException;

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
}
