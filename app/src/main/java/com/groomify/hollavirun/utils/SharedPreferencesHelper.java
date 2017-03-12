package com.groomify.hollavirun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.groomify.hollavirun.constants.AppConstant;

/**
 * Created by Valkyrie1988 on 2/12/2017.
 */

public class SharedPreferencesHelper {

    public enum PreferenceValueType {
        STRING, BOOLEAN, INTEGER, LONG
    }

    public static void savePreferences(Context context, PreferenceValueType valueType, String key, Object value) {

        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (valueType == PreferenceValueType.STRING) {
            editor.putString(key, (String) value);
        } else if (valueType == PreferenceValueType.BOOLEAN) {
            editor.putBoolean(key, (Boolean) value);
        } else if (valueType == PreferenceValueType.INTEGER) {
            editor.putInt(key, (Integer) value);
        } else if (valueType == PreferenceValueType.LONG) {
            editor.putLong(key, (Long) value);
        }
        // Commit the edits!
        editor.commit();
    }


    public static String getAuthToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_AUTH_TOKEN, null);
    }

    public static String getFbId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_FB_ID, null);
    }

    public static Long getSelectedRaceId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getLong(AppConstant.PREFS_RUN_SELECTED_ID, -1);
    }

    public static Long getUserId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getLong(AppConstant.PREFS_USER_ID, -1);
    }

    public static Boolean isProfilePictureUpdated(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getBoolean(AppConstant.PREFS_PROFILE_PIC_UPDATED, false);
    }

    public static Boolean isTeamSelected(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getBoolean(AppConstant.PREFS_TEAM_SELECTED, false);
    }

    public static String getTeamId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_TEAM_SELECTED_ID, null);
    }

    public static String getBibNo(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_BIB_NO, null);
    }

    public static String getRunnerId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getString(AppConstant.PREFS_RUNNER_ID, null);
    }

    public static boolean isMissionUnlocked(Context context, Long raceId, Integer missionId) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        String prefKey = AppConstant.PREFS_MISSION_UNLOCK_PREFIX + "_" + raceId + "_" + missionId;
        return settings.getBoolean(prefKey, false);
    }

    public static boolean isMissionSubmitted(Context context, Long raceId, Integer missionId) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        String prefKey = AppConstant.PREFS_MISSION_SUBMITTED_PREFIX + "_" + raceId + "_" + missionId;
        return settings.getBoolean(prefKey, false);
    }

    public static String getMissionUnlockTime(Context context, Long raceId, Integer missionId) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        String prefKey = AppConstant.PREFS_MISSION_UNLOCK_TIME_PREFIX + "_" + raceId + "_" + missionId;
        return settings.getString(prefKey, null);
    }

    public static void setMissionUnlocked(Context context, Long raceId, Integer missionId, boolean unlocked) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String prefKey = AppConstant.PREFS_MISSION_UNLOCK_PREFIX + "_" + raceId + "_" + missionId;
        editor.putBoolean(prefKey, unlocked);
        editor.commit();
    }

    public static void setMissionUnlockedTime(Context context, Long raceId, Integer missionId, String dateTime) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String prefKey = AppConstant.PREFS_MISSION_UNLOCK_TIME_PREFIX + "_" + raceId + "_" + missionId;
        editor.putString(prefKey, dateTime);
        editor.commit();
    }

    public static void setMissionSubmitted(Context context, Long raceId, Integer missionId, boolean submitted) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String prefKey = AppConstant.PREFS_MISSION_SUBMITTED_PREFIX + "_" + raceId + "_" + missionId;
        editor.putBoolean(prefKey, submitted);
        editor.commit();
    }

    public static void setMissionFirstAttemptsTime(Context context, Long raceId, Integer missionId, String dateTime) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String prefKey = AppConstant.PREFS_MISSION_FIRST_ATTEMPTS_TIME_PREFIX + "_" + raceId + "_" + missionId;
        editor.putString(prefKey, dateTime);
        editor.commit();
    }

    public static String getMissionFirstAttemptsTime(Context context, Long raceId, Integer missionId) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        String prefKey = AppConstant.PREFS_MISSION_FIRST_ATTEMPTS_TIME_PREFIX + "_" + raceId + "_" + missionId;
        return settings.getString(prefKey, null);
    }

    public static void setRaceExpirationTime(Context context, Long raceId, String expirationTime) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppConstant.PREFS_RUN_EXPIRATION_TIME + "_" + raceId, expirationTime);
        editor.commit();
    }

    public static String getRaceExpirationTime(Context context, Long raceId) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        String prefKey = AppConstant.PREFS_RUN_EXPIRATION_TIME + "_" + raceId;
        return settings.getString(prefKey, null);
    }

    public static boolean isRunAsGuest(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getBoolean(AppConstant.PREFS_RUN_AS_GUEST, false);
    }

    public static void setCouponRedeemed(Context context, Long raceId, Integer couponId, boolean redeemed) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String prefKey = AppConstant.PREFS_COUPON_REDEEMED_PREFIX + "_" + raceId + "_" + couponId;
        editor.putBoolean(prefKey, redeemed);
        editor.commit();
    }

    public static boolean isCouponRedeemed(Context context, Long raceId, Integer couponId) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        String prefKey = AppConstant.PREFS_COUPON_REDEEMED_PREFIX + "_" + raceId + "_" + couponId;
        return settings.getBoolean(prefKey, false);
    }


    public static boolean isTermAndConditionAccepted(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        return settings.getBoolean(AppConstant.PREFS_TERM_AND_CONDITION_ACCEPTED, false);
    }

    public static void setTermAndConditionAccepted(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(AppConstant.PREFS_TERM_AND_CONDITION_ACCEPTED, true);
        editor.commit();

    }
}
