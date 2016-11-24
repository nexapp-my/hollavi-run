package com.groomify.hollavirun;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;

import java.util.ArrayList;
import java.util.List;

public class SOSActivity extends AppCompatActivity {

    Toolbar toolbar;

    private final static String TAG = SOSActivity.class.getSimpleName();

    private View firstAidPanel;
    private View emergencyContactPanel;
    private View groomifySupportPanel;
    private View setupEmergencyContactPanel;

    private TextView emergencyContactNameTextView;
    private TextView setupEmergencyContactTextView;
    private TextView changeContactTextView;

    boolean emergencyContactSelected = false;

    private final static int PICK_CONTACT = 100;

    private String emergencyContactName;
    private String emergencyContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(firstAidPanel == null){
            firstAidPanel = findViewById(R.id.first_aid_panel);
        }

        if(emergencyContactPanel == null){
            emergencyContactPanel = findViewById(R.id.emergency_contact_panel);
        }

        if(groomifySupportPanel == null){
            groomifySupportPanel = findViewById(R.id.groomify_support_panel);
        }

        if(setupEmergencyContactPanel == null){
            setupEmergencyContactPanel = findViewById(R.id.set_emergency_contact_panel);
        }

        setupEmergencyContactPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupEmergencyContact();
            }
        });

        if(emergencyContactName == null){
            emergencyContactNameTextView = (TextView) findViewById(R.id.emergency_contact_name_text_view);
        }

        if(setupEmergencyContactTextView == null){
            setupEmergencyContactTextView = (TextView) findViewById(R.id.setup_emergency_contact_text_view);
        }

        if(changeContactTextView == null){
            changeContactTextView = (TextView) findViewById(R.id.change_contact_text_view);
        }

        SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
        emergencyContactName = settings.getString(AppConstant.PREFS_EMERGENCY_CONTACT_NAME, null);
        emergencyContactNumber = settings.getString(AppConstant.PREFS_EMERGENCY_CONTACT_NUM, null);


        if(emergencyContactName != null){
            Log.i(TAG, "Emergency contact found. Name:"+emergencyContactName+", Number:"+emergencyContactNumber);
            emergencyContactSelected = true;
        }

        toggleSetupEmergencyPanel();

    }

    private void setupEmergencyContact(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data: "+data);
        switch (requestCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Cursor cursor = null;
                    String phoneNumber = "";
                    List<String> allNumbers = new ArrayList<String>();
                    String displayName = "";
                    int phoneIdx = 0;
                    int nameIdx = 0;
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        Log.i(TAG, "Contact id returend: "+id);

                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
                        phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME);
                        if (cursor.moveToFirst()) {
                            displayName = cursor.getString(nameIdx);
                            while (cursor.isAfterLast() == false) {
                                phoneNumber = cursor.getString(phoneIdx);
                                allNumbers.add(phoneNumber);
                                cursor.moveToNext();
                            }
                        } else {
                              Log.i(TAG, "Contact Cursor return empty....");                          //no results actions
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception while processing contacts", e);
                        //error actions
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        Log.i(TAG, "Total phone number in list: "+allNumbers.size());
                        final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SOSActivity.this);
                        builder.setTitle("Choose a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                String selectedNumber = items[item].toString();
                                selectedNumber = selectedNumber.replace("-", "");
                                saveEmergencyContact(emergencyContactNumber, emergencyContactName);
                            }
                        });
                        AlertDialog alert = builder.create();
                        if(allNumbers.size() > 1) {
                            alert.show();
                        } else {
                            saveEmergencyContact(phoneNumber, displayName);
                        }

                        if (phoneNumber.length() == 0) {
                            //no numbers found actions
                            Toast.makeText(SOSActivity.this, "No phone number has been selected.",Toast.LENGTH_SHORT);
                        }
                    }
                    break;
                }
                break;
        }


        /*Toast.makeText(this,  name + " ("+number+") has been configured as emergency number " + number, Toast.LENGTH_LONG).show();

        emergencyContactNameTextView.setText(name);

        SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppConstant.PREFS_EMERGENCY_CONTACT_NAME, name);
        editor.putString(AppConstant.PREFS_EMERGENCY_CONTACT_NUM, number);

        // Commit the edits!
        editor.commit();

        toggleSetupEmergencyPanel();*/
    }
    private void saveEmergencyContact(String phoneNumber, String displayName){
        String selectedNumber = phoneNumber.toString();
        selectedNumber = selectedNumber.replace("-", "");

        Toast.makeText(this,   displayName+" ("+phoneNumber+") has been configured as emergency number.", Toast.LENGTH_LONG).show();

        emergencyContactNameTextView.setText(displayName);

        SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppConstant.PREFS_EMERGENCY_CONTACT_NAME, displayName);
        editor.putString(AppConstant.PREFS_EMERGENCY_CONTACT_NUM, phoneNumber);

        // Commit the edits!
        editor.commit();
        emergencyContactSelected = true;

        toggleSetupEmergencyPanel();
    }

    private void toggleSetupEmergencyPanel(){
        if(emergencyContactSelected){
            setupEmergencyContactTextView.setVisibility(View.INVISIBLE);
            changeContactTextView.setVisibility(View.VISIBLE);
            emergencyContactNameTextView.setVisibility(View.VISIBLE);
        }else{
            setupEmergencyContactTextView.setVisibility(View.VISIBLE);
            changeContactTextView.setVisibility(View.INVISIBLE);
            emergencyContactNameTextView.setVisibility(View.INVISIBLE);
        }

    }
}
