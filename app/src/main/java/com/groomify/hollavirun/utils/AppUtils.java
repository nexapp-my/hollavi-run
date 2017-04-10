package com.groomify.hollavirun.utils;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Coupon;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.Team;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
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
                Mission.MISSION_TYPE_SCAN_AND_UPLOAD_ONE_PHOTO
    };

    private static int missionCoverResourceId[] = {
            R.drawable.mission_banner_01,
            R.drawable.mission_banner_02,
            R.drawable.mission_banner_04,
            R.drawable.mission_banner_04,
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


    public static LatLng[] getDefaultRaceTrack(){
        return new LatLng[]{
                new LatLng(3.0198028,101.579475),

                new LatLng(3.019686,101.579285),
                new LatLng(3.0206833,101.57884),
                new LatLng(3.0215333,101.57869),
                new LatLng(3.0216527,101.579414),
                new LatLng(3.021661,101.58014),
                new LatLng(3.021661,101.58098),
                new LatLng(3.0212166,101.5811),
                new LatLng(3.0208473,101.581116),
                new LatLng(3.020461,101.581154),


                new LatLng(3.0203333,101.58103),
                new LatLng(3.0199916,101.580864),
                new LatLng(3.0198028,101.579475),
                new LatLng(3.017464,101.5805),
                new LatLng(3.0159805,101.582115),
                new LatLng(3.01505,101.58224),
                new LatLng(3.0109973,101.582115),
                new LatLng(3.0094972,101.58172),
                new LatLng(3.0083194,101.5793),
                new LatLng(3.009175,101.57844),
                new LatLng(3.0112,101.577484),
                new LatLng(3.013125,101.57732),
                new LatLng(3.0153527,101.5773),
                new LatLng(3.016675,101.57679),
                new LatLng(3.0180361,101.57577),
                new LatLng(3.018279, 101.574645),

                /*new LatLng(3.0183055,101.57535),
                new LatLng(3.016825,101.576866),
                new LatLng(3.0146167,101.57743),
                new LatLng(3.0122056,101.57744),
                new LatLng(3.0085778,101.5789),
                new LatLng(3.0089056,101.58036),
                new LatLng(3.010089,101.58182),
                new LatLng(3.0134306,101.58211),
                new LatLng(3.0157027,101.58215),
                new LatLng(3.019686,101.579285),
                new LatLng(3.0206833,101.57884),
                new LatLng(3.0215333,101.57869),
                new LatLng(3.0216527,101.579414),
                new LatLng(3.021661,101.58014),
                new LatLng(3.021661,101.58098),
                new LatLng(3.0208473,101.581116),
                new LatLng(3.020461,101.581154),*/
        };
    }

    public static LatLng[] getDefaultMisionLatLng(){
        return new LatLng[]{
                new LatLng(3.0203333,101.58103),
                new LatLng(3.0198028,101.579475),
                new LatLng(3.0180361,101.57577),
                new LatLng(3.0146167,101.57743),
                new LatLng(3.0212166,101.5811)
        };
    }

    public static Coupon[] getDefaultCoupon(){

        Coupon[] coupons =new Coupon[4];

        Coupon talentsTechCoupon = new Coupon();
        talentsTechCoupon.setName("Your Talents Discovering Center");
        talentsTechCoupon.setDescription("Talents Assessment 20% Discount Voucher\nContact: 012-2926228\nVenue: Bandar Kinrara, Puchong");
        talentsTechCoupon.setExpirationTime(new GregorianCalendar(2017, 11, 31, 23, 59, 59).getTime());
        talentsTechCoupon.setRedeemed(false);
        talentsTechCoupon.setId(1);
        talentsTechCoupon.setResourceId(R.drawable.coupon_talents_tech);

        coupons[0] = talentsTechCoupon;

        Coupon elitezCoupon = new Coupon();
        elitezCoupon.setName("Mixed Martial Arts Fitness Academy");
        elitezCoupon.setDescription("Discount Voucher of 30% for Any Membership package purchase with Free Limited Edition Elitez Fight Tee worth RM80\n*Terms & Conditions applied.\nContact: 017-3699999\nVenue: Sg. Jati Klang, Setia Alam, Kelana Jaya PJ");
        elitezCoupon.setExpirationTime(new GregorianCalendar(2017,6, 31, 23, 59, 59).getTime());
        elitezCoupon.setRedeemed(false);
        elitezCoupon.setId(2);
        elitezCoupon.setResourceId(R.drawable.coupon_elitez);

        coupons[1] = elitezCoupon;

        Coupon unClubCoupon = new Coupon();
        unClubCoupon.setName("Street Dance Fitness & Dance Academy");
        unClubCoupon.setDescription("RM99/month for Unlimited Street Dance Fitness classes\n*Terms & Conditions applied.\nContact: 017-4021115\nVenue: Jalan Puteri, Puchong");
        unClubCoupon.setExpirationTime(new GregorianCalendar(2017, 3, 30, 23, 59, 59).getTime());
        unClubCoupon.setRedeemed(false);
        unClubCoupon.setId(3);
        unClubCoupon.setResourceId(R.drawable.coupon_un_club);

        coupons[2] = unClubCoupon;

        Coupon coffee1986Coupon = new Coupon();
        coffee1986Coupon.setName("Coffee Shop. Restaurant. Espresso Bar");
        coffee1986Coupon.setDescription("Cash Voucher RM10\n*Terms & Conditions applied.\nContact: 03-33592809\nVenue: Botanic & Setia Alam");
        coffee1986Coupon.setExpirationTime(new GregorianCalendar(2017, 4, 30, 23, 59, 59).getTime());
        coffee1986Coupon.setRedeemed(false);
        coffee1986Coupon.setId(4);
        coffee1986Coupon.setResourceId(R.drawable.coupon_1986_coffee);

        coupons[3] = coffee1986Coupon;

        return coupons;
    }
}
