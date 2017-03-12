package com.groomify.hollavirun.utils;

import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Valkyrie1988 on 2/19/2017.
 */

public class DebugUtils {

    private final static String TAG = "GROOMIFY-DEBUG";

    //To add key into Development phone.
    public static void printKeyHash(ContextWrapper contextWrapper){
        try {
            PackageInfo info = contextWrapper.getPackageManager().getPackageInfo(
                    contextWrapper.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,"Failed to print key hash.", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG,"Failed to print key hash.", e);
        }
    }
}
