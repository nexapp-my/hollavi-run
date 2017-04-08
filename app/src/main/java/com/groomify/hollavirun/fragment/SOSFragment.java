package com.groomify.hollavirun.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.groomify.hollavirun.BuildConfig;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.SOSActivity;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Races;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.FbUser;
import com.groomify.hollavirun.rest.models.request.FirstAidRequest;
import com.groomify.hollavirun.rest.models.request.LoginRequest;
import com.groomify.hollavirun.rest.models.request.UpdateUserInfoRequest;
import com.groomify.hollavirun.rest.models.response.UserInfoResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.DialogUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Response;

import static com.groomify.hollavirun.constants.AppConstant.FIREBASE_REMOTE_CONF_FIRSTAID_CONTACT_NUMBER;
import static com.groomify.hollavirun.constants.AppConstant.FIREBASE_REMOTE_CONF_USE_API_FOR_SOS;
import static com.groomify.hollavirun.constants.AppConstant.FIREBASE_REMOTE_CONF_USE_DEFAULT_MAP_COORDINATE;

public class SOSFragment extends Fragment {

    private RestClient client = new RestClient();
    private final static String TAG = SOSFragment.class.getSimpleName();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private View firstAidPanel;
    private View emergencyContactPanel;
    private View groomifySupportPanel;
    private View setupEmergencyContactPanel;

    private TextView emergencyContactNameTextView;
    private TextView setupEmergencyContactTextView;
    private TextView changeContactTextView;

    public boolean emergencyContactSelected = false;

    private final static int PICK_CONTACT = 100;
    private static final int PERMISSIONS_REQUEST = 101;

    private String emergencyContactName;
    private String emergencyContactNumber;

    public String temporaryEmergencyContactNamePlaceHolder;

    private Realm realm;
    private GroomifyUser groomifyUser;
    private Races currentRaces;

    private String dialogContentFirstAid;
    private String dialogContentGroomifySupport;
    private String dialogContentEmergencyContact;

    private Dialog loadingDialog;

    private boolean useAPIForSOS = false;
    private String sosNumber = "+601117572773";
    public SOSFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Realm.init(getActivity());
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());

        groomifyUser = realm.where(GroomifyUser.class).findFirst();
        Log.i(TAG,"Current race id: "+groomifyUser.getCurrentRaceId());

        currentRaces = realm.where(Races.class).equalTo("id", SharedPreferencesHelper.getSelectedRaceId(getActivity())).findFirst();
        Log.i(TAG,"Current race query: "+currentRaces);

        emergencyContactName = groomifyUser.getEmergencyContactName();
        emergencyContactNumber = groomifyUser.getEmergencyContactPhoneNo();

        String firstAidNumber = currentRaces.getFirstAid();
        String supportNumber = currentRaces.getGroomifySupport();

        Log.i(TAG, "First aid number: "+firstAidNumber);
        Log.i(TAG, "Support number: "+supportNumber);

        dialogContentFirstAid = getResources().getString(R.string.sos_call_dialog_first_aid);
        dialogContentGroomifySupport = getResources().getString(R.string.sos_call_dialog_support);
        dialogContentEmergencyContact = getResources().getString(R.string.sos_call_dialog_emergency_contact);
        fetchRemoteConfig();
        super.onCreate(savedInstanceState);
    }

    private void fetchRemoteConfig(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        useAPIForSOS = mFirebaseRemoteConfig.getBoolean(FIREBASE_REMOTE_CONF_USE_API_FOR_SOS);
        sosNumber = mFirebaseRemoteConfig.getString(FIREBASE_REMOTE_CONF_FIRSTAID_CONTACT_NUMBER);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Firebase remote config fetched.");
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.i(TAG, "Unable to fetch Firebase remote config.");
                            FirebaseCrash.log("Unable to fetch Firebase remote config.");
                        }

                    }
                });
    }

    private void setupEmergencyContact(){
        if(!AppPermissionHelper.isContactPermissionGranted(getActivity())){
            AppPermissionHelper.requestContactAndPhoneCallPermission(getActivity());
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "On SOS fragment. onCreateView");
        View view = inflater.inflate(R.layout.fragment_sos, container, false);


        firstAidPanel = view.findViewById(R.id.first_aid_panel);
        groomifySupportPanel = view.findViewById(R.id.groomify_support_panel);
        emergencyContactPanel = view.findViewById(R.id.emergency_contact_panel);
        setupEmergencyContactPanel = view.findViewById(R.id.set_emergency_contact_panel);

        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        loadingDialog.setTitle("First Aid");
        //TextView dialogTextView = (TextView) loadingDialog.findViewById(R.id.loading_text_view);
        //dialogTextView.setText("Requesting first aid...");

        firstAidPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useAPIForSOS){
                    prompRequestFirstAidConfirmation();
                }else{
                    prompCallConfirmationDialog(sosNumber, dialogContentFirstAid);
                }
            }
        });

        groomifySupportPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompCallConfirmationDialog(currentRaces.getGroomifySupport(), dialogContentGroomifySupport);
            }
        });

        emergencyContactPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emergencyContactSelected){
                    String dialogContent = dialogContentEmergencyContact.replaceAll("EC_NAME", emergencyContactName).replaceAll("EC_NO", emergencyContactNumber);
                    prompCallConfirmationDialog(emergencyContactNumber, dialogContent);
                }else{
                    Log.i(TAG, "Emergency contact is not set, prompt user to setup now.");
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Emergency contact is not set.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Setup now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == AlertDialog.BUTTON_POSITIVE){
                                        setupEmergencyContact();
                                    }
                                }
                            }).setNegativeButton("Later", null).
                            show();
                }
            }
        });

        setupEmergencyContactPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupEmergencyContact();
            }
        });


        emergencyContactNameTextView = (TextView) view.findViewById(R.id.emergency_contact_name_text_view);
        setupEmergencyContactTextView = (TextView) view.findViewById(R.id.setup_emergency_contact_text_view);
        changeContactTextView = (TextView) view.findViewById(R.id.change_contact_text_view);

        toggleSetupEmergencyPanel();

        return view;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            Toast.makeText(getActivity(), "No phone number has been selected.",Toast.LENGTH_SHORT);
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
            Toast.makeText(getActivity(), "Selected contact does not have valid phone number.", Toast.LENGTH_LONG).show();
            return;
        }

        emergencyContactName = displayName;
        emergencyContactNumber = phoneNumber;

        new GroomifyUpdateProfileTask().execute();
    }

    private void toggleSetupEmergencyPanel(){

        Log.i(TAG, "Emergency contact name: "+emergencyContactName+", Emergency contact number:"+emergencyContactNumber);

        if (emergencyContactName != null && emergencyContactNumber != null &&
                emergencyContactName.trim().length() > 0 && emergencyContactNumber.trim().length() > 0) {
            emergencyContactSelected = true;
            emergencyContactNameTextView.setText(emergencyContactName);
        } else {
            emergencyContactSelected = false;
        }
        Log.i(TAG, "Emergency contact selected? "+emergencyContactSelected);

        if (emergencyContactSelected) {
            setupEmergencyContactTextView.setVisibility(View.INVISIBLE);
            changeContactTextView.setVisibility(View.VISIBLE);
            emergencyContactNameTextView.setVisibility(View.VISIBLE);
            Log.i(TAG, "Hide the setup button? "+emergencyContactSelected);
        } else {
            setupEmergencyContactTextView.setVisibility(View.VISIBLE);
            changeContactTextView.setVisibility(View.INVISIBLE);
            emergencyContactNameTextView.setVisibility(View.INVISIBLE);
        }

    }


    private void prompCallConfirmationDialog(final String phoneNumber, String message){

        Log.i(TAG, "prompCallConfirmationDialog");
        if(!AppPermissionHelper.isPhoneCallPermissionGranted(getActivity())){
            Log.i(TAG, "Call phone permission is not granted. Request permission.");
            AppPermissionHelper.requestContactAndPhoneCallPermission(getActivity());
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("CALL NOW", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse("tel:" +phoneNumber));
                        startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void prompRequestFirstAidConfirmation(){

        new AlertDialog.Builder(getActivity())
                .setMessage("Request for First Aid?")
                .setPositiveButton("Request First Aid", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.show();
                        new GroomifyCallFirstAidTask().execute();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();

        loadingDialog.show();
        loadingDialog.dismiss();

    }


    private class GroomifyUpdateProfileTask extends AsyncTask<Void, String, UserInfoResponse> {

        @Override
        protected UserInfoResponse doInBackground(Void... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(getActivity());
            String fbId = SharedPreferencesHelper.getFbId(getActivity());
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

                Toast.makeText(getActivity(),   emergencyContactName+" ("+emergencyContactNumber+") has been configured as emergency number.", Toast.LENGTH_LONG).show();
                SharedPreferencesHelper.savePreferences(getActivity(), SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_EMERGENCY_CONTACT_NAME, emergencyContactName);
                SharedPreferencesHelper.savePreferences(getActivity(), SharedPreferencesHelper.PreferenceValueType.STRING, AppConstant.PREFS_EMERGENCY_CONTACT_NUM, emergencyContactNumber);
                emergencyContactSelected = true;
                toggleSetupEmergencyPanel();

            } else {
                Toast.makeText(getActivity(), "Unable to setup emergency contact at this moment. Please try again later.", Toast.LENGTH_SHORT).show();

            }


        }
    }


    private class GroomifyCallFirstAidTask extends AsyncTask<Void, Void, Response<Void>> {

        @Override
        protected Response<Void> doInBackground(Void... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(getActivity());
            String fbId = SharedPreferencesHelper.getFbId(getActivity());

            FirstAidRequest firstAidRequest = new FirstAidRequest();
            if(AppConstant.currentLocation != null){
                firstAidRequest.setLng(AppConstant.currentLocation.longitude);
                firstAidRequest.setLat(AppConstant.currentLocation.latitude);
            }else{
                firstAidRequest.setLng(0.0);
                firstAidRequest.setLat(0.0);
            }

            try {
                Response<Void> restResponse = client.getApiService().callFirstAid(fbId, authToken, firstAidRequest).execute();
                return restResponse;
            } catch (Exception e) {
                Log.e(TAG, "Unable to call for first aid.", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response<Void> restResponse) {
            super.onPostExecute(restResponse);
            loadingDialog.dismiss();
            if(restResponse != null && restResponse.isSuccessful()){
                Toast.makeText(getContext(), "First Aid request sent.",  Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Unable to call for first aid at this moment. Please try again.",  Toast.LENGTH_SHORT).show();
            }
        }
    }
}
