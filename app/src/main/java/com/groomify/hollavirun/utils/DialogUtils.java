package com.groomify.hollavirun.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.groomify.hollavirun.R;

/**
 * Created by Valkyrie1988 on 2/22/2017.
 */

public class DialogUtils {


    public static AlertDialog buildLoadingDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_loading).setCancelable(false);

        return builder.create();

    }

}
