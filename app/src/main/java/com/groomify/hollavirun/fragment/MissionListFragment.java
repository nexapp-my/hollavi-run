package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MissionListFragment extends ListFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public static View viewInstance;

    Mission[] missions = {
            new Mission("01", "SELFIE WITH STRANGERS", "Take 3 selfies with your fellow runners"),
            new Mission("02", "POKEMON GO", "Spot the real Pikachu"),
            new Mission("03", "THE 3D JOURNEY", "Prepare to be mindblown."),
            new Mission("04", "INFLATABLE CASTLE", "Meet your favourite Superheroes.")

    };

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

        setListAdapter(new MissionArrayAdapter(this.getContext(), missions));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(viewInstance != null){
            return viewInstance;
        }

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
        startActivity(intent);

    }
}
