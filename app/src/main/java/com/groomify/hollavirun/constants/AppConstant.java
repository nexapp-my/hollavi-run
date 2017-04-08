package com.groomify.hollavirun.constants;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Valkyrie1988 on 11/6/2016.
 */

public class AppConstant {


    public static final String PREFS_NAME = "groomify_run_pref";

    public static final String PREFS_USER_LOGGGED_IN = "user_logged_in";

    public static final String PREFS_PROFILE_PIC_UPDATED = "profile_updated";
    public static final String PREFS_RUN_SELECTED = "run_selected";
    public static final String PREFS_RUN_SELECTED_ID = "run_selected_id";
    public static final String PREFS_BIB_NO = "bib_no";
    public static final String PREFS_RUNNER_ID = "runner_id";
    public static final String PREFS_RUN_EXPIRATION_TIME = "run_expiration_time";
    public static final String PREFS_RUN_AS_GUEST = "run_as_guest";

    public static final String PREFS_TEAM_SELECTED = "team_selected";
    public static final String PREFS_TEAM_SELECTED_ID = "team_selected_id";


    public static final String PREFS_USER_ID = "user_id";

    public static final String PREFS_EMERGENCY_CONTACT_NAME = "emergency_contact_name";
    public static final String PREFS_EMERGENCY_CONTACT_NUM = "emergency_contact_num";

    public static final String PREFS_FB_ID = "fb_id";
    public static final String PREFS_AUTH_TOKEN = "auth_token";


    public static final String PREFS_FIRST_TIME_SETUP_COMPLETE = "first_time_setup_completed";

    public static final String DEFAULT_BIB_NO = "-9999";

    public static final String PREFS_MISSION_UNLOCK_PREFIX = "mission_unlock_prefix_";
    public static final String PREFS_MISSION_SUBMITTED_PREFIX = "mission_submitted_prefix_";
    public static final String PREFS_MISSION_UNLOCK_TIME_PREFIX = "mission_unlock_time_prefix_";
    public static final String PREFS_MISSION_FIRST_ATTEMPTS_TIME_PREFIX = "mission_first_attempts_time_prefix_";

    public static final String PREFS_COUPON_REDEEMED_PREFIX = "coupon_redeemed_prefix_";

    public static final String PREFS_TERM_AND_CONDITION_ACCEPTED = "term_and_condition_accepted";

    public static final String PREFS_SCAN_AND_WIN_QR_CODE = "scan_and_win_qr_code";

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String JSON_DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'";


    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg","png");

    public static final String CAMERA_IMAGE_DIRECTORY = "CAMERA";

    public static LatLng currentLocation = null;

    public static final String FIREBASE_REMOTE_CONF_USE_DEFAULT_MAP_COORDINATE = "use_default_map_coordinate";
    public static final String FIREBASE_REMOTE_CONF_USE_DEFAULT_COUPON_LIST = "use_default_coupon_list";
    public static final String FIREBASE_REMOTE_CONF_USE_API_FOR_SOS = "use_api_for_sos";
    public static final String FIREBASE_REMOTE_CONF_FIRSTAID_CONTACT_NUMBER = "first_aid_contact_no";

}
