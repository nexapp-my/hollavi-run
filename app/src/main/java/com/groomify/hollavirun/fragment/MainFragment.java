package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.groomify.hollavirun.LatestUpdateActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.utils.AppPermissionHelper;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.view.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback {

    private final static int MAX_LATEST_NEWS_CONTENT = 50;

    private final static String TAG = MainFragment.class.getSimpleName();

    public static ProfilePictureView profilePictureView;

    /*private RecyclerView horizonRecyclerView;
    private ArrayList<MissionCard> missionCards;
    private HorizontalAdapter horizontalAdapter;*/

    public static View mainFragment;
    //public boolean isFirstTimeInitialized = true;

    private View latestNewsFloatPane = null;
    private TextView latestNewsTitle= null;
    private TextView latestNewsDesc= null;
    private TextView latestNewsTimestamp = null;
    private ImageView latestNewsCover = null;

    public static EditText editText;
    public static MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private Context context;
    private FragmentActivity fragmentActivity;

    //6.718263, 100.206579
    //1.257542, 103.897877
    private LatLngBounds MALAYSIA = new LatLngBounds(
            new LatLng(1.2, 100),new LatLng(7.0, 104.3));


    public static GoogleMap googleMap = null;

    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        fragmentActivity = this.getActivity();

        Realm.init(this.getContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        // Create global configuration and initialize ImageLoader with this config

        ImageLoadUtils.initImageLoader(this.getContext());

       /* if(!isFirstTimeInitialized){
            return;
        }*/


        //Log.i(TAG, "Inside the main fragment!!!!");

        /*NewsFeed[] newsFeeds = {
                new NewsFeed("Accident reported at section 13", "Get the latest news on the run right here.", "4 mins ago"),
                new NewsFeed("Well done guys!", "It was a beautiful run today. Thanks for your participation and till we meet again.", "4 mins ago"),
                new NewsFeed("King of the road!", "Congratulations. You have done this.", "1 day ago"),
                new NewsFeed("Neque porro quisquam est qui dolorem ipsum ", "Cras fermentum dolor et nisl tincidunt, in molestie nisl pellentesque. Duis dictum laoreet elit", "1 day ago"),
                new NewsFeed("quia dolor sit amet, consectetur", "Duis id tincidunt velit. Sed gravida diam nibh, et egestas sem porttitor et.", "1 day ago"),
                new NewsFeed("adipisci velit...", "Donec ut sapien enim. Fusce non tristique odio.", "2 days ago"),
                new NewsFeed("Nam eu pulvinar velit. Mauris ac nisl qua", "Morbi pellentesque nisi vel sollicitudin dapibus.", "1 day ago")
        };
        setListAdapter(new NewsFeedArrayAdapter(this.getContext(), newsFeeds));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        missionCards = new ArrayList<MissionCard>();
        missionCards.add(new MissionCard("01", "SELFIE WITH STRANGERS", null));
        missionCards.add(new MissionCard("02", "POKEMON GO", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.pikachu_resize, 128, 128)));
        missionCards.add(new MissionCard("03", "THE 3D JOURNEY", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.crysis_cover, 128, 128)));
        missionCards.add(new MissionCard("04", "INFLATABLE CASTLE", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mission_sample_cod, 128, 128)));
        missionCards.add(new MissionCard("05", "DON't TEXT & DRIVE", BitmapUtils.decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mission_sample_ffantasy, 128, 128)));
        missionCards.add(new MissionCard("06", "UPSIDE DOWN WORLD", null));
*/
        //isFirstTimeInitialized = false;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainFragment = inflater.inflate(R.layout.fragment_main, container, false);

        if (editText == null) {
            editText = (EditText) mainFragment.findViewById(R.id.search_runner_field);
        }
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) v;
                editText.setText("");
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

    private void launchLatestUpdateScreen(){
        Intent intent = new Intent(context, LatestUpdateActivity.class);
        startActivity(intent);
    }


    private void initializeNewsView(){

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
        }


       /* // ... boilerplate omitted for brevity
        realm = Realm.getDefaultInstance();
        // get all the customers
        RealmResults<Customer> customers = realm.where(Customer.class).findAllAsync();
        // ... build a list adapter and set it to the ListView/RecyclerView/etc

        // set up a Realm change listener
        changeListener = new RealmChangeListener() {
            @Override
            public void onChange(RealmResults<Customer> results) {
                // This is called anytime the Realm database changes on any thread.
                // Please note, change listeners only work on Looper threads.
                // For non-looper threads, you manually have to use Realm.waitForChange() instead.
                listAdapter.notifyDataSetChanged(); // Update the UI
            }
        };
        // Tell Realm to notify our listener when the customers results
        // have changed (items added, removed, updated, anything of the sort).
        customers.addChangeListener(changeListener);*/



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
                            mainFragment.getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

            map.getUiSettings().setZoomGesturesEnabled(true);
            if (!AppPermissionHelper.isLocationPermissionGranted(context)) {
                Log.i(TAG,"Location permission is not granted.");
            }else{
                map.setMyLocationEnabled(true);
            }

            Marker marketMission1;
            Marker marketMission2;
            Marker marketMission3;

            marketMission1 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(3.074028, 101.606791))
                    .title("Mission 1").flat(true).alpha(0.5f).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
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
            marketMission3.setTag(0);
            //3.064059, 101.579973

            PolylineOptions rectOptions = new PolylineOptions()
                    .add(new LatLng(3.073053, 101.597232))
                    .add(new LatLng(3.073107, 101.601963))
                    .add(new LatLng(3.074028, 101.606791))
                    .add(new LatLng(3.074103, 101.609870))
                    .add(new LatLng(3.073972, 101.610781))
                    .add(new LatLng(3.065541, 101.610792))
                    .add(new LatLng(3.065309, 101.593327))
                    .add(new LatLng(3.072339, 101.593151))
                    .add(new LatLng(3.072468, 101.596402))
                    .add(new LatLng(3.073053, 101.597232))
                    .color(ResourcesCompat.getColor(getResources(), R.color.rustyRed, null));// Closes the polyline.

             // Get back the mutable Polyline
            Polyline polyline = map.addPolyline(rectOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(3.068367, 101.602351), 15));

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


}
