package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.groomify.hollavirun.LatestUpdateActivity;
import com.groomify.hollavirun.MissionDetailsActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.RunGalleryActivity;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.entities.Team;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.request.UpdateUserLocationRequest;
import com.groomify.hollavirun.rest.models.request.UserLocation;
import com.groomify.hollavirun.rest.models.response.Info;
import com.groomify.hollavirun.rest.models.response.Mission_;
import com.groomify.hollavirun.rest.models.response.RaceDetailResponse;
import com.groomify.hollavirun.rest.models.response.RaceInfoResponse;
import com.groomify.hollavirun.rest.models.response.RaceTrackCoordinate;
import com.groomify.hollavirun.rest.models.response.SearchRunnerLocationResponse;
import com.groomify.hollavirun.rest.models.response.UpdateUserLocationResponse;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.groomify.hollavirun.view.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Response;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final static int MAX_LATEST_NEWS_CONTENT = 50;

    private final static String TAG = MainFragment.class.getSimpleName();

    public static ProfilePictureView profilePictureView;

    /*private RecyclerView horizonRecyclerView;
    private ArrayList<MissionCard> missionCards;
    private HorizontalAdapter horizontalAdapter;*/

    //public boolean isFirstTimeInitialized = true;

    private View latestNewsFloatPane = null;
    private TextView latestNewsTitle= null;
    private TextView latestNewsDesc= null;
    private TextView latestNewsTimestamp = null;
    private ImageView latestNewsCover = null;
    private ImageView runGalleryBtn = null;

    public EditText editText;
    public MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private Context context;

    private int maxBibNo = 4;
    private LatLngBounds MALAYSIA = new LatLngBounds(
            new LatLng(1.2, 100),new LatLng(7.0, 104.3));


    public GoogleMap googleMap = null;
    public Marker searchResultMarker = null;

    private Realm realm;
    private RestClient client = new RestClient();
    private String runnerId;
    private Long raceId;
    private RaceDetailResponse raceDetailResponse;

    private Mission[] missions;

    int previousClickedMarker = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "On create view");
        super.onCreate(savedInstanceState);
        context = this.getContext();

        Realm.init(this.getContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoadUtils.initImageLoader(this.getContext());
        maxBibNo = getResources().getInteger(R.integer.max_bib_no);
        runnerId = SharedPreferencesHelper.getRunnerId(getContext());
        raceId = SharedPreferencesHelper.getSelectedRaceId(getContext());
        missions = AppUtils.getDefaultMission();
        raceDetailResponse = realm.where(RaceDetailResponse.class).equalTo("id", raceId).findFirst();
        Log.i(TAG, "GOD BLESS ME PLEASE WORK!!!!!"+raceDetailResponse);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "On create view");
        View mainFragment = inflater.inflate(R.layout.fragment_main, container, false);

        editText = (EditText) mainFragment.findViewById(R.id.search_runner_field);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxBibNo);
        editText.setFilters(filterArray);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_VARIATION_NORMAL);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) v;
                editText.setHint("");
                editText.setText("");
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    EditText editText = (EditText) v;
                    editText.setHint("");
                    editText.setText("");
                }else{
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        latestNewsFloatPane = mainFragment.findViewById(R.id.latest_news_float_pane);
        latestNewsTitle = (TextView) mainFragment.findViewById(R.id.latest_news_title_text_view);
        latestNewsDesc= (TextView) mainFragment.findViewById(R.id.latest_news_desc_text_view);
        latestNewsTimestamp = (TextView) mainFragment.findViewById(R.id.latest_news_timestamp_text_view);
        latestNewsCover= (ImageView) mainFragment.findViewById(R.id.latest_news_image_view);

        latestNewsFloatPane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLatestUpdateScreen();
            }
        });

        runGalleryBtn = (ImageView) mainFragment.findViewById(R.id.run_gallery_btn);
        runGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RunGalleryActivity.class);
                startActivity(intent);
            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


        Log.i(TAG, "Creating map view.");
        mMapView = (MapView) mainFragment.findViewById(R.id.map);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        initializeNewsView();

        return mainFragment;

    }

    private void performSearch(String bibNo){
        editText.clearFocus();
        Log.i(TAG, "Performing search for bibNo: "+bibNo);
        if(bibNo.trim().length() > 0){
            Toast.makeText(getContext(), "Searching runner...", Toast.LENGTH_SHORT).show();
            new GroomifyGetRunnerLocationTask().execute(bibNo);
        }

    }

    private void launchLatestUpdateScreen(){
        Intent intent = new Intent(context, LatestUpdateActivity.class);
        startActivity(intent);
    }


    private void initializeNewsView(){
        Team[] teams = AppUtils.getDefaultTeam();
        String teamName = SharedPreferencesHelper.getTeamId(getContext());
        for(int i = 0; i < teams.length; i++){
            if(teams[i].getTeamName().equals(teamName)){
                latestNewsCover.setImageResource(teams[i].getResourceId());
                break;
            }
        }
        //NOTE, now will not latest news to show here anymore.
        /*
        RealmResults<NewsFeed> newsFeedRealmResults = realm.where(NewsFeed.class).findAll();

        Log.i(TAG, "Total news feed from database: "+newsFeedRealmResults);


        //TODO get the first item for latest news

        if(newsFeedRealmResults.size() > 0){
            NewsFeed latestNews = newsFeedRealmResults.get(1);

            latestNewsTimestamp.setText(latestNews.getTimeStamp());
            latestNewsTitle.setText(latestNews.getHeader());
            if(latestNews.getContent().length() > MAX_LATEST_NEWS_CONTENT){
                String trimContent = latestNews.getContent().substring(0, MAX_LATEST_NEWS_CONTENT) + "...";
                latestNewsDesc.setText(trimContent);
            }else{
                latestNewsDesc.setText(latestNews.getContent());
            }

            ImageLoader.getInstance().displayImage(latestNews.getCoverPhotoUrl(), latestNewsCover, ImageLoadUtils.getDisplayImageOptions());
        }else{
            latestNewsFloatPane.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {


        try {
            googleMap = map;
            // Constrain the camera target to the Adelaide bounds.
            map.setLatLngBoundsForCameraTarget(MALAYSIA);

            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

            map.getUiSettings().setZoomGesturesEnabled(true);
            if (!AppPermissionHelper.isLocationPermissionGranted(context)) {
                Log.i(TAG,"Location permission is not granted.");
            }else{
                map.setMyLocationEnabled(true);
            }

            int i = 0;
            for(Mission_ mission_: raceDetailResponse.getMissions()){
                Double missionLat = Double.parseDouble(mission_.getLat());
                Double missionLng = Double.parseDouble(mission_.getLng());
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(missionLat, missionLng))
                        .title(mission_.getTitle())
                        .snippet(mission_.getDescription())
                );
                marker.setTag(i);
                i++;
            }
/*
            Marker marketMission1;
            Marker marketMission2;
            Marker marketMission3;

            marketMission1 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(3.074028, 101.606791))
                    .title("Mission 1").flat(true).alpha(0.5f));
                            //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            marketMission1.setTag(0);

            marketMission2 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(3.069393, 101.610725))
                    .title("Mission 2"));
            marketMission2.setTag(0);

            marketMission3 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(3.065279, 101.603333))
                    .title("Mission 3"));
            marketMission3.setTag(0);


            marketMission3 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(3.065279, 101.603333))
                    .title("Mission 4"));
            marketMission3.setTag(0);*/
            //3.064059, 101.579973

            PolylineOptions rectOptions = new PolylineOptions().color(ResourcesCompat.getColor(getResources(), R.color.rustyRed, null));
            Double startLat = Double.parseDouble(raceDetailResponse.getStartPoint().getLat());
            Double startLng;
            try {
                startLng = Double.parseDouble(raceDetailResponse.getStartPoint().getLng());
            }catch (Exception e){
                startLng = 101.5710442;
            }
            rectOptions.add(new LatLng(startLat, startLng));

            for(RaceTrackCoordinate raceTrackCoordinate: raceDetailResponse.getRaceTrackCoordinates()){
                Double lng = Double.parseDouble(raceTrackCoordinate.getLng());
                Double lat = Double.parseDouble(raceTrackCoordinate.getLat());
                rectOptions.add(new LatLng(lat, lng));
            }

            Double endLat =  Double.parseDouble(raceDetailResponse.getEndPoint().getLat());
            Double endLng =  Double.parseDouble(raceDetailResponse.getEndPoint().getLng());
            rectOptions.add(new LatLng(endLat, endLng));

            Double zoomCenterLatLng = raceDetailResponse.getMapInfo().getMapCenter().getLat();
            int zoomLevel = raceDetailResponse.getMapInfo().getZoomLevel();

            Log.i(TAG, "Start point: "+startLat+", "+startLng);
            Log.i(TAG, "End point: "+endLat+", "+endLng);
            Log.i(TAG, "Total poly coordinate: "+raceDetailResponse.getRaceTrackCoordinates().size());
            Log.i(TAG, "Zoom center : "+zoomCenterLatLng);
            Log.i(TAG, "Zoom level: "+zoomLevel);

             // Get back the mutable Polyline
            Polyline polyline = map.addPolyline(rectOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(zoomCenterLatLng, zoomCenterLatLng), zoomLevel));

            googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
            googleMap.setOnMarkerClickListener(this);

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        } catch ( java.lang.SecurityException e){
            Log.w(TAG, "Application does not have location permission.", e);
        } catch (Exception ex){
            Log.e(TAG, "Failed to instantiate google map.", ex);
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy, map destroyed.");
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int missionNo = (int) marker.getTag();

        Log.i(TAG, "Marker clicked.");
        if(previousClickedMarker == missionNo) {
            Intent intent = new Intent(getActivity(), MissionDetailsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable("MISSION", missions[missionNo]);
            intent.putExtra("EXTRA_MISSION", bundle);
            getActivity().startActivity(intent);
            previousClickedMarker = missionNo;
            return false;
        }else{
            previousClickedMarker = missionNo;
            return false;
        }

    }


    private class GroomifyGetRunnerLocationTask extends AsyncTask<String, Void, SearchRunnerLocationResponse> {

        @Override
        protected SearchRunnerLocationResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(getContext());
            String fbId = SharedPreferencesHelper.getFbId(getContext());
            String bibNo = params[0];

            try {
                Response<SearchRunnerLocationResponse> restResponse = client.getApiService().searchRunnerLocation(fbId, authToken, bibNo).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling search runner api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling search runner api failed. response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to call search runner api.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchRunnerLocationResponse runnerInfoResponse) {

            super.onPostExecute(runnerInfoResponse);

            if (runnerInfoResponse != null) {
                Log.i(TAG, "Dropping marker.");
                if(searchResultMarker != null){
                    searchResultMarker.remove();
                }
                double lat = Double.parseDouble(runnerInfoResponse.getLocation().getLat());
                double lng = Double.parseDouble(runnerInfoResponse.getLocation().getLng());
                searchResultMarker =  googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .title(runnerInfoResponse.getRunBibNo()).snippet(runnerInfoResponse.getTeam()).flat(true).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_runner_marker)));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14));

                //Fancy shit
                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                Toast.makeText(getContext(), "Runner location not found. Please try again later.", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            Log.i(TAG, "Detected user location changed. Fire update event.");
            new GroomifyUpdateLocationTask().execute(location);

        }
    };


    //TODO might need a thread periodicaly pull the news.
    private class GroomifyUpdateLocationTask extends AsyncTask<Location, Void, UpdateUserLocationResponse> {

        @Override
        protected UpdateUserLocationResponse doInBackground(Location... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(getContext());
            String fbId = SharedPreferencesHelper.getFbId(getContext());
            try {

                Location location = params[0];
                UpdateUserLocationRequest updateUserLocationRequest = new UpdateUserLocationRequest();
                UserLocation userLocation = new UserLocation();
                userLocation.setLat(location.getLatitude());
                userLocation.setLng(location.getLongitude());
                userLocation.setRunnerId(Integer.parseInt(runnerId));
                updateUserLocationRequest.setUserLocation(userLocation);

                Response<UpdateUserLocationResponse> restResponse = client.getApiService().updateUserLocation(fbId, authToken, updateUserLocationRequest).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling update location api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling update location api failed, , response code: "+restResponse.code());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to update location.",e);
            }
            return null;
        }
    }


}
