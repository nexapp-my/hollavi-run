package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.CouponDetailsActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.CouponArrayAdapter;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Coupon;
import com.groomify.hollavirun.entities.GroomifyUser;
import com.groomify.hollavirun.entities.Ranking;
import com.groomify.hollavirun.rest.RestClient;
import com.groomify.hollavirun.rest.models.response.CouponsResponse;
import com.groomify.hollavirun.rest.models.response.RaceRankingResponse;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.RealmUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;


public class CouponsListFragment extends ListFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private final static String TAG = CouponsListFragment.class.getSimpleName();

    private ProgressBar loadingProgress ;
    private TextView listEmptryTextView;


    List<Coupon> coupons = new ArrayList<>();

    public static SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);

    private Date raceFinishDate;

    private Realm realm;
    private RestClient client = new RestClient();

    private  CouponArrayAdapter couponArrayAdapter;
    private Long raceId;

    public CouponsListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        raceId = SharedPreferencesHelper.getSelectedRaceId(this.getContext());
        String raceEndTime = SharedPreferencesHelper.getRaceExpirationTime(this.getContext(), raceId);

        try {
            raceFinishDate = sdf.parse(raceEndTime);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to parse race expiration date.");
        }

        Realm.init(this.getContext());
        realm = Realm.getInstance(RealmUtils.getRealmConfiguration());

       /* coupons = new Coupon[]{
                new Coupon(1, "Free Milo Drink", "Energise yourself", raceFinishDate, false,  R.drawable.milo),
                new Coupon(2, "Goodie Bag", "Everything is inside", raceFinishDate, false, R.drawable.goodie_bag),
                new Coupon(3, "First Aid Kit", "Help yourself", raceFinishDate, false, R.drawable.first_aid_kit),
                new Coupon(4, "Groomify T-Shirt", "Signature T-Shirt", raceFinishDate, false, R.drawable.t_shirt)
        };*/

        couponArrayAdapter = new CouponArrayAdapter(this.getContext(), coupons);
        setListAdapter(couponArrayAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coupons_list, container, false);
        loadingProgress = (ProgressBar) view.findViewById(R.id.coupon_loading_progress);
        if(coupons.size() == 0){
            loadingProgress.setVisibility(View.VISIBLE);
        }

        listEmptryTextView = (TextView) view.findViewById(R.id.list_empty_text_view);
        listEmptryTextView.setVisibility(View.INVISIBLE);
        new GroomifyCouponListTask().execute(raceId.toString());

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getContext(),"Clicked on coupon "+position, Toast.LENGTH_SHORT );

        Intent intent = new Intent(getContext(), CouponDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("COUPON", coupons.get(position));
        intent.putExtra("EXTRA_COUPON", bundle);
        startActivity(intent);

    }




    private class GroomifyCouponListTask extends AsyncTask<String, Void, CouponsResponse> {

        @Override
        protected CouponsResponse doInBackground(String... params) {
            String authToken = SharedPreferencesHelper.getAuthToken(getContext());
            String fbId = SharedPreferencesHelper.getFbId(getContext());
            try {
                Response<CouponsResponse> restResponse = client.getApiService().getCoupons(fbId, authToken, params[0]).execute();

                if(restResponse.isSuccessful()){
                    Log.i(TAG, "Calling race coupon api success");
                    return restResponse.body();
                }else{
                    Log.i(TAG, "Calling race coupon api failed, race id: "+params[0]+", response code: "+restResponse.code()+", error body: "+restResponse.errorBody().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to get race coupon.",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final CouponsResponse couponsResponse) {
            super.onPostExecute(couponsResponse);

            if(couponsResponse != null){

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Coupon.class);
                        for(com.groomify.hollavirun.rest.models.response.Coupon coupon: couponsResponse.getCoupons()){
                            Coupon realmCoupon = realm.createObject(Coupon.class, coupon.getId());
                            realmCoupon.setDescription(coupon.getDescription());
                            realmCoupon.setCoverPhotoUrl(coupon.getCover().getUrl());
                            realmCoupon.setExpirationTime(raceFinishDate);
                            realmCoupon.setName(coupon.getTitle());
                            realmCoupon.setRedeemed(false);
                            realmCoupon.setResourceId(-1);
                            realm.copyToRealmOrUpdate(realmCoupon);
                        }
                    }
                });

            }else{
                //Toast.makeText(getContext(), "Unable to get race ranking at this moment.", Toast.LENGTH_SHORT).show();
            }

            reloadCouponList();
        }
    }

    private void reloadCouponList(){
        loadingProgress.setVisibility(View.GONE);
        RealmResults<Coupon> couponRealmResults = realm.where(Coupon.class).findAll();

        coupons.clear();
        couponArrayAdapter.clear();
        coupons.addAll(couponRealmResults);
        couponArrayAdapter.notifyDataSetChanged();

        if(coupons.size() == 0){
            listEmptryTextView.setVisibility(View.VISIBLE);
        }


    }
}
