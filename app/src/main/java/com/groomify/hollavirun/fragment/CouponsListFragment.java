package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.groomify.hollavirun.CouponDetailsActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.CouponArrayAdapter;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Coupon;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CouponsListFragment extends ListFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private final static String TAG = CouponsListFragment.class.getSimpleName();

    //private OnListFragmentInteractionListener mListener;

    public static View viewInstance;


    Coupon[] coupons = null;

    public static SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);

    private Date raceFinishDate;

    public CouponsListFragment() {

    }


    public static CouponsListFragment newInstance(int columnCount) {
        CouponsListFragment fragment = new CouponsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long raceId = SharedPreferencesHelper.getSelectedRaceId(this.getContext());
        String raceEndTime = SharedPreferencesHelper.getRaceExpirationTime(this.getContext(), raceId);

        try {
            raceFinishDate = sdf.parse(raceEndTime);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to parse race expiration date.");
        }

        int oriDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106, getResources().getDisplayMetrics());
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());

        Bitmap tshirt = BitmapUtils.cropBitmap(dimension, dimension, BitmapFactory.decodeResource(getResources(), R.drawable.t_shirt));
        Bitmap goodieBag = BitmapUtils.cropBitmap(dimension, dimension, BitmapFactory.decodeResource(getResources(), R.drawable.goodie_bag));
        Bitmap firstAidKit = BitmapUtils.cropBitmap(dimension, dimension, BitmapFactory.decodeResource(getResources(), R.drawable.first_aid_kit));
        Bitmap milo = BitmapUtils.cropBitmap(dimension, dimension, BitmapFactory.decodeResource(getResources(), R.drawable.milo));

        ByteArrayOutputStream tshirtByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream goodieBagByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream firstAidKitByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream miloByteArr = new ByteArrayOutputStream();

        ByteArrayOutputStream oriTshirtByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream oriGoodieBagByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream oriFirstAidKitByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream oriMiloByteArr = new ByteArrayOutputStream();

        tshirt.compress(Bitmap.CompressFormat.PNG, 50, tshirtByteArr);
        goodieBag.compress(Bitmap.CompressFormat.PNG, 50, goodieBagByteArr);
        firstAidKit.compress(Bitmap.CompressFormat.PNG, 50, firstAidKitByteArr);
        milo.compress(Bitmap.CompressFormat.PNG, 50, miloByteArr);

        BitmapUtils.cropBitmap(oriDimension, oriDimension, BitmapFactory.decodeResource(getResources(), R.drawable.t_shirt)).compress(Bitmap.CompressFormat.PNG, 50, oriTshirtByteArr);
        BitmapUtils.cropBitmap(oriDimension, oriDimension, BitmapFactory.decodeResource(getResources(), R.drawable.goodie_bag)).compress(Bitmap.CompressFormat.PNG, 50, oriGoodieBagByteArr);
        BitmapUtils.cropBitmap(oriDimension, oriDimension, BitmapFactory.decodeResource(getResources(), R.drawable.first_aid_kit)).compress(Bitmap.CompressFormat.PNG, 50, oriFirstAidKitByteArr);
        BitmapUtils.cropBitmap(oriDimension, oriDimension, BitmapFactory.decodeResource(getResources(), R.drawable.milo)).compress(Bitmap.CompressFormat.PNG, 50, oriMiloByteArr);

        coupons = new Coupon[]{
                new Coupon(1, "Free Milo Drink", "Energise yourself", raceFinishDate, miloByteArr.toByteArray(), oriMiloByteArr.toByteArray()),
                new Coupon(2, "Goodie Bag", "Everything is inside", raceFinishDate, goodieBagByteArr.toByteArray(), oriGoodieBagByteArr.toByteArray()),
                new Coupon(3, "First Aid Kit", "Help yourself", raceFinishDate, firstAidKitByteArr.toByteArray(), oriFirstAidKitByteArr.toByteArray()),
                new Coupon(4, "Groomify T-Shirt", "Signature T-Shirt", raceFinishDate, tshirtByteArr.toByteArray(), oriTshirtByteArr.toByteArray())
        };

        super.onCreate(savedInstanceState);

        setListAdapter(new CouponArrayAdapter(this.getContext(), coupons));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coupons_list, container, false);
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
        bundle.putParcelable("COUPON", coupons[position]);
        intent.putExtra("EXTRA_COUPON", bundle);
        startActivity(intent);

    }
}
