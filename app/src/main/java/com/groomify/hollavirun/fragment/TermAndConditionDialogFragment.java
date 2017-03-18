package com.groomify.hollavirun.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groomify.hollavirun.FullScreenImageActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.SplashActivity;
import com.groomify.hollavirun.TermsAndConditionActivity;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

/**
 * Created by Valkyrie1988 on 3/1/2017.
 */

public class TermAndConditionDialogFragment extends DialogFragment {

    private final static String TAG = TermAndConditionDialogFragment.class.getSimpleName();
    private View termsOfUseText;
    private View privacyPolicyText;
    NoticeDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.dialog_term_and_condition, null);


        builder.setCancelable(false).setView(contentView)
                .setPositiveButton("I AGREE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(TermAndConditionDialogFragment.this);
                    }

                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(TermAndConditionDialogFragment.this);
                    }
                });

        Dialog dialog = builder.create();
        termsOfUseText = contentView.findViewById(R.id.term_link_text);
        privacyPolicyText = contentView.findViewById(R.id.privacy_link_text);

        termsOfUseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Term clicked.");
                Intent intent = new Intent(getActivity(), TermsAndConditionActivity.class);
                intent.putExtra("TYPE", 1);
                startActivity(intent);
            }
        });

        privacyPolicyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Privacy clicked.");
                Intent intent = new Intent(getActivity(), TermsAndConditionActivity.class);
                intent.putExtra("TYPE", 2);
                startActivity(intent);
            }
        });


        return dialog;
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
