package com.groomify.hollavirun;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.FbUser;
import com.groomify.hollavirun.rest.models.request.UpdateUserInfoRequest;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Response;

public class SOSActivity extends AppCompatActivity {

    Toolbar toolbar;
    RestClient client = new RestClient();
    private final static String TAG = SOSActivity.class.getSimpleName();

    private View firstAidPanel;
    private View emergencyContactPanel;
    private View groomifySupportPanel;
    private View setupEmergencyContactPanel;

    private TextView emergencyContactNameTextView;
    private TextView setupEmergencyContactTextView;
    private TextView changeContactTextView;

    public static boolean emergencyContactSelected = false;

    private final static int PICK_CONTACT = 100;
    private static final int PERMISSIONS_REQUEST = 101;

    private String emergencyContactName;
    private String emergencyContactNumber;

    public static String temporaryEmergencyContactNamePlaceHolder;

    Realm realm;
    GroomifyUser groomifyUser;
    Races currentRaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        Realm.init(this);
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());

        groomifyUser = realm.where(GroomifyUser.class).findFirst();
        Log.i(TAG,"Current race id: "+groomifyUser.getCurrentRaceId());

        currentRaces = realm.where(Races.class).equalTo("id", SharedPreferencesHelper.getSelectedRaceId(this)).findFirst();
        Log.i(TAG,"Current race query: "+currentRaces);

        emergencyContactName = groomifyUser.getEmergencyContactName();
        emergencyContactNumber = groomifyUser.getEmergencyContactPhoneNo();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String firstAidNumber = currentRaces.getFirstAid();
        String supportNumber = currentRaces.getGroomifySupport();

        Log.i(TAG, "First aid number: "+firstAidNumber);
        Log.i(TAG, "Support number: "+supportNumber);

        if(firstAidPanel == null){
            firstAidPanel = findViewById(R.id.first_aid_panel);
        }

        firstAidPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompCallConfirmationDialog(currentRaces.getFirstAid(), "Call for first aid: "+currentRaces.getFirstAid()+" ?");
            }
        });

        if(groomifySupportPanel == null){
            groomifySupportPanel = findViewById(R.id.groomify_support_panel);
        }

        groomifySupportPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompCallConfirmationDialog(currentRaces.getGroomifySupport(), "Call for groomify support now?");
            }
        });

        if(emergencyContactPanel == null){
            emergencyContactPanel = findViewById(R.id.emergency_contact_panel);
        }

        emergencyContactPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SOSActivity.emergencyContactSelected){
                    prompCallConfirmationDialog(emergencyContactNumber, "Call emergency contact "+emergencyContactName+" ?");
                }else{
                    Log.i(TAG, "Emergency contact is not set, prompt user to setup now.");
                    new AlertDialog.Builder(SOSActivity.this)
                            .setMessage("Emergency contact is not set.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Setup now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == AlertDialog.BUTTON_POSITIVE){
                                        setupEmergencyContact();
                                    }
                                }
                            }).setNegativeButton("Cancel", null).
                            show();



                }
            }
        });



        if(setupEmergencyContactPanel == null){
            setupEmergencyContactPanel = findViewById(R.id.set_emergency_contact_panel);
        }

        setupEmergencyContactPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupEmergencyContact();
            }
        });

        if(emergencyContactNameTextView == null){
            emergencyContactNameTextView = (TextView) findViewById(R.id.emergency_contact_name_text_view);
        }

        if(setupEmergencyContactTextView == null){
            setupEmergencyContactTextView = (TextView) findViewById(R.id.setup_emergency_contact_text_view);
        }

        if(changeContactTextView == null){
            changeContactTextView = (TextView) findViewById(R.id.change_contact_text_view);
        }

        /*SharedPreferences settings = getSharedPreferences(AppConstant.PREFS_NAME, 0);
        emergencyContactName = settings.getString(AppConstant.PREFS_EMERGENCY_CONTACT_NAME, null);
        emergencyContactNumber = settings.getString(AppConstant.PREFS_EMERGENCY_CONTACT_NUM, null);

        Log.i(TAG, "Emergency contact in pref: "+emergencyContactName+" - "+emergencyContactNumber);

        if(emergencyContactName != null){
            Log.i(TAG, "Emergency contact found. Name:"+emergencyContactName+", Number:"+emergencyContactNumber);
            emergencyContactSelected = true;

            emergencyContactNameTextView.setText(emergencyContactName);
        }else{
            emergencyContactSelected = false;
        }*/

        toggleSetupEmergencyPanel();

    }

    private void setupEmergencyContact(){
        if(!AppPermissionHelper.isContactPermissionGranted(this)){
            AppPermissionHelper.requestContactAndPhoneCallPermission(this);
            return;
        }

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
                    //String displayName = "";
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
                            temporaryEmergencyContactNamePlaceHolder = cursor.getString(nameIdx);
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
                                saveEmergencyContact(selectedNumber, temporaryEmergencyContactNamePlaceHolder);
                            }
                        });
                        AlertDialog alert = builder.create();
                        if(allNumbers.size() > 1) {
                            alert.show();
                        } else {
                            saveEmergencyContact(phoneNumber, temporaryEmergencyContactNamePlaceHolder);
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
        String selectedNumber = phoneNumber.replace("-", "");

        if(selectedNumber == null || selectedNumber.trim().length() <= 0){
            Toast.makeText(this,   "Selected contact does not have valid phone number.", Toast.LENGTH_LONG).show();
            return;
        }

        emergencyContactName = displayName;
        emergencyContactNumber = phoneNumber;

        new GroomifyUpdateProfileTask().execute();
    }

    private void toggleSetupEmergencyPanel(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (emergencyContactName != null && emergencyContactNumber != null &&
                        emergencyContactName.trim().length() > 0 && emergencyContactNumber.trim().length() > 0) {
                    emergencyContactSelected = true;
                    emergencyContactNameTextView.setText(emergencyContactName);
                } else {
                    emergencyContactSelected = false;
                }

                if (emergencyContactSelected) {
                    setupEmergencyContactTextView.setVisibility(View.INVISIBLE);
                    changeContactTextView.setVisibility(View.VISIBLE);
                    emergencyContactNameTextView.setVisibility(View.VISIBLE);
                } else {
                    setupEmergencyContactTextView.setVisibility(View.VISIBLE);
                    changeContactTextView.setVisibility(View.INVISIBLE);
                    emergencyContactNameTextView.setVisibility(View.INVISIBLE);
                }
            }});
    }


    private void prompCallConfirmationDialog(final String phoneNumber, String message){

        if(!AppPermissionHelper.isPhoneCallPermissionGranted(this)){
            Log.i(TAG, "Call phone permission is not granted. Request permission.");
            AppPermissionHelper.requestContactAndPhoneCallPermission(this);
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse("tel:" +phoneNumber));
                        startActivity(intent);
                        /*if(isPermissionGranted()){
                        }else{
                            requestPermission();
                        }*/
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

   /* private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    recreate();
                }
            } else {
                Toast.makeText(this, "You're require to grant phone call permission for emergency contact.", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }



    private class GroomifyUpdateProfileTask extends AsyncTask<Void, String, UserInfoResponse> {

        @Override
        protected UserInfoResponse doInBackground(Void... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(SOSActivity.this);
            String fbId = SharedPreferencesHelper.getFbId(SOSActivity.this);
            Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
            GroomifyUser realmUser = innerRealm.where(GroomifyUser.class).findFirst();


            try {
                Log.i(TAG, "Update profile picture to back-end system..");
                UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
                FbUser fbUser = new FbUser();
                fbUser.setEmergencyContactPerson(emergencyContactName);
                fbUser.setEmergencyContactPhone(emergencyContactNumber);
                updateUserInfoRequest.setFbUser(fbUser);
                Log.i(TAG, "Request ready to post:"+updateUserInfoRequest);

                Response<UserInfoResponse> restResponse = client.getApiService().updateUser(fbId, authToken, realmUser.getId(), updateUserInfoRequest).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling update user api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling update user api failed. response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to call update user api.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserInfoResponse userInfoResponse) {
            //TODO get the runner_id

            super.onPostExecute(userInfoResponse);
            if (userInfoResponse != null) {
                Log.i(TAG, "Update user info success, returning:" + userInfoResponse.toString());
                Realm innerRealm = Realm.getInstance(RealmUtils.getRealmConfiguration());
                innerRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        GroomifyUser realmUser = realm.where(GroomifyUser.class).findFirst();
                        realmUser.setEmergencyContactName(emergencyContactName);
                        realmUser.setEmergencyContactPhoneNo(emergencyContactNumber);
                    }
                });

                Toast.makeText(SOSActivity.this,   emergencyContactName+" ("+emergencyContactNumber+") has been configured as emergency number.", Toast.LENGTH_LONG).show();
                SharedPreferencesHelper.savePreferences(SOSActivity.this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_EMERGENCY_CONTACT_NAME, emergencyContactName);
                SharedPreferencesHelper.savePreferences(SOSActivity.this, SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_EMERGENCY_CONTACT_NAME, emergencyContactNumber);
                emergencyContactSelected = true;
                toggleSetupEmergencyPanel();

            } else {
                Toast.makeText(SOSActivity.this, "Unable to setup emergency contact at this moment. Please try again later.", Toast.LENGTH_SHORT).show();

            }


        }
    }
}
