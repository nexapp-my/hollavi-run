package com.groomify.hollavirun.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.groomify.hollavirun.MainActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.NewsFeedArrayAdapter;
import com.groomify.hollavirun.entities.MissionCard;
import com.groomify.hollavirun.entities.NewsFeed;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.view.ProfilePictureView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback {

    public static ProfilePictureView profilePictureView;

    private final static String TAG = MainFragment.class.getSimpleName();

    /*private RecyclerView horizonRecyclerView;
    private ArrayList<MissionCard> missionCards;
    private HorizontalAdapter horizontalAdapter;*/

    public static View mainFragment;
    //public boolean isFirstTimeInitialized = true;

    public static EditText editText;


    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //6.718263, 100.206579
    //1.257542, 103.897877
    private LatLngBounds MALAYSIA = new LatLngBounds(
            new LatLng(1.2, 100),new LatLng(7.0, 104.3));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



       /* if(!isFirstTimeInitialized){
            return;
        }*/


        Log.i(TAG, "Inside the main fragment!!!!");

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

        /*if(mainFragment != null){
            return mainFragment;
        }*/
        // Inflate the layout for this fragment
        mainFragment = inflater.inflate(R.layout.fragment_main, container, false);


        if (editText == null) {
            editText = (EditText) mainFragment.findViewById(R.id.search_runner_field);
        }
        /*editText.setOnFocusChangeListener(
                new EditText.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View v, boolean hasFocus) {
                          EditText editText = (EditText) v;
                          if(hasFocus){
                              editText.setText("");
                          }else{
                              editText.setText("Search Runner ID");
                          }
                      }
                  }
        );*/


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) mainFragment.findViewById(R.id.map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        return mainFragment;

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
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.



            // Constrain the camera target to the Adelaide bounds.
            map.setLatLngBoundsForCameraTarget(MALAYSIA);

            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            mainFragment.getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

            map.getUiSettings().setZoomGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(mainFragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mainFragment.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG,"Location permission is not granted.");
                return;
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

                    //3.073972, 101.610781
                    //3.065541, 101.610792
                    //3.065309, 101.593327
                    .color(ResourcesCompat.getColor(getResources(), R.color.rustyRed, null));// Closes the polyline.

             // Get back the mutable Polyline
            Polyline polyline = map.addPolyline(rectOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(3.068367, 101.602351), 15));

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }catch (Exception ex){
            Log.e(TAG, "Failed to instantiate google map.");
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
