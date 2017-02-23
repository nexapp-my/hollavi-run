package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.groomify.hollavirun.MissionDetailsActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.MissionArrayAdapter;
import com.groomify.hollavirun.adapter.MyMissionRecyclerViewAdapter;
import com.groomify.hollavirun.adapter.NewsFeedArrayAdapter;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.fragment.dummy.MissionContent;
import com.groomify.hollavirun.fragment.dummy.MissionContent.MissionItem;
import com.groomify.hollavirun.utils.AppUtils;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MissionListFragment extends ListFragment {

    private final String TAG = MissionListFragment.class.getSimpleName();
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public static View viewInstance;

    Mission[] missions = {
    };
    /*
    new Mission(1, "01", "SELFIE WITH STRANGERS", "Take 3 selfies with your fellow runners", R.drawable.mission_banner_01, true),
            new Mission(2, "02", "POKEMON GO", "Spot the real Pikachu", R.drawable.mission_banner_02, false),
            new Mission(3, "03", "DONT'T TEXT & DRIVE", "Prepare to be mindblown.", R.drawable.mission_banner_03, false),
            new Mission(4, "04", "INFLATABLE CASTLE", "Meet your favourite Superheroes.", R.drawable.mission_banner_04, false),
            new Mission(5, "05", "THE 3D JOURNEY", "Meet your favourite Superheroes.", R.drawable.mission_banner_05, false),
            new Mission(6, "06", "UPSIDE DOWN WORLD", "Meet your favourite Superheroes.", R.drawable.mission_banner_06, false)
     */

    MissionArrayAdapter missionArrayAdapter;
    RealmChangeListener<RealmResults<Mission>> realmChangeListener;

    private Realm realm;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MissionListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MissionListFragment newInstance(int columnCount) {
        MissionListFragment fragment = new MissionListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        Realm.init(this.getContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        RealmResults<Mission> missionRealmResults = realm.where(Mission.class).findAll();

        Log.i(TAG, "All missions. "+missionRealmResults.size());

        //missions = missionRealmResults.toArray(new Mission[0]);
        missions = AppUtils.getDefaultMission();

        realmChangeListener = new RealmChangeListener<RealmResults<Mission>>() {
            @Override
            public void onChange(RealmResults<Mission> results) {
                // This is called anytime the Realm database changes on any thread.
                // Please note, change listeners only work on Looper threads.
                // For non-looper threads, you manually have to use Realm.waitForChange() instead.
                missionArrayAdapter.notifyDataSetChanged(); // Update the UI
            }
        };
        // Tell Realm to notify our listener when the customers results
        // have changed (items added, removed, updated, anything of the sort).
        missionRealmResults.addChangeListener(realmChangeListener);

        missionArrayAdapter = new MissionArrayAdapter(this.getContext(), missions);
        //setListAdapter(new MissionArrayAdapter(this.getContext(), missions));
        setListAdapter(missionArrayAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*if(viewInstance != null){
            return viewInstance;
        }*/

        View view = inflater.inflate(R.layout.fragment_mission_list, container, false);

        // Set the adapter
        /*if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyMissionRecyclerViewAdapter(MissionContent.ITEMS, mListener));
        }*/


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MissionItem item);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getContext(),"Clicked on mission "+position, Toast.LENGTH_SHORT );

        Intent intent = new Intent(getContext(), MissionDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("MISSION", missions[position]);
        intent.putExtra("EXTRA_MISSION", bundle);
        startActivity(intent);

    }
}
