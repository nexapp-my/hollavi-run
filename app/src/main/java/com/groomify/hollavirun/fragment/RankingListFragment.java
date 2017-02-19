package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.MissionArrayAdapter;
import com.groomify.hollavirun.adapter.RankingArrayAdapter;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.entities.Ranking;
import com.groomify.hollavirun.utils.AppUtils;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RankingListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RankingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingListFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final static String TAG = RankingListFragment.class.getSimpleName();

    TextView myRankingNoTextView = null;
    TextView myRankingNameTextView = null;
    TextView myRankingUserIdTextView = null;
    TextView myRankingTimeTextView = null;

    private OnFragmentInteractionListener mListener;

    private Realm realm;

    private RealmChangeListener<RealmResults<Ranking>> realmChangeListener;

    private RankingArrayAdapter rankingArrayAdapter;


    Ranking[] rankings = {
           /* new Ranking(1, "Ceric", "10001", "1 m 0 s"),
            new Ranking(2, "Calvin Koh", "10001", "1 m 0 s"),
            new Ranking(3, "CMonz", "10001", "1 m 0 s"),
            new Ranking(4, "Jeffrey", "10001", "1 m 0 s"),
            new Ranking(5, "Ken", "10001", "1 m 0 s")*/

    };

    public RankingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingListFragment newInstance(String param1, String param2) {
        RankingListFragment fragment = new RankingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Realm.init(this.getContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        // ... boilerplate omitted for brevity
        // get all the customers
        RealmResults<Ranking> rankingRealmResults = realm.where(Ranking.class).findAll();

        Log.i(TAG, "All ranking. "+rankingRealmResults.size());
        // ... build a list adapter and set it to the ListView/RecyclerView/etc

        rankings = rankingRealmResults.toArray(new Ranking[0]);

        // set up a Realm change listener
        realmChangeListener = new RealmChangeListener<RealmResults<Ranking>>() {
            @Override
            public void onChange(RealmResults<Ranking> results) {
                // This is called anytime the Realm database changes on any thread.
                // Please note, change listeners only work on Looper threads.
                // For non-looper threads, you manually have to use Realm.waitForChange() instead.
                rankingArrayAdapter.notifyDataSetChanged(); // Update the UI
            }
        };
        // Tell Realm to notify our listener when the customers results
        // have changed (items added, removed, updated, anything of the sort).
        rankingRealmResults.addChangeListener(realmChangeListener);

        rankingArrayAdapter = new RankingArrayAdapter(this.getContext(), rankings);
        setListAdapter(rankingArrayAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);
        View parentView = view.findViewById(R.id.my_profile_ranking);
        myRankingNoTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_no);
        myRankingNameTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_name);
        myRankingUserIdTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_user_id);
        myRankingTimeTextView = (TextView) parentView.findViewById(R.id.item_user_ranking_time);

        initializeMyRanking(parentView);
        return view;
    }

    private void initializeMyRanking(View parentView)
    {
        GroomifyUser groomifyUser = realm.where(GroomifyUser.class).findFirst();

        Ranking ranking = groomifyUser.getMyRanking() ;

        myRankingNoTextView.setText("#"+ranking.getRankNumber()+".");
        myRankingNameTextView.setText(ranking.getName());
        myRankingTimeTextView.setText(ranking.getCompletionTime() == null ? "" : AppUtils.getFormattedTimeFromSeconds(ranking.getCompletionTime()));
        myRankingUserIdTextView.setText("(ID:"+ranking.getId()+")");

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
/*            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
