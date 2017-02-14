package com.groomify.hollavirun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.groomify.hollavirun.constants.AppConstant;

/**
 * Created by Valkyrie1988 on 2/12/2017.
 */

public class SharedPreferencesHelper {

    public enum PreferenceValueType{
        STRING,BOOLEAN,INTEGER,LONG
    }

    public static void savePreferences(Context context, PreferenceValueType valueType, String key, Object value){

        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if(valueType == PreferenceValueType.STRING){
            editor.putString(key, (String) value);
        }else if(valueType == PreferenceValueType.BOOLEAN){
            editor.putBoolean(key, (Boolean) value);
        }else if(valueType == PreferenceValueType.INTEGER){
            editor.putInt(key, (Integer) value);
        }else if(valueType == PreferenceValueType.LONG){
            editor.putLong(key, (Long) value);
        }
        // Commit the edits!
        editor.commit();
    }

    public static String getAuthToken(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_AUTH_TOKEN, null);
    }

    public static String getFbId(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_FB_ID, null);
    }

    public static Integer getSelectedRaceId(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getInt(AppConstant.PREFS_RUN_SELECTED_ID, -1);
    }
}
